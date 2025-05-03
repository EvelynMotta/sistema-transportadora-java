package sistematransportadora.servico;

import sistematransportadora.modelo.Produto;
import sistematransportadora.modelo.ProdutoTipo;
import sistematransportadora.repositorio.ProdutoRepositorio;

/**
 * Serviço que faz validação e teste das regras de negócio antes de
 * fazer uma alteração no banco de dados.
 */
public class ProdutoServico {
    private final ProdutoRepositorio produtoRepositorio;

    public ProdutoServico() {
        this.produtoRepositorio = new ProdutoRepositorio();
    }

    /**
     * Cadastra um novo produto.
     * @param novoProduto Produto a ser cadastrado.
     * @throws IdJaExisteException Se a id do produto já estiver cadastrada.
     * @throws ValorInvalidoException Se no mínimo um atributo não estiver conforme as regras de negócio.
     */
    public void cadastrarProduto(Produto novoProduto) {
        checarValidezCadastro(novoProduto, false);

        produtoRepositorio.criarNovo(novoProduto);
    }

    /**
     * Busca um produto com base na id e o retorna.
     * @param id Id do produto.
     * @return {@link Produto}
     * @throws IdNaoExisteException Se não encontrar por um produto
     * com id correspondente.
     */
    public Produto buscarPorId(int id) {
        var produto = produtoRepositorio.buscarPorId(id);
        if (produto == null) {
            throw new IdNaoExisteException(String.format(
                    "Não foi possível buscar por um produto com id %d", id
            ));
        }

        return produto;
    }

    /**
     * Retorna todos os produtos cadastrados.
     * @return {@code Produto[]}
     */
    public Produto[] buscarTodos() {
        return produtoRepositorio.buscarTodos();
    }
    
    /**
     * Edita um produto já cadastrado com base na id.
     * @param produto Produto a ser salvo.
     * @throws IdNaoExisteException Se a id do produto não estiver cadastrada.
     * @throws ValorInvalidoException Se no mínimo um atributo não estiver conforme as regras de negócio.
     */
    public void editarProduto(Produto produto) {
        if (!produtoRepositorio.existeId(produto.getId())) {
            throw new IdNaoExisteException(String.format("Não há produto com id %d.", produto.getId()));
        }
        checarValidezCadastro(produto, true);

        produtoRepositorio.atualizarUm(produto);
    }

    /**
     * Exclui um produto cadastrado com base na id.
     * @param id Id do produto a ser apagado.
     * @throws IdNaoExisteException Se a id do produto não estiver cadastrada.
     */
    public void excluirProduto(int id) {
        if (!produtoRepositorio.existeId(id)) {
            throw new IdNaoExisteException(String.format("Não há produto com id %d.", id));
        }
        produtoRepositorio.apagarPorId(id);
    }
    
    /**
     * Busca por uma id válida de tipo de produto, ou seja, que não esteja em uso
     * no banco de dados.
     * @return {@code int}
     */
    public int buscarIdValidaParaTipo() {
        var tiposCadastrados = produtoRepositorio.contarPorTipo().size();
        int i = tiposCadastrados;
        
        while (true) {
            if (!produtoRepositorio.existeTipoId(i))
                break;
            i++;
        }
        
        return i;
    }
    
    /**
     * Cadastra um novo tipo de produto na base de dados.
     * @param tipoProduto O novo tipo a ser cadastrado.
     * @throws IdJaExisteException Se a id do tipo já estiver cadastrada.
     * @throws ValorInvalidoException Se o nome for muito curto ou vazio.
     */
    public void cadastrarTipo(ProdutoTipo tipoProduto) {
        if (produtoRepositorio.existeTipoId(tipoProduto.id())) {
            throw new IdJaExisteException("Id de tipo de produto já existe no banco de dados.");
        }
        
        if (tipoProduto.nome().isBlank() || tipoProduto.nome().trim().length() < 3) {
            throw new ValorInvalidoException("Nome inválido! O nome precisa ter no mínimo 3 caracteres");
        }
        
        // Impossibilita a criação de um tipo "padrão", porém não deixa de o criar.
        if (tipoProduto.isPadrao()) {
            var novoTipo = new ProdutoTipo(tipoProduto.id(), tipoProduto.nome(), false);
            produtoRepositorio.criarNovoTipo(novoTipo);
            
            return;
        }
        
        produtoRepositorio.criarNovoTipo(tipoProduto);
    }
    
    /**
     * Apaga o tipo com base na id.
     * @param id A id do tipo.
     * @throws IdNaoExisteException Se a id do tipo não existir no banco.
     */
    public void apagarTipoPorId(int id) {
        if (!produtoRepositorio.existeTipoId(id)) {
            throw new IdNaoExisteException("Id de tipo de produto não existe no banco de dados.");
        }
        
        produtoRepositorio.apagarTipo(id);
    }

    private void checarValidezCadastro(Produto p, boolean updateMode) throws IdJaExisteException, ValorInvalidoException {
        if (produtoRepositorio.existeId(p.getId()) && !updateMode) {
            throw new IdJaExisteException("Id de produto já existe no banco de dados.");
        }

        if (p.getNome().isBlank() || p.getNome().trim().length() < 3) {
            throw new ValorInvalidoException("Nome inválido! O nome precisa ter no mínimo 3 caracteres");
        }

        if (produtoRepositorio.buscarTipoPorId(p.getTipo().id()) == null) {
            throw new ValorInvalidoException("Tipo de produto inválido!");
        }

        if (!p.getDimensoes().isValida()) {
            throw new ValorInvalidoException("Dimensões de produto inválidas!");
        }

        if (p.getPeso() < 0) {
            throw new ValorInvalidoException("Peso não pode ser negativo!");
        }
    }
}

