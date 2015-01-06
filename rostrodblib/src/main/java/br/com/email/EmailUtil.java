package br.com.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import br.com.exception.DbLibException;
import br.com.util.PropertiesUtil;

public class EmailUtil {

	protected static final Logger logger = Logger.getLogger(EmailUtil.class);



	public ConfiguracaoEmail retornaConfiguracaoProperties(
			ConfiguracaoEmail config) {
		
		if (config == null)
			config = new ConfiguracaoEmail();

		try {
			Properties prop = PropertiesUtil.recuperaProperties();
			config.setUsuario(prop.getProperty("usuario"));
			config.setPortaSmtp(Integer.parseInt(prop.getProperty("smtpPort")));
			config.setSenha(prop.getProperty("senha"));
			config.setSsl(Boolean.valueOf(prop.getProperty("ssl")));
			config.setNomeOrigem(prop.getProperty("usuario"));
			config.setEmailOrigem(prop.getProperty("usuario"));
			config.setSmtpHost(prop.getProperty("smtpHost"));
		} catch (DbLibException e) {
			e.printStackTrace();
		}

		return config;
	}

	public void sendEmail(ConfiguracaoEmail configuracaoEmail)
			throws DbLibException {
		try {
			configuracaoEmail = retornaConfiguracaoProperties(configuracaoEmail);
			SimpleEmail email = new SimpleEmail();
			/**
			 * Host do Provedor de email
			 */
			email.setHostName(configuracaoEmail.getSmtpHost());
			/**
			 * Quando a porta utilizada não é a padrão (gmail = 465)
			 */
			email.setSmtpPort(configuracaoEmail.getPortaSmtp());
			/**
			 * Adicione os destinatários
			 */
			for (Destinatario destinatario : configuracaoEmail
					.getListaDestinatario()) {
				email.addTo(destinatario.email, destinatario.nome);
			}

			/**
			 * Configure o seu email do qual enviará
			 */
			email.setFrom(configuracaoEmail.getEmailOrigem(),
					configuracaoEmail.getNomeOrigem());
			/**
			 * 
			 */
			email.setSubject(configuracaoEmail.getAssunto());
			/**
			 * Adicione a mensagem do email
			 */
			email.setMsg(configuracaoEmail.getMensagem());
			/**
			 * Autenticar
			 */
			email.setSSLOnConnect(configuracaoEmail.isSsl());
			email.setAuthentication(configuracaoEmail.getUsuario(),
					configuracaoEmail.getSenha());

			/**
			 * Envio
			 */
			email.send();
			logger.debug("Email enviado " + configuracaoEmail.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao enviar email", e);
		}
	}

	public static void main(String[] args) {

		
		
	
		
		
		
		ConfiguracaoEmail config = new ConfiguracaoEmail();

		config.setAssunto("teste");
		config.setEmailOrigem("davilopescastro@gmail.com");
		config.setMensagem("www.google.com");
		config.setNomeOrigem("davidson");
		config.setSenha("!!as973100");
		config.setSsl(true);
		config.setUsuario("davilopescastro@gmail.com");
		List<Destinatario> listaDestinatario = new ArrayList<Destinatario>();
		listaDestinatario.add(new Destinatario("Davi",
				"davilopescastro@gmail.com"));
		config.setListaDestinatario(listaDestinatario);
		config.setSmtpHost("smtp.gmail.com");
		config.setPortaSmtp(465);

		EmailUtil emailUtil = new EmailUtil();
		try {
			emailUtil.sendEmail(config);
		} catch (DbLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
