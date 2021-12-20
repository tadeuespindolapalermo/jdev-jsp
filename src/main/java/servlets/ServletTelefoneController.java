package servlets;

import static util.ConstantsUtil.MSG;
import static util.ConstantsUtil.REDIRECT_TELEFONE;
import static util.ConstantsUtil.REDIRECT_USUARIO;
import static util.ConstantsUtil.TOTAL_PAGINA;
import static util.ConstantsUtil.USUARIOS;
import static util.ConstantsUtil.USUARIO;
import static util.ConstantsUtil.TELEFONES;
import static util.ObjectUtil.isObjectValid;
import static util.ObjectUtil.redirect;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import dao.DAOTelefoneRepository;
import dao.DAOUsuarioRepository;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ModelLogin;
import model.ModelTelefone;

@WebServlet("/ServletTelefoneController")
public class ServletTelefoneController extends ServletGenericUtil {

	private static final long serialVersionUID = 1L;

	private DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();
	private DAOTelefoneRepository daoTelefoneRepository = new DAOTelefoneRepository();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String acao = request.getParameter("acao");
			if (isObjectValid(acao) && acao.equals("excluir")) {				
				String idFone = request.getParameter("id");
				daoTelefoneRepository.deletarTelefone(Long.parseLong(idFone));
				
				String idUsuarioPai = request.getParameter("usuariopai");
				var modelLogin = daoUsuarioRepository.buscarPoId(Long.parseLong(idUsuarioPai));
				var telefones = daoTelefoneRepository.listarTelefonesDoUsuario(modelLogin.getId());
				redirect(request, response, REDIRECT_TELEFONE, Map.of(USUARIO, modelLogin, TELEFONES, telefones, MSG, "Telefone excluído com sucesso!"));
				return;
			}
			
			String idUsuario = request.getParameter("idUsuario");
			int totalPagina = daoUsuarioRepository.getTotalPaginas(getUserLogado(request).getId());
			if (isObjectValid(idUsuario)) {
				var modelLogin = daoUsuarioRepository.buscarPoId(Long.parseLong(idUsuario));
				var telefones = daoTelefoneRepository.listarTelefonesDoUsuario(modelLogin.getId());
				redirect(request, response, REDIRECT_TELEFONE, Map.of(USUARIO, modelLogin, TELEFONES, telefones));
			} else {
				List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
				redirect(request, response, REDIRECT_USUARIO, Map.of(USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String idUsuarioPai = request.getParameter("id");
			String numero = request.getParameter("numero");
			var msg = "";
			if (!daoTelefoneRepository.existeFone(numero, Long.valueOf(idUsuarioPai))) {
				var modelTelefone = new ModelTelefone(
					numero, 
					daoUsuarioRepository.buscarPoId(Long.parseLong(idUsuarioPai)),
					getUserLogado(request));

				daoTelefoneRepository.gravarTelefone(modelTelefone);
				msg = "Telefone salvo com sucesso!";
			} else {
				msg = "Telefone já existe!";
			}
			var telefones = daoTelefoneRepository.listarTelefonesDoUsuario(Long.parseLong(idUsuarioPai));
			var modelLogin = daoUsuarioRepository.buscarPoId(Long.parseLong(idUsuarioPai));
			redirect(request, response, REDIRECT_TELEFONE, Map.of(MSG, msg, TELEFONES, telefones, USUARIO, modelLogin));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
