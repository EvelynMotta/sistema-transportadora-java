package sistematransportadora.repositorio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sistematransportadora.ConexaoBanco;
import sistematransportadora.modelo.Dimensoes;
import sistematransportadora.modelo.Embalagem;
import sistematransportadora.modelo.EmbalagemTipo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EmbalagemRepositorio implements Repositorio<Embalagem> {
    private static final Logger log = LoggerFactory.getLogger(EmbalagemRepositorio.class);

    /**
     * Cria um novo embalagem na base de dados.
     * @param obj Novo embalagem para ser adicionado.
     */
    @Override
    public void criarNovo(Embalagem obj) {
        String sql = "INSERT INTO Embalagem VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (var conn = ConexaoBanco.pegarConnection()) {
            var stmt = conn.prepareStatement(sql);
            preparaStmt(stmt, obj, false);

            stmt.executeUpdate();
        } catch(SQLException e) {
            String err = "Erro ao adicionar embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por uma embalagem na base de dados com id correspondente e a retorna.
     * Se não houver, retorna {@code null}.
     * @param id Id da embalagem.
     * @return {@link Embalagem} | {@code null}
     */
    @Override
    public Embalagem buscarPorId(int id) {
        String sql = """
            SELECT e.*, te.nome as tipo, te.padrao as tipo_padrao FROM Embalagem e
            JOIN Tipo_Embalagem te ON te.id = e.tipo_id
            WHERE e.id = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            if (!rs.next())
                return null;

            return resultParaEmbalagem(rs);
        } catch (SQLException e) {
            String err = "Erro ao conectar com o banco: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por todas as embalagens na base de dados e as retorna.
     * @return Um array de embalagens - {@code Embalagem[]}
     */
    @Override
    public Embalagem[] buscarTodos() {
        String sql = """
            SELECT e.*, te.nome as tipo, te.padrao as tipo_padrao FROM Embalagem e
            JOIN Tipo_Embalagem te ON te.id = e.tipo_id
            """;

        try (var conn = ConexaoBanco.pegarConnection()) {
            var stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            var lista = new ArrayList<Embalagem>();

            while (rs.next()) {
                lista.add(resultParaEmbalagem(rs));
            }

            return lista.toArray(Embalagem[]::new);
        } catch(SQLException e) {
            String err = "Erro ao buscar embalagens: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Conta a quantidade de embalagens por tipo cadastrado.
     * Exemplo: Tambor -> 2.
     *
     * @return Um {@code HashMap} com os nomes de tipo como chave e
     * as quantidades como valor.
     */
    @Override
    public HashMap<String, Integer> contarPorTipo() {
        var sql = """
            SELECT te.nome as tipo, COUNT(*) as quantidade FROM Embalagem e
            JOIN Tipo_Embalagem te ON e.tipo_id = te.id
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
            String err = "Erro ao contar por tipo de embalagens: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Conta a quantidade de embalagens cadastradas no banco.
     * @return {@code int}
     */
    @Override
    public int contarTodos() {
        var sql = "SELECT COUNT(*) as quantidade FROM Embalagem";
        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();

            return rs.getInt("quantidade");
        } catch (SQLException e) {
            String err = "Erro ao contar todas embalagens: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Verifica se a id de embalagem dada existe ou não na base de dados.
     * @param id Id da embalagem.
     * @return {@code boolean} que diz se existe ou não na tabela.
     */
    @Override
    public boolean existeId(int id) {
        var sql = "SELECT EXISTS(SELECT 1 FROM Embalagem WHERE id = ?) as existe";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            return rs.getBoolean("existe");
        } catch (SQLException e) {
            String err = "Erro ao verificar se existe id em embalagens: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Atualiza uma embalagem com base na id. Por isso, uma embalagem com id inexistente no banco
     * de dados não surtirá efeito algum caso enviado como parâmetro.
     * @param obj Embalagem com atributos atualizados.
     */
    @Override
    public void atualizarUm(Embalagem obj) {
        var sql = """
            UPDATE Embalagem SET altura = ?, largura = ?,
            comprimento = ?, peso = ?, empilhavel = ?,
            observacoes = ?, tipo_id = ?, produto_id = ?
            WHERE id = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            preparaStmt(stmt, obj, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao atualizar embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Apaga uma embalagem com id correspondente na base de dados.
     * @param id Id da embalagem a ser apagada.
     */
    @Override
    public void apagarPorId(int id) {
        var sql = "DELETE FROM Embalagem WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao apagar embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Cria um novo tipo de embalagem na base de dados.
     * @param novoTipo O novo tipo que se deseja adicionar.
     */
    public void criarNovoTipo(EmbalagemTipo novoTipo) {
        var sql = "INSERT INTO Tipo_Embalagem VALUES (?, ?, ?)";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, novoTipo.id());
            stmt.setString(2, novoTipo.nome());
            stmt.setBoolean(3, novoTipo.isPadrao());

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao criar tipo de embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca um tipo de embalagem na base de dados com base na id.
     * Caso não exista, retorna null.
     * @param id Id do tipo de embalagem
     * @return {@link EmbalagemTipo} | {@code null}
     */
    public EmbalagemTipo buscarTipoPorId(int id) {
        var sql = "SELECT * FROM Tipo_Embalagem WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();

            if (!rs.next())
                return null;

            return new EmbalagemTipo(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getBoolean("padrao")
            );
        } catch (SQLException e) {
            String err = "Erro ao buscar tipo de embalagem por id: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca todos os tipos de embalagem cadastrados na base de dados.
     * @return {@code EmbalagemTipo[]} | {@code null}
     */
    public EmbalagemTipo[] buscarTipos() {
        var sql = "SELECT * FROM Tipo_Embalagem";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            var lista = new ArrayList<EmbalagemTipo>();

            while (rs.next()) {
                lista.add(new EmbalagemTipo(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getBoolean("padrao")
                ));
            }
            return lista.toArray(EmbalagemTipo[]::new);
        } catch (SQLException e) {
            String err = "Erro buscar tipos de embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }
    
    /**
     * Verifica se a id to tipo de embalagem dada existe ou não na base de dados.
     * @param id Id do tipo de embalagem.
     * @return {@code boolean} que diz se existe ou não na tabela.
     */
    public boolean existeTipoId(int id) {
        var sql = "SELECT EXISTS(SELECT 1 FROM Tipo_Embalagem WHERE id = ?) as existe";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            return rs.getBoolean("existe");
        } catch (SQLException e) {
            String err = "Erro ao verificar se existe id em tipos de embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Atualiza um tipo de embalagem na base de dados por meio da id.
     * @param novoTipo Tipo com informações atualizadas.
     */
    public void atualizarUmTipo(EmbalagemTipo novoTipo) {
        var sql = "UPDATE Tipo_Embalagem SET nome = ?, padrao = ? WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setString(1, novoTipo.nome());
            stmt.setBoolean(2, novoTipo.isPadrao());
            stmt.setInt(3, novoTipo.id());

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao atualizar um tipo de embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Apaga um tipo de embalagem da base de dados por meio da id.
     * @param id Id do tipo de embalagem.
     */
    public void apagarTipo(int id) {
        var sql = "DELETE FROM Tipo_Embalagem WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao apagar tipo de embalagem: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    private Embalagem resultParaEmbalagem(ResultSet rs) throws SQLException {
        var tipo = new EmbalagemTipo(
                rs.getInt("tipo_id"),
                rs.getString("tipo"),
                rs.getBoolean("tipo_padrao")
        );

        var dimensoes = new Dimensoes(
                rs.getDouble("altura"),
                rs.getDouble("largura"),
                rs.getDouble("comprimento")
        );

        var embalagem = new Embalagem(
                rs.getInt("id"),
                tipo,
                new ProdutoRepositorio().buscarPorId(rs.getInt("produto_id")),
                dimensoes,
                rs.getDouble("peso")
        );
        embalagem.setEmpilhavel(rs.getBoolean("empilhavel"));
        embalagem.setObservacoes(rs.getString("observacoes"));

        return embalagem;
    }

    private void preparaStmt(PreparedStatement stmt, Embalagem obj, boolean updateMode) throws SQLException {
        var dimensoes = obj.getDimensoes();
        int i = 0;

        stmt.setInt(updateMode ? 9 : ++i, obj.getId());
        stmt.setDouble(++i, dimensoes.altura);
        stmt.setDouble(++i, dimensoes.largura);
        stmt.setDouble(++i, dimensoes.comprimento);
        stmt.setDouble(++i, obj.getPeso());
        stmt.setBoolean(++i, obj.isEmpilhavel());
        stmt.setString(++i, obj.getObservacoes());
        stmt.setInt(++i, obj.getTipo().id());
        stmt.setInt(++i, obj.getProdutoAssociado().getId());
    }
}
