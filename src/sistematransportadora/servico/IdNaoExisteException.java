package sistematransportadora.servico;

public class IdNaoExisteException extends RuntimeException {
    public IdNaoExisteException(String mensagem) {
        super(mensagem);
    }
}
