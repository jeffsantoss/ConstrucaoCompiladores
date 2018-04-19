package com.fbuni.compiladores.model;

public class Expressao {

    public Classificacao numero1;
    public Classificacao operador;
    public Classificacao numero2;

    public Expressao(Classificacao numero1, Classificacao operador, Classificacao numero2) throws Exception {

	super();
	this.numero1 = numero1;
	this.operador = operador;
	this.numero2 = numero2;

	// if
	// (numero1.getToken().getNomeToken().equals(numero2.getToken().getNomeToken()))
	// {

	// } else {
	// throw new IllegalArgumentException("Expressão com tipos diferentes.");
	// }
    }

}
