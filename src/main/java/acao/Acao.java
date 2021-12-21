package acao;

import java.util.Arrays;
import java.util.Objects;

public enum Acao {
	
	SALVAR("salvar"),
	DELETAR("deletar"),
	DELETAR_AJAX("deletar-ajax"),
	BUSCAR_USUARIO_AJAX("buscarUsuario-ajax"),
	DELBUSCAR_USUARIO_AJAX_PAGE("buscarUsuario-ajax-page"),
	BUSCAR_EDITAR("buscarEditar"),
	LISTAR("listar"),
	PAGINAR("paginar"),
	DOWNLOAD_FOTO("downloadFoto"),
	GRAFICO_SALARIO("grafico-salario"),
	IMPRIMIR_RELATORIO_USUARIO_TELA("imprimir-relatorio-usuario-tela"),
	IMPRIMIR_RELATORIO_USUARIO_PDF("imprimir-relatorio-usuario-PDF");

	 private final String nome;

	 Acao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
    
    public static Acao get(String value) {
        return Arrays.stream(values()).filter(v -> Objects.equals(v.nome, value)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return nome;
    }
	
}
