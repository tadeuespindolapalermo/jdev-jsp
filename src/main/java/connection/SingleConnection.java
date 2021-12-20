package connection;

import static java.util.Objects.isNull;

import java.sql.Connection;
import java.sql.DriverManager;

public final class SingleConnection {
	
	/*postgres://
	ngskqvnkribgse
	:
	5d3933ecc93cc40170edcd4308d63b8126f4d33b9b5289dee3bd50f1b1457317
	@
	ec2-3-95-130-249.compute-1.amazonaws.com:5432/dbbv7mf3n6p0q0*/

	// Local
	private static String banco = "jdbc:postgresql://localhost:5432/projeto-jsp-novo?autoReconnect=true";
	private static String password = getPassword();
	private static String user = "postgres";
	private static Connection connection;
	
	// Heroku
	private static String bancoHeroku = "jdbc:postgresql://ec2-3-95-130-249.compute-1.amazonaws.com:5432/dbbv7mf3n6p0q0?sslmode=require&autoReconnect=true";
	private static String passwordHeroku = "5d3933ecc93cc40170edcd4308d63b8126f4d33b9b5289dee3bd50f1b1457317";
	private static String userHeroku = "ngskqvnkribgse";

	static {
		conectar();
	}

	private static String getPassword() {
		return "tadeu" + "123";
	}

	private SingleConnection() {
	}

	private static void conectar() {
		try {
			if (isNull(connection)) {
				Class.forName("org.postgresql.Driver");
				//connection = DriverManager.getConnection(banco, user, password);
				connection = DriverManager.getConnection(bancoHeroku, userHeroku, passwordHeroku);
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
