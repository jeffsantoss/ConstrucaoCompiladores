package com.fbuni.compiladores.model;

public class Metodo {
    String nome;
    Integer qtdParametros;

    public Metodo(String nome, Integer qtdParametros) {
	super();
	this.nome = nome;
	this.qtdParametros = qtdParametros;
    }

    public String getNome() {
	return nome;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public Integer getQtdParametros() {
	return qtdParametros;
    }

    public void setQtdParametros(Integer qtdParametros) {
	this.qtdParametros = qtdParametros;
    }
}
