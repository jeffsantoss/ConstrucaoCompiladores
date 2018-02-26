package com.fbuni.compiladores.enumeration;

public enum ExpressoesRegulares {

	// palavras reservadas da linguagem
	PALAVRA_RESERVADA("while|if|for|public|class|static|void"),
	// indentificador qualquer
	IDENTIFICADOR("[_|a-z|A-Z][a-z|A-Z|0-9|_]*"),
	// considera ponto flutuante
	NUMERICO("\"[-+]?[0-9]*\\\\.?[0-9]+\""),
	// número inteiro
	INTEIRO("0|[1-9][0-9]*"),
	// caracteres de escape
	CARACETERES_A_DESCARTAR("[\\n| |\\t|\\r]"),
	// DELIMITADOR DE ABERTURA
	DELIMITADOR_ABERTURA("\\{|\\("),
	// DELIMITADOR DE FECHAMENTO
	DELIMITADOR_FECHAMENTO("\\}|\\)");

	private String key;

	private ExpressoesRegulares(String key) {
		this.key = key;
	}

	public String getExpressao() {
		return key;
	}

}
