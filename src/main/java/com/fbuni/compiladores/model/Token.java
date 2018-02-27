package com.fbuni.compiladores.model;

public class Token {

	private String nomeToken;
	private Integer valorAtribuido;

	public Token(String nomeToken) {
		this.nomeToken = nomeToken;
	}

	public Token() {
		// TODO Auto-generated constructor stub
	}

	public String getNomeToken() {
		return nomeToken;
	}

	public void setNomeToken(String nomeToken) {
		this.nomeToken = nomeToken;
	}

	public Integer getValorAtribuido() {
		return valorAtribuido;
	}

	public void setValorAtribuido(Integer valorAtribuido) {
		this.valorAtribuido = valorAtribuido;
	}

}
