package filter;

import static util.ObjectUtil.redirect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connection.SingleConnection;
import dao.DAOVersionadorBancoRepository;


@WebFilter(urlPatterns = { "/principal/*" })
public class FilterAutenticacao implements Filter {

	private Connection connection;
	private DAOVersionadorBancoRepository daoVersionadorBancoRepository;

	@Override
	public void destroy() {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();
			String usuarioLogado = (String) session.getAttribute("usuario");
			String urlParaAutenticar = req.getServletPath();

			if (usuarioLogado == null && !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) {
				redirect((HttpServletRequest) request, (HttpServletResponse) response,
						"/index.jsp?url=" + urlParaAutenticar, Map.of("msg", "Favor, realize o login!"));
			} else {
				chain.doFilter(request, response);
			}
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			redirect((HttpServletRequest) request, (HttpServletResponse) response, "erro.jsp",
					Map.of("msg", e.getMessage()));
			try {
				connection.rollback();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		connection = SingleConnection.getConnection();
		daoVersionadorBancoRepository = new DAOVersionadorBancoRepository();
		executarVersionador(filterConfig);
	}
	
	private void executarVersionador(FilterConfig filterConfig) {
		var pathSQL = filterConfig.getServletContext().getRealPath("versionadorbancosql") + File.separator;
		var filesSQL = new File(pathSQL).listFiles();
		try {
			for (var file : filesSQL) {
				boolean arquivoExecutado = daoVersionadorBancoRepository.arquivoSQLExecutado(file.getName());
				if (!arquivoExecutado) {
					versionar(file);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}
	
	private void versionar(File file) throws SQLException {
		try (var fileInputStream = new FileInputStream(file);
			 var scanner = new Scanner(fileInputStream, StandardCharsets.UTF_8)) {
				
			StringBuilder sql = new StringBuilder();
			while (scanner.hasNext()) {
				sql.append(scanner.nextLine()).append("\n");
			}
			try (var ps = connection.prepareStatement(sql.toString())) {
				ps.execute();
				daoVersionadorBancoRepository.gravarArquivoSQLExecutado(file.getName());
				connection.commit();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
