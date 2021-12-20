package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connection.SingleConnection;
import model.ModelLogin;

public class DAOLoginRepository implements Serializable {
	
	private static final long serialVersionUID = -7023211236094093778L;
	
	private transient Connection connection;
	
	public DAOLoginRepository() {
		connection = SingleConnection.getConnection();
	}
	
	public boolean validarAutenticacao(ModelLogin modelLogin) throws SQLException {
		var sql = "SELECT * FROM model_login WHERE upper(login) = upper(?) and upper(senha) = upper(?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {		
			statement.setString(1, modelLogin.getLogin());
			statement.setString(2, modelLogin.getSenha());			
			return statement.executeQuery().next();
		}		
	}

}
