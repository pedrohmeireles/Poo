import exceptions.CadastroDuplicadoException;
import exceptions.DadosInvalidosException;
import exceptions.RegistroNaoEncontradoException;
import model.Usuario;
import model.UsuarioColecao;

/** Demonstra as operações e o tratamento de erros no console ou no BlueJ. */
public class TesteUsuarioCollections {
    private static Usuario usuario(int id, String nome) {
        Usuario usuario = new Usuario(nome, "Engenharia de Computação", "UFG");
        usuario.setId(id);
        return usuario;
    }

    private static void listar(UsuarioColecao colecao) {
        for (Usuario usuario : colecao.listarTodos()) {
            System.out.println(usuario.getId() + " - " + usuario.getNome());
            System.out.println("    " + usuario.getCurso() + " | " + usuario.getUniversidade());
        }
        System.out.println("Total de usuários: " + colecao.getQuantidade());
    }

    public static void main(String[] args) {
        UsuarioColecao colecao = new UsuarioColecao();
        System.out.println("CAMPUS HUB - COLLECTIONS E TRATAMENTO DE EXCEÇÕES");
        System.out.println("\n1. CADASTRO, LISTAGEM E BUSCA");
        try {
            colecao.adicionar(usuario(1, "Pedro Henrique Meireles Vieira"));
            colecao.adicionar(usuario(2, "Samuel Melo Moreira Filho"));
            colecao.adicionar(usuario(3, "Henrique Leandro Amorim Ferreira Viana"));
            listar(colecao);
            System.out.println("Busca pelo ID 2: " + colecao.buscarPorId(2).getNome());
        } catch (DadosInvalidosException | CadastroDuplicadoException
                | RegistroNaoEncontradoException erro) {
            System.out.println("Não foi possível concluir a operação: " + erro.getMessage());
        }

        System.out.println("\n2. EXCEÇÕES TRATADAS");
        try {
            colecao.adicionar(usuario(1, "Gustavo Hilbert Polizel"));
        } catch (CadastroDuplicadoException | DadosInvalidosException erro) {
            System.out.println("Cadastro duplicado: " + erro.getMessage());
        }
        try {
            colecao.adicionar(usuario(0, "Gustavo Hilbert Polizel"));
        } catch (DadosInvalidosException | CadastroDuplicadoException erro) {
            System.out.println("Dados inválidos: " + erro.getMessage());
        }
        try {
            colecao.adicionar(usuario(4, " "));
        } catch (DadosInvalidosException | CadastroDuplicadoException erro) {
            System.out.println("Dados inválidos: " + erro.getMessage());
        }
        try {
            colecao.buscarPorId(99);
        } catch (RegistroNaoEncontradoException | DadosInvalidosException erro) {
            System.out.println("Busca inexistente: " + erro.getMessage());
        }
        try {
            colecao.removerPorId(99);
        } catch (RegistroNaoEncontradoException | DadosInvalidosException erro) {
            System.out.println("Remoção inexistente: " + erro.getMessage());
        }
        try {
            Integer.parseInt("abc");
        } catch (NumberFormatException erro) {
            System.out.println("Formato incorreto: informe um número inteiro para o ID.");
        }

        System.out.println("\n3. REMOÇÃO E CONTINUIDADE APÓS OS ERROS");
        try {
            colecao.removerPorId(1);
            System.out.println("Usuário de ID 1 removido.");
            colecao.adicionar(usuario(4, "Gustavo Hilbert Polizel"));
            System.out.println("Novo cadastro após os erros: " + colecao.buscarPorId(4).getNome());
            listar(colecao);
        } catch (DadosInvalidosException | CadastroDuplicadoException
                | RegistroNaoEncontradoException erro) {
            System.out.println("Não foi possível concluir a operação: " + erro.getMessage());
        }
        System.out.println("\nExecução concluída. Os erros foram tratados e o programa continuou.");
    }
}
