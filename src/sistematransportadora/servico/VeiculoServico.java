package sistematransportadora.servico;

import sistematransportadora.modelo.Veiculo;
import sistematransportadora.modelo.VeiculoTipo;
import sistematransportadora.repositorio.VeiculoRepositorio;

/**
 * Serviço que faz validação e teste das regras de negócio antes de
 * fazer uma alteração no banco de dados.
 */
public class VeiculoServico {
    private final VeiculoRepositorio veiculoRepositorio;

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
    
    /**
     * Busca por uma id válida de tipo de veículo, ou seja, que não esteja em uso
     * no banco de dados.
     * @return {@code int}
     */
    public int buscarIdValidaParaTipo() {
        var tiposCadastrados = veiculoRepositorio.contarTipos();
        int i = tiposCadastrados + 1;
        
        while (true) {
            if (!veiculoRepositorio.existeTipoId(i))
                break;
            i++;
        }
        
        return i;
    }

    /**
     * Cadastra um novo tipo de veículo na base de dados.
     * @param tipoVeiculo O novo tipo a ser cadastrado.
     * @throws IdJaExisteException Se a id do tipo já estiver cadastrada.
     * @throws ValorInvalidoException Se o nome for muito curto ou vazio.
     */
    public void cadastrarTipo(VeiculoTipo tipoVeiculo) {

        if (veiculoRepositorio.existeTipoId(tipoVeiculo.id())) {
            throw new IdJaExisteException("Id de tipo de veículo já existe no banco de dados.");
        }
        
        if (tipoVeiculo.nome().isBlank() || tipoVeiculo.nome().trim().length() < 3) {
            throw new ValorInvalidoException("Nome inválido! O nome precisa ter no mínimo 3 caracteres");
        }
        
        // Impossibilita a criação de um tipo "padrão", porém não deixa de o criar.
        if (tipoVeiculo.isPadrao()) {
            var novoTipo = new VeiculoTipo(tipoVeiculo.id(), tipoVeiculo.nome(), false);
            veiculoRepositorio.criarNovoTipo(novoTipo);
            
            return;
        }
        
        veiculoRepositorio.criarNovoTipo(tipoVeiculo);
    }
    
    /**
     * Apaga o tipo com base na id.
     * @param id A id do tipo.
     * @throws IdNaoExisteException Se a id do tipo não existir no banco.
     * @throws ValorInvalidoException Se o tipo for padrão da aplicação.
     */
    public void apagarTipoPorId(int id) {
        if (!veiculoRepositorio.existeTipoId(id)) {
            throw new IdNaoExisteException("Id de tipo de veículo não existe no banco de dados.");
        }
        
        if (veiculoRepositorio.buscarTipoPorId(id).isPadrao()) {
            throw new ValorInvalidoException("Um tipo padrão não deve ser apagado!");
        }
        
        veiculoRepositorio.apagarTipo(id);
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
