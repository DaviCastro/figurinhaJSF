package br.com.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.pojo.Album;
import br.com.service.AlbumService;

@ManagedBean(name = "albumBean")
@ViewScoped
public class AlbumBean extends GenericBean<Album, AlbumService> {

	public AlbumBean() {
		super();
	}

	@Override
	public void messageSuccesSave() {
		
	}

	@Override
	public void messageSuccessDelete() {
		// TODO Auto-generated method stub

	}
	
	
	
	

}
