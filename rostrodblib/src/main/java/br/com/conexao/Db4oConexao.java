package br.com.conexao;

import com.db4o.ObjectContainer;

public class Db4oConexao {
	
	private ObjectContainer conexao;

	public Db4oConexao(ObjectContainer conexao) {
		this.conexao = conexao;
	}
	
	
	public ObjectContainer getSessao(){
		return conexao.ext().openSession();
	}
	
}
