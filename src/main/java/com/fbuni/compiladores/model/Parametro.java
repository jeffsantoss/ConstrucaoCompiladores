package com.fbuni.compiladores.model;

public class Parametro extends Variavel {

    public Metodo metodo;

    public Metodo getMetodo() {
	return metodo;
    }

    public void setMetodo(Metodo metodo) {
	this.metodo = metodo;
    }

    @Override
    public boolean equals(Object obj) {

	Parametro outro = (Parametro) obj;

	if (this.getNome().equals(outro.getNome())) {
	    return true;
	}

	return false;
    }
}
