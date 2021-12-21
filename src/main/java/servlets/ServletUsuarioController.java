package servlets;

import static util.ConstantsUtil.ACAO;
import static util.ConstantsUtil.MSG;
import static util.ObjectUtil.redirect;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import acao.Acao;
import acao.AcaoServletUsuario;

@MultipartConfig
@WebServlet(urlPatterns = {"/ServletUsuarioController"})
public class ServletUsuarioController extends ServletGenericUtil {
	
	private static final long serialVersionUID = 1L;

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
	    	new AcaoServletUsuario(Acao.get(request.getParameter(ACAO)), request, response);
    	} catch(Exception e) {
			redirect(request, response, "erro.jsp", Map.of(MSG, e.getMessage()));
    	}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			new AcaoServletUsuario(Acao.SALVAR, request, response);
		} catch (Exception e) {
			redirect(request, response, "erro.jsp", Map.of(MSG, e.getMessage()));
		}
	}	
	
}
