package acao;

import static acao.Acao.BUSCAR_EDITAR;
import static acao.Acao.BUSCAR_USUARIO_AJAX;
import static acao.Acao.DELBUSCAR_USUARIO_AJAX_PAGE;
import static acao.Acao.DELETAR;
import static acao.Acao.DELETAR_AJAX;
import static acao.Acao.DOWNLOAD_FOTO;
import static acao.Acao.GRAFICO_SALARIO;
import static acao.Acao.IMPRIMIR_RELATORIO_USUARIO_PDF;
import static acao.Acao.IMPRIMIR_RELATORIO_USUARIO_TELA;
import static acao.Acao.LISTAR;
import static acao.Acao.PAGINAR;
import static acao.Acao.SALVAR;
import static java.util.Objects.isNull;
import static util.ConstantsUtil.DATA_FINAL;
import static util.ConstantsUtil.DATA_INICIAL;
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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.DAOUsuarioRepository;
import dto.GraficoSalarioDTO;
import model.ModelLogin;
import net.sf.jasperreports.engine.JRException;
import servlets.ServletGenericUtil;
import util.ReportUtil;

public class AcaoServletUsuario extends ServletGenericUtil {
	
	private static final long serialVersionUID = 1L;
	
	private final DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();
	
	public AcaoServletUsuario(Acao acao, HttpServletRequest request, HttpServletResponse response) throws SQLException, ParseException, IOException, JRException, ServletException {
		if (isObjectValid(acao)) {
			int totalPagina = daoUsuarioRepository.getTotalPaginas(getUserLogado(request).getId());
			if (acao.equals(Acao.DELETAR)) {
	    		deletar(request, response, acao, totalPagina); 
	    	} else if (acao.equals(BUSCAR_USUARIO_AJAX)) {
	    		buscarAjax(request, response);
	    	} else if (acao.equals(DELBUSCAR_USUARIO_AJAX_PAGE)) {
	    		buscarAjaxPage(request, response);
	    	} else if (acao.equals(BUSCAR_EDITAR)) {
	    		buscarParaEditar(request, response, totalPagina);
	    	} else if (acao.equals(LISTAR)) {
	    		listar(request, response, totalPagina);
	    	} else if (acao.equals(DOWNLOAD_FOTO)) {
	    		downloadFoto(request, response);
	    	} else if(acao.equals(PAGINAR)) {
	    		paginar(request, response, totalPagina);
	    	} else if(acao.equals(IMPRIMIR_RELATORIO_USUARIO_TELA)) {
	    		imprimirRelatorioTela(request, response);
	    	} else if(acao.equals(IMPRIMIR_RELATORIO_USUARIO_PDF)) {
	    		imprimirRelatorioPDF(request, response);
	    	} else if(acao.equals(GRAFICO_SALARIO)) {
	    		gerarGraficoSalario(request, response);
	    	} else if(acao.equals(SALVAR)) {
	    		salvar(request, response, totalPagina);
	    	} else {
	    		redirectDefault(request, response, totalPagina);
	    	}	    
		}
	}

