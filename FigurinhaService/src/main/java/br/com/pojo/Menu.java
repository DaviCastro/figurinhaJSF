package br.com.pojo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.annotation.AutoComplete;
import br.com.annotation.HbnDao;
import br.com.annotation.Service;
import br.com.dao.Identifiable;
import br.com.dao.hbn.HbnMenuDao;
import br.com.service.MenuServiceImpl;

@Entity
@Table(name = "menu")
@Service(service=MenuServiceImpl.class)
@HbnDao(hbnDao = HbnMenuDao.class)
public class Menu implements Serializable, Identifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -642312115827482492L;

	@Id
	@GeneratedValue()
	@Column(name = "id")
	private Integer id;

	@AutoComplete
	@Column(name = "nome")
	private String nome;


	@ManyToOne(targetEntity=Menu.class)
	@JoinColumn(name = "idMenuPai")
	private Menu menuPai;

	@Column(name = "url")
	private String url;

	@ElementCollection(targetClass = String.class)
	@JoinTable(name = "menu_permissao", uniqueConstraints = { @UniqueConstraint(columnNames = {
			"menu", "permissao" }) }, joinColumns = @JoinColumn(name="menu"))
	@Column(name = "permissao", length = 50)
	private Set<String> permissao = new HashSet<String>();
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public Menu getMenuPai() {
		return menuPai;
	}

	public void setMenuPai(Menu menuPai) {
		this.menuPai = menuPai;
	}

	public boolean possuiFilho() {
		return this.menuPai != null && this.menuPai.getId() != 0 && this.url != null;
	}

	public boolean isPai() {
		return this.menuPai == null || this.menuPai.getId() == 0;
	}

	@Override
	public String toString() {
		return  nome;
	}

	public Set<String> getPermissao() {
		return permissao;
	}

	public void setPermissao(Set<String> permissao) {
		this.permissao = permissao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((menuPai == null) ? 0 : menuPai.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((permissao == null) ? 0 : permissao.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Menu other = (Menu) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (menuPai == null) {
			if (other.menuPai != null)
				return false;
		} else if (!menuPai.equals(other.menuPai))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (permissao == null) {
			if (other.permissao != null)
				return false;
		} else if (!permissao.equals(other.permissao))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	
}
