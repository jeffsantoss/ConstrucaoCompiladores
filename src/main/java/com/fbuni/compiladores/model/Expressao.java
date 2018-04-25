package com.fbuni.compiladores.model;

import java.util.Arrays;
import java.util.List;

public class Expressao {

    public Classificacao numero1;
    public Classificacao operador;
    public Classificacao numero2;

    public Expressao(Classificacao numero1, Classificacao operador, Classificacao numero2) throws Exception {

	super();

	tiposAceitesExpressao(numero1, operador, numero2);

	this.numero1 = numero1;
	this.operador = operador;
	this.numero2 = numero2;

    }

    private void tiposAceitesExpressao(Classificacao numero1, Classificacao operador, Classificacao numero2)
	    throws Exception {

	List<String> tiposAceitos = Arrays.asList("LITERAL", "INTEIRO", "ID", "ID_VAR_LOCAL");

	Boolean primeiroNumeroCorreto = false;
	Boolean segundoNumeroCorreto = false;

	for (String nomeToken : tiposAceitos) {
	    if (numero1.getToken().getNomeToken().equals(nomeToken)) {
		primeiroNumeroCorreto = true;
	    }
	}

	for (String nomeToken : tiposAceitos) {
	    if (numero2.getToken().getNomeToken().equals(nomeToken)) {
		segundoNumeroCorreto = true;
	    }
	}

	if (!primeiroNumeroCorreto || !segundoNumeroCorreto) {
	    throw new IllegalArgumentException(
		    "Expressão inválida, verifique os tipos da sua expressão, tipos aceitos: LITERAL, INTEIRO, FLOAT");
	}

    }

}
