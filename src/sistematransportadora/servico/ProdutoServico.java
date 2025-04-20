package sistematransportadora.servico;

import sistematransportadora.modelo.Produto;
import sistematransportadora.repositorio.ProdutoRepositorio;

/**
 * Serviço que faz validação e teste das regras de negócio antes de
 * fazer uma alteração no banco de dados.
 */
public class ProdutoServico {
    private final ProdutoRepositorio produtoRepositorio;

    // TODO: Implementar métodos para os tipos.

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

