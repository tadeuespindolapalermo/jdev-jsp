package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import connection.SingleConnection;

public class DAOVersionadorBancoRepository implements Serializable {

	private static final long serialVersionUID = -8718292286512953853L;

	private transient Connection connection;

	public DAOVersionadorBancoRepository() {
		connection = SingleConnection.getConnection();
	}
	
	public void gravarArquivoSQLExecutado(String nomeArquivo) throws SQLException {
		var sql = "INSERT INTO versionadorbanco (arquivo_sql) VALUES (?)";
		try (var statement = connection.prepareStatement(sql)) {
			statement.setString(1, nomeArquivo);
			statement.execute();
		}
	}

	public boolean arquivoSQLExecutado(String nomeArquivo) throws SQLException {
		var sql = "SELECT COUNT(1) > 0 AS executado FROM versionadorbanco WHERE arquivo_sql = ?";
		try (var statement = connection.prepareStatement(sql)) {
			statement.setString(1, nomeArquivo);
			var result = statement.executeQuery();
			result.next();
			return result.getBoolean("executado");
		}
	}

}
