package exceptions;

/** Exceção de negócio utilizada pelo gerenciamento de usuários em memória. */
public class CadastroDuplicadoException extends Exception {
    public CadastroDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
