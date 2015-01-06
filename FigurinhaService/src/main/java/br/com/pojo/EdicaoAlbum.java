package br.com.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.annotation.HbnDao;
import br.com.annotation.Service;
import br.com.dao.Identifiable;
import br.com.dao.hbn.HbnEdicaoAlbumDao;
import br.com.service.EdicaoAlbumServiceImpl;

@Entity
@Table(name = "edicaoAlbum")
@Service(service = EdicaoAlbumServiceImpl.class)
@HbnDao(hbnDao = HbnEdicaoAlbumDao.class)
public class EdicaoAlbum implements Serializable, Identifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8009374339777337967L;

	public EdicaoAlbum() {
		super();
	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(targetEntity=Album.class)
	@JoinColumn(name="album")
	private Album album;

	@Column(name = "edicao")
	private String edicao;
	@Column(name = "descricao")
	private String descricao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getEdicao() {
		return edicao;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((edicao == null) ? 0 : edicao.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		EdicaoAlbum other = (EdicaoAlbum) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (edicao == null) {
			if (other.edicao != null)
				return false;
		} else if (!edicao.equals(other.edicao))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
