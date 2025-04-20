package sistematransportadora.modelo;

public class Veiculo {
    private final int id;
    private String nome;
    private String placa;
    private String modelo;
    private VeiculoTipo tipo;
    private Dimensoes dimensoesInternas;
    private double capacidadeDePeso;
    private String observacoes;

    public Veiculo(int id, String nome, VeiculoTipo tipo, String placa, String modelo, double capacidadeDePeso) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.placa = placa;
        this.modelo = modelo;
        this.capacidadeDePeso = capacidadeDePeso;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public VeiculoTipo getTipo() {
        return tipo;
    }

    public void setTipo(VeiculoTipo tipo) {
        this.tipo = tipo;
    }

    public Dimensoes getDimensoesInternas() {
        return dimensoesInternas;
    }

    public void setDimensoesInternas(Dimensoes dimensoesInternas) {
        this.dimensoesInternas = dimensoesInternas;
    }

    public double getCapacidadeDePeso() {
        return capacidadeDePeso;
    }

    public void setCapacidadeDePeso(double capacidadeDePeso) {
        this.capacidadeDePeso = capacidadeDePeso;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
