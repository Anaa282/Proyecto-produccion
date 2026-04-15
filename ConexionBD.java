import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://172.30.16.52:3306/yourDatabase";
    private static final String USER = "yourUsername";
    private static final String PASSWORD = "yourPassword";
    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());

    public static Connection getConnection() {
        Connection connection = null;
        int attempts = 0;
        boolean connected = false;

        while (attempts < 3 && !connected) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connected = true;
                logger.info("Database connection established successfully.");
            } catch (SQLException e) {
                attempts++;
                logger.log(Level.WARNING, "Attempt " + attempts + " - Failed to connect to the database: " + e.getMessage());
                if (attempts == 3) {
                    logger.severe("Max attempts reached. Unable to connect to the database.");
                }
            }
        }
        return connection;
    }
}