package br.com.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * Anotação que devera ser incluida no campo da classe pojo para tornar possivel a  utilização 
 * no metodo generico AutoComplete do Bean generico
 *
 */
@Target(value = { ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AutoComplete {

}
