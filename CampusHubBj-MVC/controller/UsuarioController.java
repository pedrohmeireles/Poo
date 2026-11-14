package controller;

import model.Usuario;
import model.UsuarioRepository;
import view.UsuarioView;
import java.sql.SQLException;

/** Controlador: valida entradas, chama o repositório e atualiza a visão. */
public class UsuarioController {
    private final UsuarioRepository repo;
    private final UsuarioView view;

    public UsuarioController(UsuarioRepository repo, UsuarioView view) {
        this.repo = repo;
        this.view = view;
        view.vincularAcoes(this::cadastrar, this::atualizar, this::excluir,
                this::buscarPorId, this::carregarTodos);
    }

    private void atualizarTabela() throws SQLException {
        view.getDados().setAll(repo.listarTodos());
    }

    /** READ: lista todos. */
    public void carregarTodos() {
        try {
            atualizarTabela();
            view.status("Total de usuários: " + view.getDados().size());
        } catch (Exception e) {
            view.erro("Erro ao listar usuários", e);
        }
    }

    /** READ: consulta por ID. */
    private void buscarPorId() {
        String texto = view.getCampoBuscaId().getText().trim();
        if (texto.isEmpty()) {
            view.aviso("Informe o ID para buscar.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(texto);
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            view.aviso("O ID deve ser um número inteiro positivo.");
            return;
        }

        try {
            Usuario encontrado = repo.consultarPorId(id);
            if (encontrado == null) {
                view.getDados().clear();
                view.limparFormulario();
                view.status("Nenhum usuário encontrado com ID " + id + ".");
                return;
            }
            view.getDados().setAll(encontrado);
            view.getTabela().getSelectionModel().selectFirst();
            view.status("Usuário com ID " + id + " encontrado. Use \"Listar todos\" para ver a lista completa.");
        } catch (Exception e) {
            view.erro("Erro ao consultar usuário", e);
        }
    }

    /** CREATE. */
    private void cadastrar() {
        String nome = view.getCampoNome().getText().trim();
        String curso = view.getCampoCurso().getText().trim();
        String universidade = view.getCampoUniversidade().getText().trim();

        if (nome.isEmpty() || curso.isEmpty() || universidade.isEmpty()) {
            view.aviso("Preencha nome, curso e universidade.");
            return;
        }

        try {
            Usuario novo = new Usuario(nome, curso, universidade);
            repo.criar(novo);
            view.getTabela().getSelectionModel().clearSelection();
            view.limparFormulario();
            atualizarTabela();
            view.status("Usuário cadastrado com ID " + novo.getId() + ".");
        } catch (Exception e) {
            view.erro("Erro ao cadastrar usuário", e);
        }
    }

    /** UPDATE. */
    private void atualizar() {
        Usuario selecionado = view.getTabela().getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            view.aviso("Selecione um usuário na view.getTabela() para atualizar.");
            return;
        }

        String nome = view.getCampoNome().getText().trim();
        String curso = view.getCampoCurso().getText().trim();
        String universidade = view.getCampoUniversidade().getText().trim();

        if (nome.isEmpty() || curso.isEmpty() || universidade.isEmpty()) {
            view.aviso("Preencha nome, curso e universidade.");
            return;
        }

        try {
            int id = selecionado.getId();
            Usuario alterado = new Usuario(nome, curso, universidade);
            alterado.setId(id);
            repo.atualizar(alterado);
            atualizarTabela();
            view.status("Usuário com ID " + id + " atualizado.");
        } catch (Exception e) {
            carregarTodos(); // descarta alterações locais que não foram salvas
            view.erro("Erro ao atualizar usuário", e);
        }
    }

    /** DELETE. */
    private void excluir() {
        Usuario selecionado = view.getTabela().getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            view.aviso("Selecione um usuário na view.getTabela() para excluir.");
            return;
        }

        if (!view.confirmarExclusao(selecionado.getNome())) {
            return;
        }

        try {
            int id = selecionado.getId();
            repo.excluirPorId(id);
            view.getTabela().getSelectionModel().clearSelection();
            view.limparFormulario();
            atualizarTabela();
            view.status("Usuário com ID " + id + " excluído.");
        } catch (Exception e) {
            view.erro("Erro ao excluir usuário", e);
        }
    }

}
