package br.com.factory;

import java.util.HashMap;
import java.util.Map;

import br.com.conexao.Db4oConexao;
import br.com.exception.DbLibException;

import com.db4o.Db4oEmbedded;

public class Db4oConexaoFactory {

	private static String CAMINHO_BANCO = "./db4o";
	private static String EXTENSAO = ".db4o";

	private static Map<String, Db4oConexao> mapConexoes = new HashMap<String, Db4oConexao>();

	private static Db4oConexao getConexao(String banco)
			throws DbLibException {
		try {
			return new Db4oConexao(Db4oEmbedded.openFile(
					Db4oEmbedded.newConfiguration(), CAMINHO_BANCO + "/"
							+ banco + EXTENSAO));

		} catch (Exception e) {
			throw new DbLibException("Erro ao abrir conexão com banco: "
					+ banco, e);
		}

	}

	public static Db4oConexao adquirirConexao(String banco)
			throws DbLibException {
		if (mapConexoes.get(banco) == null)
			mapConexoes.put(banco, getConexao(banco));
		return mapConexoes.get(banco);

	}

}
