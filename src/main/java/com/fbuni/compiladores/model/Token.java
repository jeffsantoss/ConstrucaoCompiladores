package com.fbuni.compiladores.model;

public class Token {

	private String nomeToken;
	private String valorAtribuido;

	public Token(String nomeToken, String valorAtribuido) {
		this.nomeToken = nomeToken;
		if (!valorAtribuido.isEmpty()) {
			this.valorAtribuido = valorAtribuido;
		}
	}

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

	public String getValorAtribuido() {
		return valorAtribuido;
	}

	public void setValorAtribuido(String valorAtribuido) {
		this.valorAtribuido = valorAtribuido;
	}

}
