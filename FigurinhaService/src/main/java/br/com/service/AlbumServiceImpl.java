package br.com.service;

import br.com.dao.Dao;
import br.com.pojo.Album;

public class AlbumServiceImpl extends GenericService<Album, Dao<Album>>
		implements AlbumService {



	@Override
	public boolean consiste() {

		return false;
	}

}
