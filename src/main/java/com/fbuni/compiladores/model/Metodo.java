package com.fbuni.compiladores.model;

public class Metodo {
    String nome;
    Long qtdParametros;

    public Metodo(String nome, Long qtdParametros) {
	super();
	this.nome = nome;
	this.qtdParametros = qtdParametros;
    }

    public Long getQtdParametros() {
	return qtdParametros;
    }

    public void setQtdParametros(Long qtdParametros) {
	this.qtdParametros = qtdParametros;
    }

    public String getNome() {
	return nome;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

}
