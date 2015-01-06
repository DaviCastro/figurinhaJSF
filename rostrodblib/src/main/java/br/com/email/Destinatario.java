package br.com.email;

public class Destinatario {
	String nome;
	String email;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Destinatario(String nome, String email) {
		super();
		this.nome = nome;
		this.email = email;
	}

}
