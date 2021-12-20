package util;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportUtil implements Serializable {

	private static final long serialVersionUID = -7392526883009869201L;

	public byte[] gerarRelatorioPDF(List<?> dados, String nome, ServletContext context) throws JRException {
		var dataSource = new JRBeanCollectionDataSource(dados);
		var pathReport = context.getRealPath("relatorio") + File.separator + nome + ".jasper";
		var printJasper = JasperFillManager.fillReport(pathReport, new HashMap<>(), dataSource);
		return JasperExportManager.exportReportToPdf(printJasper);
	}
	
	public byte[] gerarRelatorioPDF(List<?> dados, String nome, Map<String, Object> params, ServletContext context) throws JRException {
		var dataSource = new JRBeanCollectionDataSource(dados);
		var pathReport = context.getRealPath("relatorio") + File.separator + nome + ".jasper";
		var printJasper = JasperFillManager.fillReport(pathReport, params, dataSource);
		return JasperExportManager.exportReportToPdf(printJasper);
	}

}
