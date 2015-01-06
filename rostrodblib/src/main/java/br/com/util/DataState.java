package br.com.util;

/**
 * Enum com principal objetivo de diferenciar 
 * o estado em que a entidade está principalmente se é uma entidade nova ou 
 * se a mesma está sendo editada. Usado principalmente para decidir qual mensagem renderizar para o usuario
 *
 */
public enum DataState {
	ADD_STATE,EDIT_STATE,SEARCH_STATE;
}
