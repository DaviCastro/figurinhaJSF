package br.com.conexao;

import br.com.exception.DbLibException;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class Conexao {

	private static ObjectContainer root;

	private static String file;

	public static String getFile() {
		return file;
	}

	public static void setFile(String file) {
		Conexao.file = file;
	}

	public static ObjectContainer getInstance() throws DbLibException {

		if (root == null) {
			root = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), file);
		}

		return root;

	}

	public static ObjectContainer getSession() throws DbLibException {
		return getInstance().ext().openSession();
	}

	public static boolean close() {
		return root.close();
	}

}
