package br.com.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import br.com.email.ConfiguracaoEmail;
import br.com.email.Destinatario;
import br.com.email.EmailUtil;
import br.com.exception.DbLibException;
import br.com.pojo.Usuario;
import br.com.service.UsuarioService;
import br.com.util.FacesUtil;
import br.com.util.Message;
import br.com.util.PropertiesUtil;

@ManagedBean(name = "usuarioBean")
@ViewScoped
public class UsuarioBean extends GenericBean<Usuario, UsuarioService> {

	private static final String CONFIRMACAO = "CONFIRMACAO";
	private static final String EMAIL = "EMAIL";
	private static final String CAMINHO = "http://localhost:8080/MedicalWeb/publico/pages/confirmaEmail.jsf?";
	private static final int horas = 24;

	private String confirmacaoSenha;

	Set<String> permissoesSelecionadas = new HashSet<String>();

	private List<String> permissoes;

	public UsuarioBean() {
		super();

		permissoes = getRoles();
	}

	@Override
	protected List<Message> validateSave(Usuario dataEdit) {

		List<Message> lMensagens = new ArrayList<Message>();

		if (!StringUtils.equals(confirmacaoSenha, dataEdit.getSenha())) {
			lMensagens.add(new Message("mensagem.usuario.incorreto",
					FacesMessage.SEVERITY_ERROR, "messages", true));
		}

		permissoes.addAll(permissoesSelecionadas);
		this.dataEdit.setPermissao(permissoesSelecionadas);
		this.dataEdit.setSenha(encryptPassSpring(dataEdit.getSenha()));
		return lMensagens;
	}

	public String encryptPassSpring(String pass) {
		PasswordEncoder encoder = new ShaPasswordEncoder(256);
		pass = encoder.encodePassword(pass, null);
		return pass;

	}

	public String novoUsuario() {
		dataEdit.setAtivo(false);
		dataEdit.setDataCadastro(new Date());
		dataEdit.setDataConfirmacaoCadastro(new Date());

		EmailUtil email = new EmailUtil();
		ConfiguracaoEmail config = new ConfiguracaoEmail();
		try {
			config = email.retornaConfiguracaoProperties(config);
			config.setAssunto("Cadastro de usuario");

			Calendar data = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String checkSum = dataEdit.getEmail() + df.format(data.getTime());
			data.add(Calendar.HOUR, horas);
			checkSum = checkSum + df.format(data.getTime());

			String mensagem = CAMINHO + EMAIL + "=" + dataEdit.getEmail() + "&"
					+ CONFIRMACAO + "=" + encryptPassSpring(checkSum);

			config.setMensagem(mensagem);
			List<Destinatario> destinatario = new ArrayList<Destinatario>();
			destinatario.add(new Destinatario(dataEdit.getLogin(), dataEdit
					.getEmail()));
			config.setListaDestinatario(destinatario);

			save();
			email.sendEmail(config);
		} catch (DbLibException e) {

		}

		return "";

	}

	public String confirmacaoCadastroPorEmail() {

		try {
			String confirmacao = String.valueOf(FacesUtil
					.getHttpServletRequest().getParameter(CONFIRMACAO));

			String email = String.valueOf(FacesUtil.getHttpServletRequest()
					.getParameter(EMAIL));

			dataSearch.setEmail(email);

			dataEdit = dataService.searchOne(dataSearch);

			if (dataEdit == null || dataEdit.isAtivo()) {
				FacesUtil.addMessage(new Message(
						"mensagem.usuario.confirmacaoInvalida",
						FacesMessage.SEVERITY_WARN, "messages", true));
				return "";
			}

			Calendar data = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String checkSum = dataEdit.getEmail()
					+ df.format(dataEdit.getDataCadastro());
			data.add(Calendar.HOUR, horas);
			checkSum = checkSum + df.format(data.getTime());

			if (confirmacao.equals(encryptPassSpring(checkSum))) {
				dataEdit.setAtivo(true);
				dataEdit.setDataConfirmacaoCadastro(new Date());
				Set<String> permissao = new HashSet<String>();
				permissao.add(PropertiesUtil.recuperaProperties().getProperty(
						"permissaoPadrao"));
				dataEdit.setPermissao(permissao);
				dataService.save(dataEdit);
			} else {
				FacesUtil.addMessage(new Message(
						"mensagem.usuario.confirmacaoInvalida",
						FacesMessage.SEVERITY_WARN, "messages", true));
				return "";
			}

		} catch (DbLibException e) {

		}

		FacesUtil.addMessage(new Message("mensagem.usuario.confirmado",
				FacesMessage.SEVERITY_INFO,
				new String[] { dataEdit.getLogin() }, null, true));

		return "usuarioConfirmado";
	}

	public Set<String> getPermissoesSelecionadas() {
		return permissoesSelecionadas;
	}

	public void setPermissoesSelecionadas(Set<String> permissoesSelecionadas) {
		this.permissoesSelecionadas = permissoesSelecionadas;
	}

	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}

	public List<String> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<String> permissoes) {
		this.permissoes = permissoes;
	}

	@Override
	public void messageSuccesSave() {
		FacesUtil.addMessage(new Message("mensagem.usuario.registrado",
				FacesMessage.SEVERITY_INFO,
				new String[] { dataEdit.getLogin() }, null, true));

	}

	@Override
	public void messageSuccessDelete() {
		// TODO Auto-generated method stub

	}

}
