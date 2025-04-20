package sistematransportadora.modelo;

public class Embalagem {
    private final int id;
    private Dimensoes dimensoes;
    private double peso;
    private boolean empilhavel = false;
    private String observacoes;
    private EmbalagemTipo tipo;
    private Produto produtoAssociado;

    public Embalagem(int id, EmbalagemTipo tipo, Produto produtoAssociado, Dimensoes dimensoes, double peso) {
        this.id = id;
        this.tipo = tipo;
        this.produtoAssociado = produtoAssociado;
        this.dimensoes = dimensoes;
        this.peso = peso < 0 ? 0 : peso;

    }

    public int getId() {
        return id;
    }

    public Dimensoes getDimensoes() {
        return dimensoes;
    }

    public void setDimensoes(Dimensoes dimensoes) {
        this.dimensoes = dimensoes;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso < 0 ? 0 : peso;
    }

    public boolean isEmpilhavel() {
        return empilhavel;
    }

    public void setEmpilhavel(boolean empilhavel) {
        this.empilhavel = empilhavel;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public EmbalagemTipo getTipo() {
        return tipo;
    }

    public void setTipo(EmbalagemTipo tipo) {
        this.tipo = tipo;
    }

    public Produto getProdutoAssociado() {
        return produtoAssociado;
    }

    public void setProdutoAssociado(Produto produtoAssociado) {
        this.produtoAssociado = produtoAssociado;
    }
}
