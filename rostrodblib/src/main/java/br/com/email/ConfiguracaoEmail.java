package br.com.email;

import java.util.List;

public class ConfiguracaoEmail {

	private String smtpHost;
	private int portaSmtp;
	private String emailOrigem;
	private String nomeOrigem;

	private List<Destinatario> listaDestinatario;
	private String assunto;
	private String mensagem;
	private boolean ssl;
	private String usuario;
	private String senha;

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getPortaSmtp() {
		return portaSmtp;
	}

	public void setPortaSmtp(int portaSmtp) {
		this.portaSmtp = portaSmtp;
	}

	public String getEmailOrigem() {
		return emailOrigem;
	}

	public String getNomeOrigem() {
		return nomeOrigem;
	}

	public void setNomeOrigem(String nomeOrigem) {
		this.nomeOrigem = nomeOrigem;
	}

	public void setEmailOrigem(String emailOrigem) {
		this.emailOrigem = emailOrigem;
	}

	public List<Destinatario> getListaDestinatario() {
		return listaDestinatario;
	}

	public void setListaDestinatario(List<Destinatario> listaDestinatario) {
		this.listaDestinatario = listaDestinatario;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}
