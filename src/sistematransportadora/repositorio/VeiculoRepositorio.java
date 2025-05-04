package sistematransportadora.repositorio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sistematransportadora.ConexaoBanco;
import sistematransportadora.modelo.Dimensoes;
import sistematransportadora.modelo.Veiculo;
import sistematransportadora.modelo.VeiculoTipo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class VeiculoRepositorio implements Repositorio<Veiculo> {
    private static final Logger log = LoggerFactory.getLogger(VeiculoRepositorio.class);

    /**
     * Cria um novo veículo na base de dados.
     * @param obj Novo veículo para ser adicionado.
     */
    @Override
    public void criarNovo(Veiculo obj) {
        String sql = "INSERT INTO Veiculo VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (var conn = ConexaoBanco.pegarConnection()) {
            var stmt = conn.prepareStatement(sql);
            preparaStmt(stmt, obj, false);

            stmt.executeUpdate();
        } catch(SQLException e) {
            String err = "Erro ao adicionar carro: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por um veículo na base de dados com id correspondente e o retorna.
     * Se não houver, retorna {@code null}.
     * @param id Id do veículo.
     * @return {@link Veiculo} | {@code null}
     */
    @Override
    public Veiculo buscarPorId(int id) {
        String sql = """
            SELECT v.*, tv.nome as tipo, tv.padrao as tipo_padrao FROM Veiculo v
            JOIN Tipo_Veiculo tv ON tv.id = v.tipo_id
            WHERE v.id = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            if (!rs.next())
                return null;

            return resultParaVeiculo(rs);
        } catch (SQLException e) {
            String err = "Erro ao conectar com o banco: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por um veículo na base de dados com placa correspondente e o retorna.
     * Se não houver, retorna {@code null}.
     * @param placa Placa do veículo.
     * @return {@link Veiculo} | {@code null}
     */
    public Veiculo buscarPorPlaca(String placa) {
        String sql = """
            SELECT v.*, tv.nome as tipo, tv.padrao as tipo_padrao FROM Veiculo v
            JOIN Tipo_Veiculo tv ON tv.id = v.tipo_id
            WHERE v.placa = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setString(1, placa);

            var rs = stmt.executeQuery();
            if (!rs.next())
                return null;

            return resultParaVeiculo(rs);
        } catch (SQLException e) {
            String err = "Erro ao buscar veículo por placa: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca por todos os veículos na base de dados e os retorna.
     * @return Um array de veículos - {@code Veiculo[]}
     */
    @Override
    public Veiculo[] buscarTodos() {
        String sql = """
            SELECT v.*, tv.nome as tipo, tv.padrao as tipo_padrao FROM Veiculo v
            JOIN Tipo_Veiculo tv ON tv.id = v.tipo_id
            """;

        try (var conn = ConexaoBanco.pegarConnection()) {
            var stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            var lista = new ArrayList<Veiculo>();

            while (rs.next()) {
                lista.add(resultParaVeiculo(rs));
            }

            return lista.toArray(Veiculo[]::new);
        } catch(SQLException e) {
            String err = "Erro ao buscar carros: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }
    
    /**
     * Verifica se a id to tipo de veículo dada existe ou não na base de dados.
     * @param id Id do tipo de veículo.
     * @return {@code boolean} que diz se existe ou não na tabela.
     */
    public boolean existeTipoId(int id) {
        var sql = "SELECT EXISTS(SELECT 1 FROM Tipo_Veiculo WHERE id = ?) as existe";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            return rs.getBoolean("existe");
        } catch (SQLException e) {
            String err = "Erro ao verificar se existe id em tipos de veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Conta a quantidade de veículos por tipo cadastrado.
     * Exemplo: Picape -> 5.
     *
     * @return Um {@code HashMap} com os nomes de tipo como chave e
     * as quantidades como valor.
     */
    @Override
    public HashMap<String, Integer> contarPorTipo() {
        var sql = """
            SELECT tv.nome as tipo, COUNT(*) as quantidade FROM Veiculo v
            JOIN Tipo_Veiculo tv ON v.tipo_id = tv.id
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
            String err = "Erro ao contar por tipo de veículos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Conta a quantidade de veículos cadastrados no banco.
     * @return {@code int}
     */
    @Override
    public int contarTodos() {
        var sql = "SELECT COUNT(*) as quantidade FROM Veiculo";
        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();

            return rs.getInt("quantidade");
        } catch (SQLException e) {
            String err = "Erro ao contar todos veículos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }
    
    /**
     * Conta os tipos de veículo cadastrados na base de dados.
     * @return {@code int}
     */
    public int contarTipos() {
        var sql = "SELECT COUNT(*) as quantidade FROM Tipo_Veiculo";
        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();

            return rs.getInt("quantidade");
        } catch (SQLException e) {
            String err = "Erro ao contar todos os tipos de veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Verifica se a id de veículo dada existe ou não na base de dados.
     * @param id Id do veículo.
     * @return {@code boolean} que diz se existe ou não na tabela.
     */
    @Override
    public boolean existeId(int id) {
        var sql = "SELECT EXISTS(SELECT 1 FROM Veiculo WHERE id = ?) as existe";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();
            return rs.getBoolean("existe");
        } catch (SQLException e) {
            String err = "Erro ao verificar se existe id em veículos: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Atualiza um veículo com base na id. Por isso, um veículo com id inexistente no banco
     * de dados não surtirá efeito algum caso enviado como parâmetro.
     * @param obj Veículo com atributos atualizados.
     */
    @Override
    public void atualizarUm(Veiculo obj) {
        var sql = """
            UPDATE Veiculo SET nome = ?, placa = ?,
            modelo = ?, tipo_id = ?, altura_interna = ?,
            largura_interna = ?, comprimento_interno = ?,
            capacidade_peso = ?, observacoes = ?
            WHERE id = ?
            """;

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            preparaStmt(stmt, obj, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao atualizar veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Apaga um veículo com id correspondente na base de dados.
     * @param id Id do veículo a ser apagado.
     */
    @Override
    public void apagarPorId(int id) {
        var sql = "DELETE FROM Veiculo WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao apagar veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Cria um novo tipo de veículo na base de dados.
     * @param novoTipo O novo tipo que se deseja adicionar.
     */
    public void criarNovoTipo(VeiculoTipo novoTipo) {
        var sql = "INSERT INTO Tipo_Veiculo VALUES (?, ?, ?)";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, novoTipo.id());
            stmt.setString(2, novoTipo.nome());
            stmt.setBoolean(3, novoTipo.isPadrao());

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao criar tipo de veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca um tipo de véiculo na base de dados com base na id.
     * Caso não exista, retorna null.
     * @param id Id do tipo de veículo
     * @return {@link VeiculoTipo} | {@code null}
     */
    public VeiculoTipo buscarTipoPorId(int id) {
        var sql = "SELECT * FROM Tipo_Veiculo WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            var rs = stmt.executeQuery();

            if (!rs.next())
                return null;

            return new VeiculoTipo(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getBoolean("padrao")
            );
        } catch (SQLException e) {
            String err = "Erro ao buscar tipo de veículo por id: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Busca todos os tipos de veículo cadastrados na base de dados.
     * @return {@code VeiculoTipo[]} | {@code null}
     */
    public VeiculoTipo[] buscarTipos() {
        var sql = "SELECT * FROM Tipo_Veiculo";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            var rs = stmt.executeQuery();
            var lista = new ArrayList<VeiculoTipo>();

            while (rs.next()) {
                lista.add(new VeiculoTipo(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getBoolean("padrao")
                ));
            }
            return lista.toArray(VeiculoTipo[]::new);
        } catch (SQLException e) {
            String err = "Erro buscar tipos de veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Atualiza um tipo de veículo na base de dados por meio da id.
     * @param novoTipo Tipo com informações atualizadas.
     */
    public void atualizarUmTipo(VeiculoTipo novoTipo) {
        var sql = "UPDATE Tipo_Veiculo SET nome = ?, padrao = ? WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setString(1, novoTipo.nome());
            stmt.setBoolean(2, novoTipo.isPadrao());
            stmt.setInt(3, novoTipo.id());

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao atualizar um tipo de veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    /**
     * Apaga um tipo de veículo da base de dados por meio da id.
     * @param id Id do tipo de veículo.
     */
    public void apagarTipo(int id) {
        var sql = "DELETE FROM Tipo_Veiculo WHERE id = ?";

        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            String err = "Erro ao apagar tipo de veículo: " + e.getMessage();
            log.error(err);

            throw new RuntimeException(err);
        }
    }

    private Veiculo resultParaVeiculo(ResultSet rs) throws SQLException {
        var tipo = new VeiculoTipo(
                rs.getInt("tipo_id"),
                rs.getString("tipo"),
                rs.getBoolean("tipo_padrao")
        );

        var veiculo = new Veiculo(
                rs.getInt("id"),
                rs.getString("nome"),
                tipo,
                rs.getString("placa"),
                rs.getString("modelo"),
                rs.getDouble("capacidade_peso")
        );
        veiculo.setDimensoesInternas(new Dimensoes(
                rs.getDouble("altura_interna"),
                rs.getDouble("largura_interna"),
                rs.getDouble("comprimento_interno")
        ));
        veiculo.setObservacoes(rs.getString("observacoes"));

        return veiculo;
    }

    private void preparaStmt(PreparedStatement stmt, Veiculo obj, boolean updateMode) throws SQLException {
        var dimensoes = obj.getDimensoesInternas();
        int i = 0;

        stmt.setInt(updateMode ? 10 : ++i, obj.getId());
        stmt.setString(++i, obj.getNome());
        stmt.setString(++i, obj.getPlaca());
        stmt.setString(++i, obj.getModelo());
        stmt.setInt(++i, obj.getTipo().id());
        if (dimensoes != null) {
            stmt.setDouble(++i, dimensoes.altura);
            stmt.setDouble(++i, dimensoes.largura);
            stmt.setDouble(++i, dimensoes.comprimento);
        } else i += 3;
        stmt.setDouble(++i, obj.getCapacidadeDePeso());
        stmt.setString(++i, obj.getObservacoes());
    }
}
