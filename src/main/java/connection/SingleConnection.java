package connection;

import static java.util.Objects.isNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public final class SingleConnection {
	
	private static Connection connection;
	private static Map<String, String> credentials = new Credentials().getCredentials(Server.LOCAL);
	
	static {
		conectar();
	}
	
	private SingleConnection() {}

	private static void conectar() {
		try {
			if (isNull(connection)) {
				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection(
					credentials.get("url"), credentials.get("user"), credentials.get("password")
				);
				connection.setAutoCommit(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		return connection;
	}

}
