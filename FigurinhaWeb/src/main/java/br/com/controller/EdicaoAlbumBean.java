package br.com.controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.exception.DbLibException;
import br.com.pojo.Album;
import br.com.pojo.EdicaoAlbum;
import br.com.service.EdicaoAlbumService;
import br.com.service.Service;
import br.com.service.ServiceFactory;

@ManagedBean(name = "edicaoAlbumBean")
@ViewScoped
public class EdicaoAlbumBean extends
		GenericBean<EdicaoAlbum, EdicaoAlbumService> {

	public EdicaoAlbumBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	private List<?> listaAlbum;

	private Service<?> albumService = ServiceFactory.getService(Album.class);



	@Override
	public void messageSuccesSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageSuccessDelete() {
		// TODO Auto-generated method stub

	}
	public List<?> getListaAlbum() {
		if (listaAlbum == null)
			try {
				listaAlbum = albumService.findAll();
			} catch (DbLibException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return listaAlbum;
	}

	public void setListaAlbum(List<?> listaAlbum) {
		this.listaAlbum = listaAlbum;
	}

}
