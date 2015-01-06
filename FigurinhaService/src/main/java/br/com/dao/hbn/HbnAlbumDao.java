package br.com.dao.hbn;

import br.com.dao.HbnDao;
import br.com.exception.DbLibException;
import br.com.interfaceDao.AlbumDao;
import br.com.pojo.Album;

public class HbnAlbumDao extends HbnDao<Album> implements AlbumDao {

	public HbnAlbumDao(String banco) throws DbLibException {
		super(banco);
		// TODO Auto-generated constructor stub
	}

}
