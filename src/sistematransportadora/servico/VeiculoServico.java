package sistematransportadora.servico;

import sistematransportadora.modelo.Veiculo;
import sistematransportadora.repositorio.VeiculoRepositorio;

/**
 * Serviço que faz validação e teste das regras de negócio antes de
 * fazer uma alteração no banco de dados.
 */
public class VeiculoServico {
    private final VeiculoRepositorio veiculoRepositorio;

    // TODO: Implementar métodos para os tipos.

    public VeiculoServico() {
        this.veiculoRepositorio = new VeiculoRepositorio();
    }

    /**
     * Cadastra um novo veículo.
     * @param novoVeiculo Veículo a ser cadastrado.
     * @throws IdJaExisteException Se a id do veículo já estiver cadastrada.
     * @throws ValorInvalidoException Se no mínimo um atributo não estiver conforme as regras de negócio.
     */
    public void cadastrarVeiculo(Veiculo novoVeiculo) {
        checarValidezCadastro(novoVeiculo, false);

        veiculoRepositorio.criarNovo(novoVeiculo);
    }

    /**
     * Busca um veículo com base na id e o retorna.
     * @param id Id do veículo.
     * @return {@link Veiculo}
     * @throws IdNaoExisteException Se não encontrar por um veículo
     * com id correspondente.
     */
    public Veiculo buscarPorId(int id) {
        var veiculo = veiculoRepositorio.buscarPorId(id);
        if (veiculo == null) {
            throw new IdNaoExisteException(String.format(
                    "Não foi possível buscar por um veiculo com id %d", id
            ));
        }

        return veiculo;
    }

    /**
     * Retorna todos os veículos cadastrados.
     * @return {@code Veiculo[]}
     */
    public Veiculo[] buscarTodos() {
        return veiculoRepositorio.buscarTodos();
    }
    
    /**
     * Edita um veículo já cadastrado com base na id.
     * @param veiculo Veículo a ser salvo.
     * @throws IdNaoExisteException Se a id do veículo não estiver cadastrada.
     * @throws ValorInvalidoException Se no mínimo um atributo não estiver conforme as regras de negócio.
     */
    public void editarVeiculo(Veiculo veiculo) {
        if (!veiculoRepositorio.existeId(veiculo.getId())) {
            throw new IdNaoExisteException(String.format("Não há veículo com id %d.", veiculo.getId()));
        }
        checarValidezCadastro(veiculo, true);

        veiculoRepositorio.atualizarUm(veiculo);
    }

    /**
     * Exclui um veículo cadastrado com base na id.
     * @param id Id do veículo a ser apagado.
     * @throws IdNaoExisteException Se a id do veículo não estiver cadastrada.
     */
    public void excluirVeiculo(int id) {
        if (!veiculoRepositorio.existeId(id)) {
            throw new IdNaoExisteException(String.format("Não há veículo com id %d.", id));
        }
        veiculoRepositorio.apagarPorId(id);
    }

    private void checarValidezCadastro(Veiculo v, boolean updateMode) throws IdJaExisteException, ValorInvalidoException {
        if (veiculoRepositorio.existeId(v.getId()) && !updateMode) {
            throw new IdJaExisteException("Id de veículo já existe no banco de dados.");
        }

        if (v.getNome().isBlank() || v.getNome().trim().length() < 3) {
            throw new ValorInvalidoException("Nome inválido! O nome precisa ter no mínimo 3 caracteres");
        }

        if (v.getPlaca().isBlank() || v.getPlaca().trim().length() < 7) {
            throw new ValorInvalidoException("Placa inválida! Ela precisa ter no mínimo 7 caracteres");
        }

        if (veiculoRepositorio.buscarPorPlaca(v.getPlaca()) != null && !updateMode) {
            throw new ValorInvalidoException("Placa inválida! Já existe uma mesma placa cadastrada no banco de dados.");
        }

        if (veiculoRepositorio.buscarTipoPorId(v.getTipo().id()) == null) {
            throw new ValorInvalidoException(
                    "Tipo de veículo inválido! Não foi possível encontrar um tipo cadastrado com id correspondente."
            );
        }

        if (v.getCapacidadeDePeso() < 0) {
            throw new ValorInvalidoException("Capacidade de peso não pode ser negativa!");
        }
    }
}
