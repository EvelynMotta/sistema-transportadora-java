package sistematransportadora.modelo;

public class Dimensoes {
    public double altura;
    public double largura;
    public double comprimento;

    public Dimensoes(double altura, double largura, double comprimento) {
        this.altura = altura;
        this.largura = largura;
        this.comprimento = comprimento;
    }

    public boolean isValida() {
        return !(this.altura < 0 || this.largura < 0 || this.comprimento < 0);
    }
}
