package servlets;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import dao.DAOUsuarioRepository;
import model.ModelLogin;

public class ServletGenericUtil extends HttpServlet implements Serializable {

	private static final long serialVersionUID = 1L;

	private DAOUsuarioRepository daoUsuarioRepository;

	public ServletGenericUtil() {
		daoUsuarioRepository = new DAOUsuarioRepository();
	}

	public ModelLogin getUserLogado(HttpServletRequest request) throws SQLException, ParseException {
		String usuarioLogado = (String) request.getSession().getAttribute("usuario");
		return daoUsuarioRepository.buscarUsuarioLogado(usuarioLogado);
	}

}
