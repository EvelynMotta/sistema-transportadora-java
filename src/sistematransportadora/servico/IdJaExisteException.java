package sistematransportadora.servico;

public class IdJaExisteException extends RuntimeException {
    public IdJaExisteException(String mensagem) {
        super(mensagem);
    }
}
