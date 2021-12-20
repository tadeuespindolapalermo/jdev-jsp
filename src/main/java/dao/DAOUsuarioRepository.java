package dao;

import static java.util.Objects.nonNull;
import static util.ObjectUtil.isObjectValid;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import connection.SingleConnection;
import dto.GraficoSalarioDTO;
import model.ModelLogin;
import model.ModelTelefone;

public class DAOUsuarioRepository implements Serializable {
	
	private static final long serialVersionUID = -6165831396485571353L;
	
	private transient Connection connection;
	
	public DAOUsuarioRepository() {
		connection = SingleConnection.getConnection();
	}
	
	private GraficoSalarioDTO montarGraficoSalario(String sql, Object... parametros) throws SQLException, ParseException {
		var graficoSalarioDTO = new GraficoSalarioDTO();
		
		try (var statement = connection.prepareStatement(sql)) {
			statement.setLong(1, (Long) parametros[0]);
			if (parametros.length > 1) {
				statement.setDate(2, Date.valueOf(new SimpleDateFormat("yyyy-mm-dd").format(new SimpleDateFormat("dd/mm/yyyy").parse((String) parametros[1]))));
				statement.setDate(3, Date.valueOf(new SimpleDateFormat("yyyy-mm-dd").format(new SimpleDateFormat("dd/mm/yyyy").parse((String) parametros[2]))));
			}
			var result = statement.executeQuery();
			
			var perfis = new ArrayList<String>();	
			var salarios = new ArrayList<Double>();				
			
			while(result.next()) {
				salarios.add(result.getDouble("media_salarial"));
				perfis.add(result.getString("perfil"));
			}
			
			graficoSalarioDTO.setPerfis(perfis);
			graficoSalarioDTO.setSalarios(salarios);
		}
		return graficoSalarioDTO;
	}
	
	public GraficoSalarioDTO montarGraficoMediaSalarial(Long idUsuarioLogado) throws SQLException, ParseException {
		var sql = "SELECT AVG(rendamensal) AS media_salarial, perfil FROM model_login WHERE usuario_id = ? GROUP BY perfil";
		return montarGraficoSalario(sql, idUsuarioLogado);
	}
	
	public GraficoSalarioDTO montarGraficoMediaSalarial(Long idUsuarioLogado, String dataInicial, String dataFinal) throws SQLException, ParseException {
		var sql = "SELECT AVG(rendamensal) AS media_salarial, perfil FROM model_login WHERE usuario_id = ? AND datanascimento >= ? AND datanascimento <= ? GROUP BY perfil";
		return montarGraficoSalario(sql, idUsuarioLogado, dataInicial, dataFinal);
	}
	
	public ModelLogin gravarUsuario(ModelLogin objeto, Long usuarioLogado) throws SQLException, ParseException {
		var index = 0;
		var sql = objeto.isNovo() ?
			"INSERT INTO model_login (rendamensal, datanascimento, cep, logradouro, bairro, localidade, uf, numero, sexo, perfil, login, senha, nome, email, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" :
			"UPDATE model_login SET rendamensal = ?, datanascimento = ?, cep=?, logradouro=?, bairro=?, localidade=?, uf=?, numero=?, sexo=?, perfil=?, login=?, senha=?, nome=?, email=? WHERE id=?";
		try (var preparedInsert = connection.prepareStatement(sql)) {
			preparedInsert.setDouble(++ index, objeto.getRendaMensal());
			preparedInsert.setDate(++ index, objeto.getDataNascimento());
			preparedInsert.setString(++ index, objeto.getEndereco().getCep());
			preparedInsert.setString(++ index, objeto.getEndereco().getLogradouro());
			preparedInsert.setString(++ index, objeto.getEndereco().getBairro());
			preparedInsert.setString(++ index, objeto.getEndereco().getLocalidade());
			preparedInsert.setString(++ index, objeto.getEndereco().getUf());
			preparedInsert.setString(++ index, objeto.getEndereco().getNumero());
			preparedInsert.setString(++ index, objeto.getSexo());
			preparedInsert.setString(++ index, objeto.getPerfil());
			preparedInsert.setString(++ index, objeto.getLogin());
			preparedInsert.setString(++ index, objeto.getSenha());
			preparedInsert.setString(++ index, objeto.getNome());
			preparedInsert.setString(++ index, objeto.getEmail());
			preparedInsert.setLong(++ index, objeto.isNovo() ? usuarioLogado : objeto.getId());
			preparedInsert.executeUpdate();
			connection.commit();
			if (isObjectValid(objeto.getFoto())) {
				var where = objeto.isNovo() ? "login" : "id";
				sql = "UPDATE model_login SET foto=?, extensaoFoto=? WHERE " + where + "=?";
				try(var preparedUpdate = connection.prepareStatement(sql)) {
					preparedUpdate.setString(1, objeto.getFoto());
					preparedUpdate.setString(2, objeto.getExtensaoFoto());
					if (objeto.isNovo()) {
						preparedUpdate.setString(3, objeto.getLogin());
					} else {
						preparedUpdate.setLong(3, objeto.getId());
					}
					preparedUpdate.executeUpdate();
					connection.commit();
				}
			}
		}
		return buscarUsuario(objeto.getLogin(), usuarioLogado);
	}
	
