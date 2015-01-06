package br.com.dao.hbn;

import br.com.dao.HbnDao;
import br.com.exception.DbLibException;
import br.com.interfaceDao.EdicaoAlbumDao;
import br.com.pojo.EdicaoAlbum;

public class HbnEdicaoAlbumDao extends HbnDao<EdicaoAlbum> implements EdicaoAlbumDao {

	public HbnEdicaoAlbumDao(String banco) throws DbLibException {
		super(banco);
		// TODO Auto-generated constructor stub
	}

}
