package sistematransportadora.servico;

import sistematransportadora.modelo.Embalagem;
import sistematransportadora.modelo.EmbalagemTipo;
import sistematransportadora.repositorio.EmbalagemRepositorio;
import sistematransportadora.repositorio.ProdutoRepositorio;

/**
 * Serviço que faz validação e teste das regras de negócio antes de
 * fazer uma alteração no banco de dados.
 */
public class EmbalagemServico {
    private final EmbalagemRepositorio embalagemRepositorio;
    private final ProdutoRepositorio produtoRepositorio;

    public EmbalagemServico() {
        this.embalagemRepositorio = new EmbalagemRepositorio();
        produtoRepositorio = new ProdutoRepositorio();
    }

    /**
     * Cadastra uma nova embalagem.
     * @param novoEmbalagem Embalagem a ser cadastrada.
     * @throws IdJaExisteException Se a id da embalagem já estiver cadastrada.
     * @throws ValorInvalidoException Se no mínimo um atributo não estiver conforme as regras de negócio.
     */
    public void cadastrarEmbalagem(Embalagem novoEmbalagem) {
        checarValidezCadastro(novoEmbalagem, false);

        embalagemRepositorio.criarNovo(novoEmbalagem);
    }

    /**
     * Busca uma embalagem com base na id e a retorna.
     * @param id Id da embalagem.
     * @return {@link Embalagem}
     * @throws IdNaoExisteException Se não encontrar por uma embalagem
     * com id correspondente.
     */
    public Embalagem buscarPorId(int id) {
        var embalagem = embalagemRepositorio.buscarPorId(id);
        if (embalagem == null) {
            throw new IdNaoExisteException(String.format(
                    "Não foi possível buscar por uma embalagem com id %d", id
                    ));
        }

        return embalagem;
    }

    /**
     * Retorna todas as embalagens cadastradas.
     * @return {@code Embalagem[]}
     */
    public Embalagem[] buscarTodas() {
        return embalagemRepositorio.buscarTodos();
    }

    /**
     * Edita uma embalagem já cadastrada com base na id.
     * @param embalagem Embalagem a ser salva.
     * @throws IdNaoExisteException Se a id da embalagem não estiver cadastrada.
     * @throws ValorInvalidoException Se no mínimo um atributo não estiver conforme as regras de negócio.
     */
    public void editarEmbalagem(Embalagem embalagem) {
        if (!embalagemRepositorio.existeId(embalagem.getId())) {
            throw new IdNaoExisteException(String.format("Não há embalagem com id %d.", embalagem.getId()));
        }
        checarValidezCadastro(embalagem, true);

        embalagemRepositorio.atualizarUm(embalagem);
    }

    /**
     * Exclui uma embalagem cadastrado com base na id.
     * @param id Id da embalagem a ser apagado.
     * @throws IdNaoExisteException Se a id da embalagem não estiver cadastrada.
     */
    public void excluirEmbalagem(int id) {
        if (!embalagemRepositorio.existeId(id)) {
            throw new IdNaoExisteException(String.format("Não há embalagem com id %d.", id));
        }
        embalagemRepositorio.apagarPorId(id);
    }

    /**
     * Busca por uma id válida de tipo de embalagem, ou seja, que não esteja em uso
     * no banco de dados.
     * @return {@code int}
     */
    public int buscarIdValidaParaTipo() {
        var tiposCadastrados = embalagemRepositorio.contarTipos();
        int i = tiposCadastrados + 1;
        
        while (true) {
            if (!embalagemRepositorio.existeTipoId(i))
                break;
            i++;
        }
        
        return i;
    }

    /**
     * Cadastra um novo tipo de embalagem na base de dados.
     * @param tipoEmbalagem O novo tipo a ser cadastrado.
     * @throws IdJaExisteException Se a id do tipo já estiver cadastrada.
     * @throws ValorInvalidoException Se o nome for muito curto ou vazio.
     */
    public void cadastrarTipo(EmbalagemTipo tipoEmbalagem) {

        if (embalagemRepositorio.existeTipoId(tipoEmbalagem.id())) {
            throw new IdJaExisteException("Id de tipo de embalagem já existe no banco de dados.");
        }
        
        if (tipoEmbalagem.nome().isBlank() || tipoEmbalagem.nome().trim().length() < 3) {
            throw new ValorInvalidoException("Nome inválido! O nome precisa ter no mínimo 3 caracteres");
        }
        
        // Impossibilita a criação de um tipo "padrão", porém não deixa de o criar.
        if (tipoEmbalagem.isPadrao()) {
            var novoTipo = new EmbalagemTipo(tipoEmbalagem.id(), tipoEmbalagem.nome(), false);
            embalagemRepositorio.criarNovoTipo(novoTipo);
            
            return;
        }
        
        embalagemRepositorio.criarNovoTipo(tipoEmbalagem);
    }
    
    /**
     * Apaga o tipo com base na id.
     * @param id A id do tipo.
     * @throws IdNaoExisteException Se a id do tipo não existir no banco.
     * @throws ValorInvalidoException Se o tipo for padrão da aplicação.
     */
    public void apagarTipoPorId(int id) {
        if (!embalagemRepositorio.existeTipoId(id)) {
            throw new IdNaoExisteException("Id de tipo de embalagem não existe no banco de dados.");
        }
        
        if (embalagemRepositorio.buscarTipoPorId(id).isPadrao()) {
            throw new ValorInvalidoException("Um tipo padrão não deve ser apagado!");
        }
        
        embalagemRepositorio.apagarTipo(id);
    }
    
    private void checarValidezCadastro(Embalagem e, boolean updateMode) throws IdJaExisteException, ValorInvalidoException {
        if (embalagemRepositorio.existeId(e.getId()) && !updateMode) {
            throw new IdJaExisteException("Id de embalagem já existe no banco de dados.");
        }

        if (embalagemRepositorio.buscarTipoPorId(e.getTipo().id()) == null) {
            throw new ValorInvalidoException("Tipo de embalagem inválido!");
        }

        if (!e.getDimensoes().isValida()) {
            throw new ValorInvalidoException("Dimensões de embalagem inválidas!");
        }

        if (e.getPeso() < 0) {
            throw new ValorInvalidoException("Peso não pode ser negativo!");
        }

        if (!produtoRepositorio.existeId(e.getProdutoAssociado().getId())) {
            throw new ValorInvalidoException(String.format(
                    "Não há produto cadastrado com id %d.", e.getProdutoAssociado().getId()
                    ));
        }
    }
}

