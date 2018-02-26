package com.fbuni.compiladores.model;

public class Classificacao {

    private String lexema;
    private String simbolo;
    private String significado;
    private Integer codigoSimbolo;
    private Token token;
    private Integer linha;
    private Integer coluna;;

    public Integer getLinha() {
	return linha;
    }

    public void setLinha(Integer linha) {
	this.linha = linha;
    }

    public Integer getColuna() {
	return coluna;
    }

    public void setColuna(Integer coluna) {
	this.coluna = coluna;
    }

    public String getLexema() {
	return lexema;
    }

    public void setLexema(String lexema) {
	this.lexema = lexema;
    }

    public String getSimbolo() {
	return simbolo;
    }

    public void setSimbolo(String simbolo) {
	this.simbolo = simbolo;
    }

    public String getSignificado() {
	return significado;
    }

    public void setSignificado(String significado) {
	this.significado = significado;
    }

    public Integer getCodigoSimbolo() {
	return codigoSimbolo;
    }

    public void setCodigoSimbolo(Integer codigo) {
	this.codigoSimbolo = codigo;
    }

    public Token getToken() {
	return token;
    }

    public void setToken(Token token) {
	this.token = token;
    }

}
