package view;

import model.Usuario;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/** Visão: constrói os controles e apresenta mensagens. */
public class UsuarioView {
    private final ObservableList<Usuario> dados = FXCollections.observableArrayList();
    private TableView<Usuario> tabela;
    private TextField campoNome, campoCurso, campoUniversidade, campoBuscaId;
    private Label status;
    private Runnable cadastrar, atualizar, excluir, buscarPorId, carregarTodos;
    private final BorderPane raiz = new BorderPane();

    public UsuarioView() {
        raiz.setPadding(new Insets(16));
        raiz.setTop(criarTopo());
        raiz.setCenter(criarCentro());
        raiz.setBottom(criarRodape());
    }

    public void vincularAcoes(Runnable cadastrar, Runnable atualizar, Runnable excluir,
            Runnable buscarPorId, Runnable carregarTodos) {
        this.cadastrar = cadastrar;
        this.atualizar = atualizar;
        this.excluir = excluir;
        this.buscarPorId = buscarPorId;
        this.carregarTodos = carregarTodos;
    }

    public BorderPane getRaiz() { return raiz; }
    public TableView<Usuario> getTabela() { return tabela; }
    public ObservableList<Usuario> getDados() { return dados; }
    public TextField getCampoNome() { return campoNome; }
    public TextField getCampoCurso() { return campoCurso; }
    public TextField getCampoUniversidade() { return campoUniversidade; }
    public TextField getCampoBuscaId() { return campoBuscaId; }

    public boolean confirmarExclusao(String nome) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar exclusão");
        alerta.setHeaderText("Excluir o usuário \"" + nome + "\"?");
        alerta.setContentText("Essa ação não pode ser desfeita.");
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    private VBox criarTopo() {
        Label titulo = new Label("Campus Hub");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitulo = new Label("Cadastro de usuários (estudantes da plataforma)");
        subtitulo.setStyle("-fx-text-fill: #555555;");

        VBox topo = new VBox(2, titulo, subtitulo);
        topo.setPadding(new Insets(0, 0, 14, 0));
        return topo;
    }

    private VBox criarCentro() {
        VBox formulario = criarFormulario();
        HBox busca = criarBarraBusca();
        criarTabela();

        VBox centro = new VBox(12, formulario, busca, tabela);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        return centro;
    }

    private VBox criarFormulario() {
        campoNome = new TextField();
        campoNome.setPromptText("Ex.: José Fulano");
        campoCurso = new TextField();
        campoCurso.setPromptText("Ex.: Engenharia de Computação");
        campoUniversidade = new TextField();
        campoUniversidade.setPromptText("Ex.: UFG");

        GridPane grade = new GridPane();
        grade.setHgap(10);
        grade.setVgap(8);
        grade.add(new Label("Nome:"), 0, 0);
        grade.add(campoNome, 1, 0);
        grade.add(new Label("Curso:"), 0, 1);
        grade.add(campoCurso, 1, 1);
        grade.add(new Label("Universidade:"), 0, 2);
        grade.add(campoUniversidade, 1, 2);
        GridPane.setHgrow(campoNome, Priority.ALWAYS);
        GridPane.setHgrow(campoCurso, Priority.ALWAYS);
        GridPane.setHgrow(campoUniversidade, Priority.ALWAYS);

        Button btnCadastrar = new Button("Cadastrar");
        btnCadastrar.setOnAction(e -> cadastrar.run());

        Button btnAtualizar = new Button("Atualizar selecionado");
        btnAtualizar.setOnAction(e -> atualizar.run());

        Button btnExcluir = new Button("Excluir selecionado");
        btnExcluir.setOnAction(e -> excluir.run());

        Button btnLimpar = new Button("Limpar campos");
        btnLimpar.setOnAction(e -> {
            tabela.getSelectionModel().clearSelection();
            limparFormulario();
        });

        HBox botoes = new HBox(8, btnCadastrar, btnAtualizar, btnExcluir, btnLimpar);
        botoes.setAlignment(Pos.CENTER_LEFT);

        VBox caixa = new VBox(10, grade, botoes);
        caixa.setPadding(new Insets(12));
        caixa.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 6; -fx-background-radius: 6;");
        return caixa;
    }

    private HBox criarBarraBusca() {
        campoBuscaId = new TextField();
        campoBuscaId.setPromptText("ID do usuário");
        campoBuscaId.setPrefWidth(140);
        campoBuscaId.setOnAction(e -> buscarPorId.run());

        Button btnBuscar = new Button("Buscar por ID");
        btnBuscar.setOnAction(e -> buscarPorId.run());

        Button btnListar = new Button("Listar todos");
        btnListar.setOnAction(e -> carregarTodos.run());

        HBox barra = new HBox(8, new Label("Consulta:"), campoBuscaId, btnBuscar, btnListar);
        barra.setAlignment(Pos.CENTER_LEFT);
        return barra;
    }

    private void criarTabela() {
        tabela = new TableView<>(dados);
        tabela.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tabela.setPlaceholder(new Label("Nenhum usuário cadastrado."));

        TableColumn<Usuario, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colId.setPrefWidth(70);

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colNome.setPrefWidth(250);

        TableColumn<Usuario, String> colCurso = new TableColumn<>("Curso");
        colCurso.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCurso()));
        colCurso.setPrefWidth(260);

        TableColumn<Usuario, String> colUniversidade = new TableColumn<>("Universidade");
        colUniversidade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUniversidade()));
        colUniversidade.setPrefWidth(200);

        tabela.getColumns().add(colId);
        tabela.getColumns().add(colNome);
        tabela.getColumns().add(colCurso);
        tabela.getColumns().add(colUniversidade);

        // Ao selecionar uma linha, os dados vão para o formulário (para editar).
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, atual) -> {
            if (atual != null) {
                campoNome.setText(atual.getNome());
                campoCurso.setText(atual.getCurso());
                campoUniversidade.setText(atual.getUniversidade());
            }
        });
    }

    private HBox criarRodape() {
        status = new Label("Pronto.");
        status.setStyle("-fx-text-fill: #333333;");
        HBox rodape = new HBox(status);
        rodape.setPadding(new Insets(10, 0, 0, 0));
        return rodape;
    }

    // ------------------------------------------------------------------
    // Operações (ligadas ao UsuarioRepository)
    public void limparFormulario() {
        campoNome.clear();
        campoCurso.clear();
        campoUniversidade.clear();
    }

    public void status(String mensagem) {
        status.setText(mensagem);
    }

    public void aviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    public void erro(String titulo, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(titulo);
        alerta.setContentText(e.getMessage() != null ? e.getMessage() : e.toString());
        alerta.showAndWait();
    }

}
