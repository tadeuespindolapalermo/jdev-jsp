package model;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ModelLogin implements Serializable {

	private static final long serialVersionUID = 2472796871797376756L;

	// Identificador
	private Long id;

	// Dados básicos
	private String nome;
	private String email;
	private String sexo;
	private Date dataNascimento;
	private Double rendaMensal;
	private List<ModelTelefone> telefones = new ArrayList<>();

	// Credenciais
	private String login;
	private String senha;

	// Perfil
	private boolean userAdmin;
	private String perfil;

	// Foto
	private String foto;
	private String extensaoFoto;

	// Endereço
	private Endereco endereco = new Endereco();

	public ModelLogin() {
	}

	public ModelLogin(String login, String senha) {
		this.login = login;
		this.senha = senha;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha) {
		this(login, senha);
		this.id = id;
		this.nome = nome;
		this.email = email;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin) {
		this(id, nome, email, login, senha);
		this.userAdmin = userAdmin;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin,
			String perfil) {
		this(id, nome, email, login, senha, userAdmin);
		this.perfil = perfil;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin, String perfil,
			String sexo) {
		this(id, nome, email, login, senha, userAdmin, perfil);
		this.sexo = sexo;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin, String perfil,
			String sexo, String foto) {
		this(id, nome, email, login, senha, userAdmin, perfil, sexo);
		this.foto = foto;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin, String perfil,
			String sexo, String foto, String extensaoFoto) {
		this(id, nome, email, login, senha, userAdmin, perfil, sexo, foto);
		this.extensaoFoto = extensaoFoto;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin, String perfil,
			String sexo, String foto, String extensaoFoto, Date dataNascimento) {
		this(id, nome, email, login, senha, userAdmin, perfil, sexo, foto, extensaoFoto);
		this.dataNascimento = dataNascimento;
	}
	
	public ModelLogin(Long id, String nome, String email, String login, String senha, boolean userAdmin, String perfil,
			String sexo, String foto, String extensaoFoto, Date dataNascimento, Double rendaMensal) {
		this(id, nome, email, login, senha, userAdmin, perfil, sexo, foto, extensaoFoto, dataNascimento);
		this.rendaMensal = rendaMensal;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, String perfil) {
		this(id, nome, email, login, senha);
		this.perfil = perfil;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, String perfil, String sexo) {
		this(id, nome, email, login, senha, perfil);
		this.sexo = sexo;
	}

	public ModelLogin(Long id, String nome, String email, String login, String senha, String perfil, String sexo,
			Date dataNascimento) {
		this(id, nome, email, login, senha, perfil, sexo);
		this.dataNascimento = dataNascimento;
	}
	
	public ModelLogin(Long id, String nome, String email, String login, String senha, String perfil, String sexo,
			Date dataNascimento, Double rendaMensal) {
		this(id, nome, email, login, senha, perfil, sexo, dataNascimento);
		this.rendaMensal = rendaMensal;
	}

	public boolean isNovo() {
		return isNull(id);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isUserAdmin() {
		return userAdmin;
	}

	public void setUserAdmin(boolean userAdmin) {
		this.userAdmin = userAdmin;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getExtensaoFoto() {
		return extensaoFoto;
	}

	public void setExtensaoFoto(String extensaoFoto) {
		this.extensaoFoto = extensaoFoto;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setRendaMensal(Double rendaMensal) {
		this.rendaMensal = rendaMensal;
	}

	public Double getRendaMensal() {
		return rendaMensal;
	}
	
	public void setTelefones(List<ModelTelefone> telefones) {
		this.telefones = telefones;
	}
	
	public List<ModelTelefone> getTelefones() {
		return telefones;
	}

	@Override
	public String toString() {
		return "ModelLogin [id=" + id + ", nome=" + nome + ", email=" + email + ", sexo=" + sexo + ", dataNascimento="
				+ dataNascimento + ", rendaMensal=" + rendaMensal + ", login=" + login + ", senha=" + senha
				+ ", userAdmin=" + userAdmin + ", perfil=" + perfil + ", foto=" + foto + ", extensaoFoto="
				+ extensaoFoto + ", endereco=" + endereco + "]";
	}

}
