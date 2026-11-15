package exceptions;

/** Exceção de negócio utilizada pelo gerenciamento de usuários em memória. */
public class RegistroNaoEncontradoException extends Exception {
    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
