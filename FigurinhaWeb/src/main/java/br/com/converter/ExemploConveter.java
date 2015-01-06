//package br.com.converter;
//
//import javax.faces.component.UIComponent;
//import javax.faces.context.FacesContext;
//import javax.faces.convert.Converter;
//import javax.faces.convert.FacesConverter;
//
//import br.com.pojo.Exemplo;
//
//@FacesConverter(value = "exemploConverter")
//public class ExemploConveter implements Converter {
//
//	@Override
//	public Object getAsObject(FacesContext facesContext, UIComponent component,
//			String value) {
//		if (value.trim().equals(""))
//			return null;
//		Exemplo exemplo = new Exemplo();
//
//		exemplo.setNome(value);	
//
//		return exemplo;
//	}
//
//	@Override
//	public String getAsString(FacesContext facesContext, UIComponent component,
//			Object object) {
//		if (object == null)
//			return null;
//
//		return ((Exemplo) object).getNome();
//
//	}
//
//}
