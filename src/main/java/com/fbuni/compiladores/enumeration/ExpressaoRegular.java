package com.fbuni.compiladores.enumeration;

public enum ExpressaoRegular {

	// palavras reservadas da linguagem
	PALAVRA_RESERVADA("while|if|for|public|class|static|void"),
	// indentificador qualquer
	IDENTIFICADOR("[_|a-z|A-Z][a-z|A-Z|0-9|_]*"),
	// considera ponto flutuante
	NUMERICO("\"[-+]?[0-9]*\\\\.?[0-9]+\""),
	//
	LITERAL("\\\"(\\\\.|[^\\\"])*\\\""),
	// ABERTURA DE FUNÇÃO
	ABERTURA_FUNCAO_ESCOPO_INDEXACAO("[\\{|\\(|\\[]"),
	// DELIMITADOR DE FECHAMENTO
	FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO("[\\}|\\)|\\]]"),
	// DELIMITADOR DE FECHAMENTO
	SEPARADOR_DE_CLASSE("[.]"),
	// DELIMITADOR DE FECHAMENTO
	FIM_DE_LINHA("[;]"),
	// SEPARADORES DE CARATECERES
	SEPARADORES_LEXEMAS("[\\}|\\)|\\]|;|,|.|\\{|\\(|\\[]");

	private String key;

	private ExpressaoRegular(String key) {
		this.key = key;
	}

	public String getExpressao() {
		return key;
	}

}