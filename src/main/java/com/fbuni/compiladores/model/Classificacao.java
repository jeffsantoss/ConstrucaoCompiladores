package com.fbuni.compiladores.model;

public class Classificacao {

	private Lexema lexema;
	private Token token;

	public Token getToken() {
		return token;
	}

	public Lexema getLexema() {
		return lexema;
	}

	public void setLexema(Lexema lexema) {
		this.lexema = lexema;
	}

	public void setToken(Token token) {
		this.token = token;
	}

}
