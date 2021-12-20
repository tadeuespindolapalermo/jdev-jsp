package servlets;

import static java.util.Objects.isNull;
import static util.ConstantsUtil.ACAO;
import static util.ConstantsUtil.MSG;
import static util.ConstantsUtil.REDIRECT_USUARIO;
import static util.ConstantsUtil.REDIRECT_USUARIO_RELATORIO;
import static util.ConstantsUtil.TOTAL_PAGINA;
import static util.ConstantsUtil.USUARIOS;
import static util.ObjectUtil.isObjectValid;
import static util.ObjectUtil.isObjectsNotValid;
import static util.ObjectUtil.redirect;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.DAOUsuarioRepository;
import dto.GraficoSalarioDTO;
import model.ModelLogin;
import util.ReportUtil;

@MultipartConfig
@WebServlet(urlPatterns = {"/ServletUsuarioController"})
public class ServletUsuarioController extends ServletGenericUtil {
	
	private static final long serialVersionUID = 1L;
	
	private DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	try {
    		var msg = "";
	    	String acao = request.getParameter(ACAO);
	    	int totalPagina = daoUsuarioRepository.getTotalPaginas(getUserLogado(request).getId());
	    	if (isObjectValid(acao) && acao.equals("deletar")) {
	    		String id = request.getParameter("id");
	    		daoUsuarioRepository.deletarUsuario(id);
	    		if (acao.equalsIgnoreCase("deletar")) {
		    		msg = "Excluído com sucesso!";
		    		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
		    		redirect(request, response, REDIRECT_USUARIO, Map.of(MSG, msg, USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
		    	} else if (acao.equals("deletar-ajax")) {
		    		response.getWriter().write("Excluído com sucesso!");
		    	} 
	    		
	    	} else if (isObjectValid(acao) && acao.equals("buscarUsuario-ajax")) {
	    		String nomeBusca = request.getParameter("nomeBusca");
	    		var dadosJsonUsuario = daoUsuarioRepository.buscarPorNome(nomeBusca, getUserLogado(request).getId());
	    		var json = new ObjectMapper().writeValueAsString(dadosJsonUsuario);
	    		response.addHeader("totalPagina", "" + daoUsuarioRepository.getTotalPaginasPorNome(nomeBusca, getUserLogado(request).getId()));
	    		response.getWriter().write(json);
	    		
	    	} else if (isObjectValid(acao) && acao.equals("buscarUsuario-ajax-page")) {
	    		String nomeBusca = request.getParameter("nomeBusca");
	    		String pagina = request.getParameter("pagina");
	    		var dadosJsonUsuario = daoUsuarioRepository.buscarPorNomePaginado(nomeBusca, getUserLogado(request).getId(), pagina);
	    		response.addHeader("totalPagina", "" + daoUsuarioRepository.getTotalPaginasPorNome(nomeBusca, getUserLogado(request).getId()));
	    		response.getWriter().write(new ObjectMapper().writeValueAsString(dadosJsonUsuario));
	    		
	    	} else if (isObjectValid(acao) && acao.equals("buscarEditar")) {
	    		msg = "Usuário em edição!";
	    		String id = request.getParameter("id");
	    		var modelLogin = daoUsuarioRepository.buscarPoId(id, getUserLogado(request).getId());
	    		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
	    		redirect(request, response, REDIRECT_USUARIO, Map.of("modelLogin", modelLogin, MSG, msg, USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
	    	
	    	} else if (isObjectValid(acao) && acao.equals("listar")) {
	    		msg = "Usuários carregados!";
	    		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
	    		redirect(request, response, REDIRECT_USUARIO, Map.of(USUARIOS, usuarios, MSG, msg, TOTAL_PAGINA, totalPagina));
	    	
	    	} else if (isObjectValid(acao) && acao.equals("downloadFoto")) {
	    		String id = request.getParameter("id");
	    		var modelLogin = daoUsuarioRepository.buscarPoId(id, getUserLogado(request).getId());
	    		if (isObjectValid(modelLogin.getFoto())) {
	    			response.setHeader("Content-Disposition", "attachment;filename=arquivo." + modelLogin.getExtensaoFoto());
	    			response.getOutputStream().write(Base64.decodeBase64(modelLogin.getFoto().split("\\,")[1]));
	    		}
	    	
	    	} else if(isObjectValid(acao) && acao.equals("paginar")) {
	    		Integer offset = Integer.parseInt(request.getParameter("pagina"));
	    		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodosPaginado(getUserLogado(request).getId(), offset);
	    		redirect(request, response, REDIRECT_USUARIO, Map.of(MSG, msg, USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
	    	
	    	} else if(isObjectValid(acao) && acao.equals("imprimir-relatorio-usuario-tela")) {
	    		String dataInicial = request.getParameter("dataInicial");
	    		String dataFinal = request.getParameter("dataFinal");
	    		
	    		Map<String, Object> parameters = new HashMap<>(Map.of("dataInicial", dataInicial, "dataFinal", dataFinal));
	    		
	    		if (isObjectsNotValid(dataInicial, dataFinal)) {
	    			parameters.put("listaDeTodosOsUsuarios", daoUsuarioRepository.listarTodosSemLimit(getUserLogado(request).getId()));
	    		} else {
	    			parameters.put("listaDeTodosOsUsuarios", daoUsuarioRepository.listarTodosSemLimitPorPeriodo(getUserLogado(request).getId(), dataInicial, dataFinal));
	    		}
	    		
	    		redirect(request, response, REDIRECT_USUARIO_RELATORIO, parameters);
	    	
	    	} else if(isObjectValid(acao) && acao.equals("imprimir-relatorio-usuario-PDF")) {
	    		String dataInicial = request.getParameter("dataInicial");
	    		String dataFinal = request.getParameter("dataFinal");
	    		
	    		var usuarios = isObjectsNotValid(dataInicial, dataFinal)
    				? daoUsuarioRepository.listarTodosSemLimit(getUserLogado(request).getId())
					: daoUsuarioRepository.listarTodosSemLimitPorPeriodo(getUserLogado(request).getId(), dataInicial, dataFinal);
	    		
	    		Map<String, Object> params = new HashMap<>();
	    		params.put("PARAM_SUB_REPORT", request.getServletContext().getRealPath("relatorio") + File.separator);
	    		byte[] relatorio = new ReportUtil().gerarRelatorioPDF(usuarios, "relatorio-usuario", params, request.getServletContext());
    			response.setHeader("Content-Disposition", "attachment;filename=arquivo.pdf");
    			response.getOutputStream().write(relatorio);
    			
	    	} else if(isObjectValid(acao) && acao.equals("grafico-salario")) {
	    		String dataInicial = request.getParameter("dataInicial");
	    		String dataFinal = request.getParameter("dataFinal");
	    		
	    		GraficoSalarioDTO dtoGraficoSalario = isObjectsNotValid(dataInicial, dataFinal)
    				? daoUsuarioRepository.montarGraficoMediaSalarial(getUserLogado(request).getId())
    				: daoUsuarioRepository.montarGraficoMediaSalarial(getUserLogado(request).getId(), dataInicial, dataFinal);    	
	    		
	    		response.getWriter().write(new ObjectMapper().writeValueAsString(dtoGraficoSalario));
	    		
	    	} else {
	    		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
	    		redirect(request, response, REDIRECT_USUARIO, Map.of(MSG, msg, USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
	    	}	    
    	} catch(Exception e) {
    		e.printStackTrace();
			redirect(request, response, "erro.jsp", Map.of(MSG, e.getMessage()));
    	}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			var msg = "";
			int totalPagina = daoUsuarioRepository.getTotalPaginas(getUserLogado(request).getId());
			String id = request.getParameter("id");
			String nome = request.getParameter("nome");
			String email = request.getParameter("email");
			String login = request.getParameter("login");
			String senha = request.getParameter("senha");
			String perfil = request.getParameter("perfil");
			String sexo = request.getParameter("sexo");
			String dataNascimento = request.getParameter("dataNascimento");
			String rendaMensal = request.getParameter("rendaMensal");
			
			String[] endereco = {
				request.getParameter("cep"),
				request.getParameter("logradouro"),
				request.getParameter("bairro"),
				request.getParameter("localidade"),
				request.getParameter("uf"),
				request.getParameter("numero")
			};			
			
			var modelLogin = new ModelLogin(
				isObjectValid(id) ? Long.parseLong(id) : null, 
				nome, email, login, senha, perfil, sexo,
				Date.valueOf(new SimpleDateFormat("yyyy-mm-dd").format(new SimpleDateFormat("dd/mm/yyyy").parse(dataNascimento))),
				Double.parseDouble(rendaMensal.split("\\ ")[1].replaceAll("\\.", "").replaceAll("\\,", "."))
			);
			
			preencherEndereco(modelLogin, endereco);
			
			var part = request.getPart("fileFoto");
			if (isObjectValid(part) && part.getSize() > 0) {
				var extensao = part.getContentType().split("\\/")[1];
				byte[] foto = IOUtils.toByteArray(part.getInputStream());
				var base64 = "data:image/" + extensao + ";base64," + Base64.encodeBase64String(foto);
				modelLogin.setFoto(base64);
				modelLogin.setExtensaoFoto(extensao);
			}
			
			if (daoUsuarioRepository.validarLogin(modelLogin.getLogin()) && isNull(modelLogin.getId())) {
				msg = "Já existe usuário com o mesmo login, informe outro login!";
			} else {
				msg = modelLogin.isNovo() ? "Gravado com sucesso!" : "Atualizado com sucesso!";
				modelLogin = daoUsuarioRepository.gravarUsuario(modelLogin, getUserLogado(request).getId());
			}
			
			List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
			redirect(request, response, REDIRECT_USUARIO, Map.of("modelLogin", modelLogin, MSG, msg, USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
		} catch (Exception e) {
			e.printStackTrace();
			redirect(request, response, "erro.jsp", Map.of(MSG, e.getMessage()));
		}
	}
	
	private void preencherEndereco(ModelLogin modelLogin, String... dados) {
		modelLogin.getEndereco().setCep(dados[0]);
		modelLogin.getEndereco().setLogradouro(dados[1]);
		modelLogin.getEndereco().setBairro(dados[2]);
		modelLogin.getEndereco().setLocalidade(dados[3]);
		modelLogin.getEndereco().setUf(dados[4]);
		modelLogin.getEndereco().setNumero(dados[5]);
	}

}
