package br.com.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import br.com.exception.DbLibException;
import br.com.pojo.Usuario;
import br.com.service.Service;
import br.com.service.ServiceFactory;

@ManagedBean(name = "loginBean")
@RequestScoped
/**
 * 
 * Bean responsavel pelo login
 * se baseia no spring security
 *
 */
public class LoginBean {

	private String username;
	private String password;

	public LoginBean() {
		super();
	}

	/**
	 * Metodo que faz o login com base nos parametros do spring security
	 * 
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public String doLogin() throws IOException, ServletException {
		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();
		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
				.getRequestDispatcher("/j_spring_security_check?j_username="
						+ username + "&j_password=" + password);
		dispatcher.forward((ServletRequest) context.getRequest(),
				(ServletResponse) context.getResponse());

		if (isUsuarioLogado()) {

			atualizaDataLoginUsuario();

		}

		FacesContext.getCurrentInstance().responseComplete();
		// It's OK to return null here because Faces is just going to exit.
		return null;
	}

	/**
	 * Metodo que faz o logout com base no spring security
	 * 
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String doLogout() throws ServletException, IOException {

		ExternalContext context = FacesContext.getCurrentInstance()
				.getExternalContext();
		RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
				.getRequestDispatcher("/j_spring_security_logout");
		dispatcher.forward((ServletRequest) context.getRequest(),
				(ServletResponse) context.getResponse());
		FacesContext.getCurrentInstance().responseComplete();

		return null;
	}

	/**
	 * Verifica se o usuario est� logado
	 * 
	 * @return
	 */
	public boolean isUsuarioLogado() {

		return SecurityContextHolder.getContext().getAuthentication() != null ? !StringUtils
				.equals(SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal().toString(), "anonymousUser") : false;
	}

	/**
	 * Recupera o login do usuario logado
	 * 
	 * @return
	 */
	public String getNomeUsuario() {

		return SecurityContextHolder.getContext().getAuthentication() != null ? ((User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal()).getUsername()
				: null;
	}

	private void atualizaDataLoginUsuario() {
		@SuppressWarnings("unchecked")
		Service<Usuario> usuarioService = (Service<Usuario>) ServiceFactory
				.getService(Usuario.class);

		Usuario usuario = new Usuario();
		usuario.setLogin(getNomeUsuario());
		usuario.setAtivo(true);
		try {
			usuario = usuarioService.searchOne(usuario);

			
				Calendar diaCorrente = Calendar.getInstance();
				if (usuario.getDataUltimoLogin()==null|| usuario.getDataUltimoLogin().get(Calendar.DATE) != diaCorrente
						.get(Calendar.DATE)
						|| usuario.getDataUltimoLogin().get(Calendar.YEAR) != diaCorrente
								.get(Calendar.YEAR)
						|| usuario.getDataUltimoLogin().get(Calendar.MONTH) != diaCorrente
								.get(Calendar.MONTH)) {

					sorteiaFigurinhaUsuario(usuario);

				}
			

			usuario.setDataUltimoLogin(Calendar.getInstance());
			usuarioService.save(usuario);
		} catch (DbLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sorteiaFigurinhaUsuario(Usuario usuario) {
		System.out.println("ee");

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
