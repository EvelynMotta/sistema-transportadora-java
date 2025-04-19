package sistematransportadora;

import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author pedro
 */
public class SistemaTransportadora {

    public static void main(String[] args) {
        prepararBanco();
    }

    private static void prepararBanco() {
        String pastaAppData = System.getenv("APPDATA");
        File pastaSistema = new File(pastaAppData, "SistemaTransportadora");
        File bd = new File (pastaSistema.getPath(), "data.db");

        var existiaPasta = !pastaSistema.mkdirs();
        if (!existiaPasta || !bd.exists()) {
            try (var bdConn = ConexaoBanco.pegarConnection()) {
                File arquivoSql = new File("src/scripts/schema.sql");
                var stmt = bdConn.createStatement();

                if (arquivoSql.exists()) {
                    executarArquivoSQL(arquivoSql, stmt);
                } else {
                    System.out.println(arquivoSql.getAbsolutePath());
                    throw new FileNotFoundException("Não foi possível encontra a schema necessária.");
                }
            } catch (SQLException | FileNotFoundException e) {
                String err = "Erro ao conectar com o banco: " + e.getMessage();
                throw  new RuntimeException(err);
            }
        }
    }

    private static void executarArquivoSQL(File file, Statement stmt) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
}
