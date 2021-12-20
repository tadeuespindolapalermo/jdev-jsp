package model;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.Objects;

public class ModelTelefone implements Serializable {

	private static final long serialVersionUID = 5592901800843863075L;

	private Long id;
	private String numero;
	private ModelLogin usuarioPai;
	private ModelLogin usuarioCadastro;
	
	public ModelTelefone(Long id, String numero, ModelLogin usuarioPai, ModelLogin usuarioCadastro) {
		this.id = id;
		this.numero = numero;
		this.usuarioPai = usuarioPai;
		this.usuarioCadastro = usuarioCadastro;
	}
	
	public ModelTelefone( String numero, ModelLogin usuarioPai, ModelLogin usuarioCadastro) {
		this.numero = numero;
		this.usuarioPai = usuarioPai;
		this.usuarioCadastro = usuarioCadastro;
	}

	public boolean isNovo() {
		return isNull(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public ModelLogin getUsuarioPai() {
		return usuarioPai;
	}

	public void setUsuarioPai(ModelLogin usuarioPai) {
		this.usuarioPai = usuarioPai;
	}

	public ModelLogin getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(ModelLogin usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelTelefone other = (ModelTelefone) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "ModelTelefone [id=" + id + ", numero=" + numero + ", usuarioPai=" + usuarioPai + ", usuarioCadastro="
				+ usuarioCadastro + "]";
	}

}
