package br.com.pojo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.annotation.HbnDao;
import br.com.annotation.Service;
import br.com.dao.Identifiable;
import br.com.dao.hbn.HbnUsuarioDao;
import br.com.service.UsuarioServiceImpl;

@Entity
@Table(name = "usuario")
@Service(service = UsuarioServiceImpl.class)
@HbnDao(hbnDao = HbnUsuarioDao.class)
public class Usuario implements Serializable, Identifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue()
	@Column(name = "id")
	private Integer id;

	@Column(name = "login")
	private String login;

	@Column(name = "ativo")
	private boolean ativo;

	@Column(name = "senha")
	private String senha;

	@Column(name = "email")
	private String email;
	@Column(name = "DT_CADASTRO")
	private Date dataCadastro;
	@Column(name = "DT_CO_CADASTRO")
	private Date dataConfirmacaoCadastro;

	@Column(name = "DT_ULTIMO_LOGIN")
	private Calendar dataUltimoLogin;

	@ElementCollection(targetClass = String.class)
	@JoinTable(name = "usuario_permissao", uniqueConstraints = { @UniqueConstraint(columnNames = {
			"usuario", "permissao" }) }, joinColumns = @JoinColumn(name = "usuario"))
	@Column(name = "permissao", length = 50)
	private Set<String> permissao = new HashSet<String>();

	public Set<String> getPermissao() {
		return permissao;
	}

	public void setPermissao(Set<String> permissao) {
		this.permissao = permissao;
	}

	public Calendar getDataUltimoLogin() {
		return dataUltimoLogin;
	}

	public void setDataUltimoLogin(Calendar dataUltimoLogin) {
		this.dataUltimoLogin = dataUltimoLogin;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataConfirmacaoCadastro() {
		return dataConfirmacaoCadastro;
	}

	public void setDataConfirmacaoCadastro(Date dataConfirmacaoCadastro) {
		this.dataConfirmacaoCadastro = dataConfirmacaoCadastro;
	}

}
