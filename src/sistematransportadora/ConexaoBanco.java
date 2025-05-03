package sistematransportadora;

import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final HikariDataSource dataSource = new HikariDataSource();

    static {
        var arquivoDb = new File(System.getenv("APPDATA"), "SistemaTransportadora/data.db");

        dataSource.setJdbcUrl(String.format("jdbc:sqlite:%s", arquivoDb.getPath()));
        dataSource.setMaximumPoolSize(10);
        
        // Ativando as verificações de foreign keys do sqlite
        dataSource.setConnectionInitSql("PRAGMA foreign_keys = ON");
    }

    public static Connection pegarConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
