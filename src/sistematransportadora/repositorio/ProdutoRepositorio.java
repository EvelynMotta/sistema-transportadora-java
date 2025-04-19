package sistematransportadora.repositorio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sistematransportadora.ConexaoBanco;
import sistematransportadora.modelo.Dimensoes;
import sistematransportadora.modelo.Fragilidade;
import sistematransportadora.modelo.Produto;
import sistematransportadora.modelo.ProdutoTipo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProdutoRepositorio implements Repositorio<Produto> {
    private static final Logger log = LoggerFactory.getLogger(ProdutoRepositorio.class);

    /**
     * Cria um novo produto na base de dados.
     * @param obj Novo produto para ser adicionado.
     */
    @Override
    public void criarNovo(Produto obj) {
        String sql = "INSERT INTO Produto VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (var conn = ConexaoBanco.pegarConnection()) {
            var stmt = conn.prepareStatement(sql);
            preparaStmt(stmt, obj, false);

            stmt.executeUpdate();
        } catch(SQLException e) {
            String err = "Erro ao adicionar produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por um produto na base de dados com id correspondente e o retorna.
     * Se não houver, retorna {@code null}.
     * @param id Id do produto.
     * @return {@link Produto} | {@code null}
     */
    @Override
    public Produto buscarPorId(int id) {
        String sql = """
            SELECT p.*, tp.nome as tipo, tp.padrao as tipo_padrao FROM Produto p
            JOIN Tipo_Produto tp ON tp.id = p.tipo_id
            WHERE p.id = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            if (!rs.next())
                return null;

            return resultParaProduto(rs);
        } catch (SQLException e) {
            String err = "Erro ao conectar com o banco: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por todos os produtos na base de dados e os retorna.
     * @return Um array de produtos - {@code Produto[]}
     */
    @Override
    public Produto[] buscarTodos() {
        String sql = """
            SELECT p.*, tp.nome as tipo, tp.padrao as tipo_padrao FROM Produto p
            JOIN Tipo_Produto tp ON tp.id = p.tipo_id
            """;

        try (var conn = ConexaoBanco.pegarConnection()) {
            var stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            var lista = new ArrayList<Produto>();

            while (rs.next()) {
                lista.add(resultParaProduto(rs));
            }

            return lista.toArray(Produto[]::new);
        } catch(SQLException e) {
            String err = "Erro ao buscar produtos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Conta a quantidade de produtos por tipo cadastrado.
     * Exemplo: Tecnologia -> 3.
     *
     * @return Um {@code HashMap} com os nomes de tipo como chave e
     * as quantidades como valor.
     */
    @Override
    public HashMap<String, Integer> contarPorTipo() {
        var sql = """
            SELECT tp.nome as tipo, COUNT(*) as quantidade FROM Produto p
            JOIN Tipo_Produto tp ON p.tipo_id = tp.id
            GROUP BY tipo_id;
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            HashMap<String, Integer> dicionario = new HashMap<>();

            while (rs.next()) {
                dicionario.put(
                        rs.getString("tipo"),
                        rs.getInt("quantidade")
                );
            }

            if (dicionario.isEmpty())
                return null;

            return dicionario;
        } catch (SQLException e) {
            String err = "Erro ao contar por tipo de produtos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Conta a quantidade de produtos cadastrados no banco.
     * @return {@code int}
     */
    @Override
    public int contarTodos() {
        var sql = "SELECT COUNT(*) as quantidade FROM Produto";
        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();

            return rs.getInt("quantidade");
        } catch (SQLException e) {
            String err = "Erro ao contar todos produtos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Verifica se a id de produto dada existe ou não na base de dados.
     * @param id Id do produto.
     * @return {@code boolean} que diz se existe ou não na tabela.
     */
    @Override
    public boolean existeId(int id) {
        var sql = "SELECT EXISTS(SELECT 1 FROM Produto WHERE id = ?) as existe";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            return rs.getBoolean("existe");
        } catch (SQLException e) {
            String err = "Erro ao verificar se existe id em produtos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Atualiza um produto com base na id. Por isso, um produto com id inexistente no banco
     * de dados não surtirá efeito algum caso enviado como parâmetro.
     * @param obj Produto com atributos atualizados.
     */
    @Override
    public void atualizarUm(Produto obj) {
        var sql = """
            UPDATE Produto SET nome = ?, descricao = ?,
            familia = ?, tipo_id = ?, lote = ?,
            altura = ?, largura = ?, comprimento = ?,
            peso = ?, grau_fragilidade = ?, observacoes = ?
            WHERE id = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            preparaStmt(stmt, obj, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao atualizar produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Apaga um produto com id correspondente na base de dados.
     * @param id Id do produto a ser apagado.
     */
    @Override
    public void apagarPorId(int id) {
        var sql = "DELETE FROM Produto WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao apagar produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Cria um novo tipo de produto na base de dados.
     * @param novoTipo O novo tipo que se deseja adicionar.
     */
    public void criarNovoTipo(ProdutoTipo novoTipo) {
        var sql = "INSERT INTO Tipo_Produto VALUES (?, ?, ?)";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, novoTipo.id());
            stmt.setString(2, novoTipo.nome());
            stmt.setBoolean(3, novoTipo.isPadrao());

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao criar tipo de produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca um tipo de produto na base de dados com base na id.
     * Caso não exista, retorna null.
     * @param id Id do tipo de produto
     * @return {@link ProdutoTipo} | {@code null}
     */
    public ProdutoTipo buscarTipoPorId(int id) {
        var sql = "SELECT * FROM Tipo_Produto WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();

            if (!rs.next())
                return null;

            return new ProdutoTipo(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getBoolean("padrao")
            );
        } catch (SQLException e) {
            String err = "Erro ao buscar tipo de produto por id: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca todos os tipos de produto cadastrados na base de dados.
     * @return {@code ProdutoTipo[]} | {@code null}
     */
    public ProdutoTipo[] buscarTipos() {
        var sql = "SELECT * FROM Tipo_Produto";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            var lista = new ArrayList<ProdutoTipo>();

            while (rs.next()) {
                lista.add(new ProdutoTipo(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getBoolean("padrao")
                ));
            }
            return lista.toArray(ProdutoTipo[]::new);
        } catch (SQLException e) {
            String err = "Erro buscar tipos de produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Atualiza um tipo de produto na base de dados por meio da id.
     * @param novoTipo Tipo com informações atualizadas.
     */
    public void atualizarUmTipo(ProdutoTipo novoTipo) {
        var sql = "UPDATE Tipo_Produto SET nome = ?, padrao = ? WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setString(1, novoTipo.nome());
            stmt.setBoolean(2, novoTipo.isPadrao());
            stmt.setInt(3, novoTipo.id());

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao atualizar um tipo de produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Apaga um tipo de produto da base de dados por meio da id.
     * @param id Id do tipo de produto.
     */
    public void apagarTipo(int id) {
        var sql = "DELETE FROM Tipo_Produto WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao apagar tipo de produto: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    private Produto resultParaProduto(ResultSet rs) throws SQLException {
        var tipo = new ProdutoTipo(
                rs.getInt("tipo_id"),
                rs.getString("tipo"),
                rs.getBoolean("tipo_padrao")
        );

        Fragilidade fragilidade = switch (rs.getString("fragilidade")) {
            case "alta" -> Fragilidade.ALTA;
            case "média" -> Fragilidade.MEDIA;
            default -> Fragilidade.BAIXA;
        };

        var produto = new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                tipo
        );
        produto.setDescricao(rs.getString(("descricao")));
        produto.setFamilia(rs.getString("familia"));
        produto.setLote(rs.getString("lote"));
        produto.setDimensoes(new Dimensoes(
                rs.getDouble("altura"),
                rs.getDouble("largura"),
                rs.getDouble("comprimento")
        ));
        produto.setPeso(rs.getDouble("peso"));
        produto.setGrauFragilidade(fragilidade);
        produto.setObservacoes(rs.getString("observacoes"));

        return produto;
    }

    private void preparaStmt(PreparedStatement stmt, Produto obj, boolean updateMode) throws SQLException {
        var dimensoes = obj.getDimensoes();
        int i = 0;

        String fragilidadeString;
        if (obj.getGrauFragilidade() == null) {
            fragilidadeString = "baixa";
        } else {
            fragilidadeString = switch (obj.getGrauFragilidade()) {
                case ALTA -> "alta";
                case MEDIA -> "média";
                case BAIXA -> "baixa";
            };
        }

        stmt.setInt(updateMode ? 12 : ++i, obj.getId());
        stmt.setString(++i, obj.getNome());
        stmt.setString(++i, obj.getDescricao());
        stmt.setString(++i, obj.getFamilia());
        stmt.setInt(++i, obj.getTipo().id());
        stmt.setString(++i, obj.getLote());
        if (dimensoes != null) {
            stmt.setDouble(++i, dimensoes.altura);
            stmt.setDouble(++i, dimensoes.largura);
            stmt.setDouble(++i, dimensoes.comprimento);
        } else i += 3;
        stmt.setDouble(++i, obj.getPeso());
        stmt.setString(++i, fragilidadeString);
        stmt.setString(++i, obj.getObservacoes());
    }
}