	public List<ModelLogin> listarTodosSemLimit(Long usuarioLogado) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE useradmin IS FALSE AND usuario_id = ?";
		return consultar(sql, false, false, false, true, false, usuarioLogado);
	}
	
	public List<ModelLogin> listarTodosSemLimitPorPeriodo(Long usuarioLogado, String dataInicial, String dataFinal) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE useradmin IS FALSE AND usuario_id = ? AND datanascimento >= ? AND datanascimento <= ?";
		return consultar(sql, false, false, false, true, true, usuarioLogado, dataInicial, dataFinal);
	}
	
	public List<ModelLogin> listarTodos(Long usuarioLogado) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE useradmin IS FALSE AND usuario_id = ? LIMIT 5";
		return consultar(sql, false, false, false, false, false, usuarioLogado);
	}
	
	public List<ModelLogin> listarTodosPaginado(Long usuarioLogado, Integer offset) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE useradmin IS FALSE AND usuario_id = ? ORDER BY nome OFFSET " + offset + " LIMIT 5";
		return consultar(sql, false, false, false, false, false, usuarioLogado);
	}
	
	public List<ModelLogin> buscarPorNome(String nome, Long usuarioLogado) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE UPPER(nome) LIKE UPPER(?) AND useradmin IS FALSE AND usuario_id = ? LIMIT 5";
		return consultar(sql, false, false, false, false, false, "%" + nome + "%", usuarioLogado);
	}
	
	public List<ModelLogin> buscarPorNomePaginado(String nome, Long usuarioLogado, String offset) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE UPPER(nome) LIKE UPPER(?) AND useradmin IS FALSE AND usuario_id=? OFFSET " + offset + " LIMIT 5";
		return consultar(sql, false, false, false, false, false, "%" + nome + "%", usuarioLogado);
	}
	
	public ModelLogin buscarPoId(String id, Long usuarioLogado) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE id = ? AND useradmin IS FALSE AND usuario_id = ?";
		return consultar(sql, true, true, true, false, false, Long.parseLong(id), usuarioLogado).stream().findFirst().orElse(new ModelLogin());
	}
	
	public ModelLogin buscarPoId(Long id) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE id = ? AND useradmin IS FALSE";
		return consultar(sql, true, true, true, false, false, id).stream().findFirst().orElse(new ModelLogin());
	}
	
	public ModelLogin buscarUsuario(String login, Long usuarioLogado) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE UPPER(login) = UPPER(?) AND useradmin IS FALSE AND usuario_id = ?";
		return consultar(sql, true, true, true, false, false, login, usuarioLogado).stream().findFirst().orElse(new ModelLogin());
	}
	
	public ModelLogin buscarUsuario(String login) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE UPPER(login) = UPPER(?) AND useradmin IS FALSE";
		return consultar(sql, true, true, true, false, false, login).stream().findFirst().orElse(new ModelLogin());
	}
	
	public ModelLogin buscarUsuarioLogado(String login) throws SQLException, ParseException {
		var sql = "SELECT * FROM model_login WHERE UPPER(login) = UPPER(?)";
		return consultar(sql, true, true, true, false, false, login).stream().findFirst().orElse(new ModelLogin());
	}
	
	private List<ModelLogin> consultar(String sql, boolean renderPassword, boolean renderFoto, boolean renderEndereco, boolean renderTelefone, boolean renderPeriodo, Object... parametros) throws SQLException, ParseException {
		var retorno = new ArrayList<ModelLogin>();
		try (var statement = connection.prepareStatement(sql)) {
			if (nonNull(parametros)) {
				if (parametros[0] instanceof String parameter) statement.setString(1, parameter);
				if (parametros[0] instanceof Long parameter) statement.setLong(1, parameter);
				if (parametros.length > 1 && parametros[1] instanceof Long parameter) statement.setLong(2, parameter);
				if (renderPeriodo) {
					statement.setDate(2, Date.valueOf(new SimpleDateFormat("yyyy-mm-dd").format(new SimpleDateFormat("dd/mm/yyyy").parse((String) parametros[1]))));
					statement.setDate(3, Date.valueOf(new SimpleDateFormat("yyyy-mm-dd").format(new SimpleDateFormat("dd/mm/yyyy").parse((String) parametros[2]))));
				}
			}
			var resultado = statement.executeQuery();
			while (resultado.next()) {
				var modelLogin = new ModelLogin(
					resultado.getLong("id"),
					resultado.getString("nome"),
					resultado.getString("email"),
					resultado.getString("login"),					
					renderPassword ? resultado.getString("senha") : null,
					resultado.getBoolean("useradmin"),
					resultado.getString("perfil"),
					resultado.getString("sexo"),
					renderFoto ? resultado.getString("foto") : null,
					renderFoto ? resultado.getString("extensaoFoto") : null,
					resultado.getDate("datanascimento"),
					resultado.getDouble("rendamensal")
				);	
				if (renderEndereco) preencherEndereco(modelLogin, resultado);
				if (renderTelefone)	preencherTelefone(modelLogin);
				retorno.add(modelLogin);
			}
		}
		return retorno;
	}
	
	private void preencherTelefone(ModelLogin modelLogin) throws SQLException, ParseException {
		var telefones = consultarTelefones(modelLogin.getId());
		if (!telefones.isEmpty())
			modelLogin.setTelefones(telefones);
	}
	
	private void preencherEndereco(ModelLogin modelLogin, ResultSet resultado) throws SQLException {
		modelLogin.getEndereco().setCep(resultado.getString("cep"));
		modelLogin.getEndereco().setLogradouro(resultado.getString("logradouro"));
		modelLogin.getEndereco().setBairro(resultado.getString("bairro"));
		modelLogin.getEndereco().setLocalidade(resultado.getString("localidade"));
		modelLogin.getEndereco().setUf(resultado.getString("uf"));
		modelLogin.getEndereco().setNumero(resultado.getString("numero"));
	}
	
	public boolean validarLogin(String login) throws SQLException {
		var sql = "SELECT COUNT(1) > 0 AS existe FROM model_login WHERE UPPER(login) = UPPER(?)";
		try (var statement = connection.prepareStatement(sql)) {
			statement.setString(1, login);
			var resultado = statement.executeQuery();
			resultado.next();
			return resultado.getBoolean("existe");
		}
	}
	
	public void deletarUsuario(String id) throws SQLException {
		var sql = "DELETE FROM model_login WHERE id = ? AND useradmin IS FALSE";
		try (var statement = connection.prepareStatement(sql)) {
			statement.setLong(1, Long.parseLong(id));
			statement.executeUpdate();
			connection.commit();
		}
	}
	
	public int getTotalPaginas(Long usuarioLogado) throws SQLException {
		var sql = "SELECT COUNT(1) AS total FROM model_login WHERE usuario_id =?";
		try (var statement = connection.prepareStatement(sql)) {
			statement.setLong(1, usuarioLogado);
			return getCalculoTotalPaginas(statement);
		}
	}
	
	public int getTotalPaginasPorNome(String nome, Long usuarioLogado) throws SQLException {
		var sql = "SELECT COUNT(1) AS total FROM model_login WHERE UPPER(nome) LIKE UPPER(?) AND useradmin IS FALSE AND usuario_id = ?";
		try (var statement = connection.prepareStatement(sql)) {
			statement.setString(1, "%" + nome + "%");
			statement.setLong(2, usuarioLogado);
			return getCalculoTotalPaginas(statement);
		}
	}
	
	private int getCalculoTotalPaginas(PreparedStatement statement) throws SQLException {
		var resultado = statement.executeQuery();
		resultado.next();
		var cadastros = resultado.getDouble("total");
		var registrosPorPagina = 5.0;
		Double pagina = cadastros / registrosPorPagina;
		var resto = pagina % 2;
		if (resto > 0) pagina ++;
		return pagina.intValue();
	}
	
	private List<ModelTelefone> consultarTelefones(Long isUsuarioPai) throws SQLException, ParseException {
		var sql = "SELECT * FROM telefone WHERE usuario_pai_id = ?";
		var retorno = new ArrayList<ModelTelefone>();
		try (var statement = connection.prepareStatement(sql)) {
			statement.setLong(1, isUsuarioPai);
			var resultado = statement.executeQuery();
			while (resultado.next()) {
				retorno.add(new ModelTelefone(
					resultado.getLong("id"),
					resultado.getString("numero"),
					buscarPoId(resultado.getLong("usuario_pai_id")),
					buscarPoId(resultado.getLong("usuario_cad_id"))
				));
			}
		}
		return retorno;
	}

}
