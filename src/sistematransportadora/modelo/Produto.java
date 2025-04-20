package sistematransportadora.modelo;

public class Produto {
    private final int id;
    private String nome;
    private String descricao;
    private String familia;
    private ProdutoTipo tipo;
    private String lote;
    private Dimensoes dimensoes;
    private double peso;
    private Fragilidade grauFragilidade;
    private String observacoes;

    public Produto(int id, String nome, ProdutoTipo tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public ProdutoTipo getTipo() {
        return tipo;
    }

    public void setTipo(ProdutoTipo tipo) {
        this.tipo = tipo;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
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
        this.peso = peso;
    }

    public Fragilidade getGrauFragilidade() {
        return grauFragilidade;
    }

    public void setGrauFragilidade(Fragilidade grauFragilidade) {
        this.grauFragilidade = grauFragilidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
