package exceptions;

/** Exceção de negócio utilizada pelo gerenciamento de usuários em memória. */
public class DadosInvalidosException extends Exception {
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