	private void redirectDefault(HttpServletRequest request, HttpServletResponse response, int totalPagina)
			throws SQLException, ParseException {
		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
		redirect(request, response, REDIRECT_USUARIO, Map.of(USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
	}   

	private void gerarGraficoSalario(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ParseException, IOException {
		String dataInicial = request.getParameter(DATA_INICIAL);
		String dataFinal = request.getParameter(DATA_FINAL);
		
		GraficoSalarioDTO dtoGraficoSalario = isObjectsNotValid(dataInicial, dataFinal)
			? daoUsuarioRepository.montarGraficoMediaSalarial(getUserLogado(request).getId())
			: daoUsuarioRepository.montarGraficoMediaSalarial(getUserLogado(request).getId(), dataInicial, dataFinal);    	
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(dtoGraficoSalario));
	}

	private void imprimirRelatorioPDF(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ParseException, JRException, IOException {
		String dataInicial = request.getParameter(DATA_INICIAL);
		String dataFinal = request.getParameter(DATA_FINAL);
		
		var usuarios = isObjectsNotValid(dataInicial, dataFinal)
			? daoUsuarioRepository.listarTodosSemLimit(getUserLogado(request).getId())
			: daoUsuarioRepository.listarTodosSemLimitPorPeriodo(getUserLogado(request).getId(), dataInicial, dataFinal);
		
		Map<String, Object> params = new HashMap<>();
		params.put("PARAM_SUB_REPORT", request.getServletContext().getRealPath("relatorio") + File.separator);
		byte[] relatorio = new ReportUtil().gerarRelatorioPDF(usuarios, "relatorio-usuario", params, request.getServletContext());
		response.setHeader("Content-Disposition", "attachment;filename=arquivo.pdf");
		response.getOutputStream().write(relatorio);
	}

	private void imprimirRelatorioTela(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ParseException {
		String dataInicial = request.getParameter(DATA_INICIAL);
		String dataFinal = request.getParameter(DATA_FINAL);
		
		Map<String, Object> parameters = new HashMap<>(Map.of(DATA_INICIAL, dataInicial, DATA_FINAL, dataFinal));
		
		if (isObjectsNotValid(dataInicial, dataFinal)) {
			parameters.put("listaDeTodosOsUsuarios", daoUsuarioRepository.listarTodosSemLimit(getUserLogado(request).getId()));
		} else {
			parameters.put("listaDeTodosOsUsuarios", daoUsuarioRepository.listarTodosSemLimitPorPeriodo(getUserLogado(request).getId(), dataInicial, dataFinal));
		}
		
		redirect(request, response, REDIRECT_USUARIO_RELATORIO, parameters);
	}

	private void paginar(HttpServletRequest request, HttpServletResponse response, int totalPagina)
			throws SQLException, ParseException {
		Integer offset = Integer.parseInt(request.getParameter("pagina"));
		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodosPaginado(getUserLogado(request).getId(), offset);
		redirect(request, response, REDIRECT_USUARIO, Map.of(USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
	}

	private void downloadFoto(HttpServletRequest request, HttpServletResponse response)	throws IOException {
		try {
			String id = request.getParameter("id");
			var modelLogin = daoUsuarioRepository.buscarPoId(id, getUserLogado(request).getId());
			if (isObjectValid(modelLogin.getFoto())) {
				response.setHeader("Content-Disposition", "attachment;filename=arquivo." + modelLogin.getExtensaoFoto());
				response.getOutputStream().write(Base64.decodeBase64(modelLogin.getFoto().split("\\,")[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listar(HttpServletRequest request, HttpServletResponse response, int totalPagina)
			throws SQLException, ParseException {
		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
		redirect(request, response, REDIRECT_USUARIO, Map.of(USUARIOS, usuarios, MSG, "Usuários carregados!", TOTAL_PAGINA, totalPagina));
	}

	private void buscarParaEditar(HttpServletRequest request, HttpServletResponse response, int totalPagina)
			throws SQLException, ParseException {
		String id = request.getParameter("id");
		var modelLogin = daoUsuarioRepository.buscarPoId(id, getUserLogado(request).getId());
		List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
		redirect(request, response, REDIRECT_USUARIO, Map.of("modelLogin", modelLogin, MSG, "Usuário em edição!", USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
	}

	private void buscarAjaxPage(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ParseException, IOException {
		String nomeBusca = request.getParameter("nomeBusca");
		String pagina = request.getParameter("pagina");
		var dadosJsonUsuario = daoUsuarioRepository.buscarPorNomePaginado(nomeBusca, getUserLogado(request).getId(), pagina);
		response.addHeader("totalPagina", "" + daoUsuarioRepository.getTotalPaginasPorNome(nomeBusca, getUserLogado(request).getId()));
		response.getWriter().write(new ObjectMapper().writeValueAsString(dadosJsonUsuario));
	}

	private void buscarAjax(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ParseException, IOException {
		String nomeBusca = request.getParameter("nomeBusca");
		var dadosJsonUsuario = daoUsuarioRepository.buscarPorNome(nomeBusca, getUserLogado(request).getId());
		var json = new ObjectMapper().writeValueAsString(dadosJsonUsuario);
		response.addHeader("totalPagina", "" + daoUsuarioRepository.getTotalPaginasPorNome(nomeBusca, getUserLogado(request).getId()));
		response.getWriter().write(json);
	}

	private void deletar(HttpServletRequest request, HttpServletResponse response, Acao acao, int totalPagina)
			throws SQLException, ParseException, IOException {
		var msg = "Excluído com sucesso!";
		String id = request.getParameter("id");
		daoUsuarioRepository.deletarUsuario(id);
		if (acao.equals(DELETAR)) {
			List<ModelLogin> usuarios = daoUsuarioRepository.listarTodos(getUserLogado(request).getId());
			redirect(request, response, REDIRECT_USUARIO, Map.of(MSG, msg, USUARIOS, usuarios, TOTAL_PAGINA, totalPagina));
		} else if (acao.equals(DELETAR_AJAX)) {
			response.getWriter().write(msg);
		}
	}
	
	private void salvar(HttpServletRequest request, HttpServletResponse response, int totalPagina) throws SQLException, ParseException, IOException, ServletException {
	
		var msg = "";
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
