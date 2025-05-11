package sistematransportadora;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;
import sistematransportadora.ui.TelaPrincipal;

/**
 *
 * @author pedro
 */
public class SistemaTransportadora {
    public static final String VERSAO_APP = "v1.0.0";
    public static final int VERSAO_BANCO = 1;
    
    public static void main(String[] args) {
        prepararBanco();
        TelaPrincipal.main(null);
    }

    private static void prepararBanco() {
        String pastaAppData = System.getenv("APPDATA");
        File pastaSistema = new File(pastaAppData, "SistemaTransportadora");
        File bd = new File (pastaSistema.getPath(), "data.db");

        var existiaPasta = !pastaSistema.mkdirs();
        if (!existiaPasta || !bd.exists()) {
            try (var bdConn = ConexaoBanco.pegarConnection()) {
                InputStream arquivoSql = SistemaTransportadora.class
                        .getResourceAsStream("/scripts/schema.sql");
                var stmt = bdConn.createStatement();

                if (arquivoSql != null) {
                    executarArquivoSQL(arquivoSql, stmt);
                } else {
                    throw new FileNotFoundException("Não foi possível encontra a schema necessária.");
                }
            } catch (SQLException | FileNotFoundException e) {
                String err = "Erro ao conectar com o banco: " + e.getMessage();
                throw  new RuntimeException(err);
            }
            return;
        }
        
        verificarNecessidadeDeMigrations();
    }

    private static void executarArquivoSQL(InputStream file, Statement stmt) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            String linha;
            StringBuilder sql = new StringBuilder();

            while ((linha = reader.readLine()) != null) {
                sql.append(linha).append("\n");
            }

            stmt.executeLargeUpdate(sql.toString());
        } catch (IOException | SQLException e) {
            System.out.println("Erro ao executar script SQL de criação: " + e.getMessage());
        }
    }
    
    private static void verificarNecessidadeDeMigrations() {
        int versaoBancoLocal;
        
        try (var bdConn = ConexaoBanco.pegarConnection()) {
            var stmt = bdConn.prepareStatement("SELECT * FROM Versao WHERE id = 1");
            var rs = stmt.executeQuery();
            versaoBancoLocal = rs.getInt("numero_versao");
            
            // Por enquanto sem tratamento para caso a aplicação
            // esteja desatualizada.
            if (VERSAO_BANCO <= versaoBancoLocal)
                return;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar necessidade de migrations: " + e.getMessage());
            return;
        }
        
        executarMigrationsParaVersao(versaoBancoLocal);
    }
    
    private static void executarMigrationsParaVersao(int versaoAtual) {
        StringBuilder sqlBuilder = new StringBuilder();

        for (int v = versaoAtual; v < VERSAO_BANCO; v++) {
            var arquivo = SistemaTransportadora.class.getResourceAsStream(
                String.format("/scripts/migrations/migration_%d.sql", v)
            );
            if (arquivo == null) continue;

            try (var reader = new BufferedReader(new InputStreamReader(arquivo))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (linha.startsWith("--")) continue;
                    sqlBuilder.append(linha).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler arquivo de migration: " + e.getMessage());
                return;
            }
        }

        String[] comandos = sqlBuilder.toString().split(";");
        ArrayList<String> comandosDrop = new ArrayList<>();
        ArrayList<String> comandosTransacionais = new ArrayList<>();

        for (String cmd : comandos) {
            String sql = cmd.trim();
            if (sql.isEmpty()) continue;

            if (sql.toUpperCase().startsWith("DROP TABLE")) {
                comandosDrop.add(sql);
            } else {
                comandosTransacionais.add(sql);
            }
        }

        // Executa os comandos transacionais.
        try (Connection conn = ConexaoBanco.pegarConnection()) {
            conn.setAutoCommit(false);
            for (String sql : comandosTransacionais) {
                System.out.println("Executando: " + sql);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = ConexaoBanco.pegarConnection()) {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao fazer rollback: " + rollbackEx.getMessage());
            }
            
            System.err.println("Erro ao executar migrations: " + e.getMessage());
            return;
        } finally {
            try (Connection conn = ConexaoBanco.pegarConnection()) {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Erro ao ativar autoCommit: " + e.getMessage());
            }
        }

        // Executa os comandos de DROP fora da transação.
        for (String sql : comandosDrop) {
            System.out.println("Executando DROP fora da transação: " + sql);
            try (
                Connection conn = ConexaoBanco.pegarConnection();
                Statement stmt = conn.createStatement()
            ) {
                stmt.execute(sql);
            } catch (SQLException e) {
                System.err.println("Erro ao executar DROP TABLE: " + e.getMessage());
            }
        }
    }
}
