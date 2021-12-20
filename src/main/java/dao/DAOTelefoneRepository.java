package dao;

import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import connection.SingleConnection;
import model.ModelTelefone;

public class DAOTelefoneRepository implements Serializable {

	private static final long serialVersionUID = 4714120627238352996L;

	private transient Connection connection;
	private DAOUsuarioRepository daoUsuarioRepository;

	public DAOTelefoneRepository() {
		connection = SingleConnection.getConnection();
		daoUsuarioRepository = new DAOUsuarioRepository();
	}
	
	public void gravarTelefone(ModelTelefone telefone) throws SQLException {
		var sql = telefone.isNovo() ?
			"INSERT INTO telefone (numero, usuario_pai_id, usuario_cad_id) VALUES (?, ?, ?)" :
			"UPDATE telefone SET numero=?, usuario_pai_id=?, usuario_cad_id=? WHERE id=?";
		try (PreparedStatement preparedInsert = connection.prepareStatement(sql)) {
			preparedInsert.setString(1, telefone.getNumero());
			preparedInsert.setLong(2, telefone.getUsuarioPai().getId());
			preparedInsert.setLong(3, telefone.getUsuarioCadastro().getId());
			if (!telefone.isNovo()) preparedInsert.setLong(4, telefone.getId());
			preparedInsert.executeUpdate();
			connection.commit();
		}
	}
	
	public List<ModelTelefone> listarTelefonesDoUsuario(Long idUsuarioPai) throws SQLException, ParseException {
		var sql = "SELECT * FROM telefone WHERE usuario_pai_id = ?";
		return consultar(sql, idUsuarioPai);
	}
	
	private List<ModelTelefone> consultar(String sql, Object... parametros) throws SQLException, ParseException {
		List<ModelTelefone> retorno = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			if (nonNull(parametros)) {
				statement.setLong(1, (Long) parametros[0]);
			}
			ResultSet resultado = statement.executeQuery();
			while (resultado.next()) {
				retorno.add(new ModelTelefone(
					resultado.getLong("id"),
					resultado.getString("numero"),
					daoUsuarioRepository.buscarPoId(resultado.getLong("usuario_pai_id")),
					daoUsuarioRepository.buscarPoId(resultado.getLong("usuario_cad_id"))
				));
			}
		}
		return retorno;
	}
	
	public void deletarTelefone(Long id) throws SQLException {
		var sql = "DELETE FROM telefone WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, id);
			statement.executeUpdate();
			connection.commit();
		}
	}
	
	public boolean existeFone(String fone, Long idUsuario) throws SQLException {
		var sql = "SELECT COUNT(1) > 0 AS existeTelefone FROM telefone WHERE usuario_pai_id = ? AND numero = ?";
		try (var preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setLong(1, idUsuario);
			preparedStatement.setString(2, fone);
			ResultSet resultado = preparedStatement.executeQuery();
			resultado.next();
			return resultado.getBoolean("existeTelefone");
		}
	}

}
