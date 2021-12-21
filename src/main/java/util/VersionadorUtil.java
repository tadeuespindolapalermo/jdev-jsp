package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import javax.servlet.FilterConfig;

import connection.SingleConnection;
import dao.DAOVersionadorBancoRepository;

public class VersionadorUtil {
	
	private static Connection connection = SingleConnection.getConnection();
	private static DAOVersionadorBancoRepository daoVersionadorBancoRepository = new DAOVersionadorBancoRepository();
	
	private VersionadorUtil() {}
	
	public static void executarVersionador(FilterConfig filterConfig) {
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
	
	private static void versionar(File file) throws SQLException {
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
