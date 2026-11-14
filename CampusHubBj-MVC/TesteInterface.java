import controller.UsuarioController;
import model.Database;
import model.UsuarioRepository;
import view.UsuarioView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Integração JavaFX + controlador + ORM em arquivo temporário. */
public class TesteInterface {
    private static Button botao(Parent parent, String texto) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Button && texto.equals(((Button) node).getText())) {
                return (Button) node;
            }
            if (node instanceof Parent) {
                Button result = botao((Parent) node, texto);
                if (result != null) return result;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Path arquivo = Files.createTempFile("campushub-interface-", ".db");
        CountDownLatch terminado = new CountDownLatch(1);
        AtomicReference<Throwable> falha = new AtomicReference<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            Database db = new Database(arquivo.toString());
            Stage stage = new Stage();
            try {
                UsuarioView view = new UsuarioView();
                UsuarioController controller = new UsuarioController(new UsuarioRepository(db), view);
                stage.setScene(new Scene(view.getRaiz(), 860, 560));
                stage.show();
                controller.carregarTodos();
                if (!view.getDados().isEmpty()) throw new AssertionError("Banco deveria estar vazio");
                view.getCampoNome().setText("  Estudante de Teste  ");
                view.getCampoCurso().setText("Computação");
                view.getCampoUniversidade().setText("UFG");
                botao(view.getRaiz(), "Cadastrar").fire();
                if (view.getTabela().getItems().size() != 1) throw new AssertionError("Tabela não atualizada");
                if (!"Estudante de Teste".equals(view.getDados().get(0).getNome())) {
                    throw new AssertionError("Nome incorreto");
                }
                if (!view.getCampoNome().getText().isEmpty()) throw new AssertionError("Formulário não limpo");
                db.close();
                db = new Database(arquivo.toString());
                UsuarioView reaberta = new UsuarioView();
                new UsuarioController(new UsuarioRepository(db), reaberta).carregarTodos();
                if (reaberta.getDados().size() != 1) throw new AssertionError("Registro não persistiu");
                reaberta.getCampoBuscaId().setText("999999");
                botao(reaberta.getRaiz(), "Buscar por ID").fire();
                if (!reaberta.getDados().isEmpty()) throw new AssertionError("Busca inexistente manteve linhas antigas");
                botao(reaberta.getRaiz(), "Listar todos").fire();
                if (reaberta.getDados().size() != 1) throw new AssertionError("Listar todos não restaurou a tabela");
                System.out.println("TESTE JAVAFX APROVADO: cadastro, tabela, limpeza, reabertura e busca.");
            } catch (Throwable error) {
                falha.set(error);
            } finally {
                stage.close();
                db.close();
                terminado.countDown();
            }
        });
        boolean concluiu = terminado.await(45, TimeUnit.SECONDS);
        Platform.exit();
        if (!concluiu) throw new AssertionError("Tempo limite no teste JavaFX");
        Files.deleteIfExists(arquivo);
        if (falha.get() != null) throw new AssertionError("Falha na integração JavaFX", falha.get());
    }
}
