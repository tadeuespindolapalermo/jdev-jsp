package servlets;

import static util.ObjectUtil.isObjectValid;
import static util.ObjectUtil.isObjectsValid;
import static util.ObjectUtil.redirect;

import java.io.IOException;
import java.util.Map;
import static util.ConstantsUtil.ACAO;
import static util.ConstantsUtil.MSG;
import dao.DAOLoginRepository;
import dao.DAOUsuarioRepository;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ModelLogin;

@WebServlet(urlPatterns = {"/principal/ServletLogin", "/ServletLogin"})
public class ServletLogin extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final DAOLoginRepository daoLoginRepository = new DAOLoginRepository();
	private final DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		String acao = request.getParameter(ACAO);
        	if (isObjectValid(acao) && acao.equalsIgnoreCase("logout")) {
        		request.getSession().invalidate();
        		redirect(request, response, "/index.jsp", Map.of());
        	} else {
        		doPost(request, response);
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		
    		String login = request.getParameter("login");
        	String senha = request.getParameter("senha");
        	String url = request.getParameter("url");
        	
    		if (isObjectsValid(login, senha)) {
        		var modelLogin = new ModelLogin(login, senha);    		
        		if (daoLoginRepository.validarAutenticacao(modelLogin)) {
        			modelLogin = daoUsuarioRepository.buscarUsuarioLogado(login);
        			request.getSession().setAttribute("usuario", modelLogin.getLogin());
        			request.getSession().setAttribute("isAdmin", modelLogin.isUserAdmin());
        			request.getSession().setAttribute("perfilUsuario", modelLogin.getPerfil());
        			request.getSession().setAttribute("fotoUsuario", modelLogin.getFoto());
        			if (url == null || url.equals("null")) {
        				url = "principal/principal.jsp";
        			}
        			redirect(request, response, url, Map.of());
        		} else {
        			redirect(request, response, "/index.jsp", Map.of(MSG, "Informe o login e senha corretamente!"));
        		}
        	} else {
        		redirect(request, response, "index.jsp", Map.of(MSG, "Informe o login e senha corretamente!"));
        	}
		} catch (Exception e) {
			e.printStackTrace();
			redirect(request, response, "erro.jsp", Map.of(MSG, e.getMessage()));
		}
	}

}
