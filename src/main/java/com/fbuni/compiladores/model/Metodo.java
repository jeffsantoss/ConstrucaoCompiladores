package com.fbuni.compiladores.model;

import java.util.List;

import com.fbuni.compiladores.enumeration.TipoVariavel;

public class Metodo {

    private String nome;
    private List<Parametro> parametros;
    private List<Classificacao> escopo;
    private TipoVariavel tipoRetorno;

    public TipoVariavel getTipoRetorno() {
	return tipoRetorno;
    }

    public void setTipoRetorno(TipoVariavel tipoRetorno) {
	this.tipoRetorno = tipoRetorno;
    }

    public List<Classificacao> getEscopo() {
	return escopo;
    }

    public void setEscopo(List<Classificacao> escopo) {

	this.escopo = escopo;
    }

    public Metodo(String nome, List<Parametro> parametros) {
	super();
	this.nome = nome;
	this.parametros = parametros;
    }

    public Metodo() {
	// TODO Auto-generated constructor stub
    }

    public String getNome() {
	return nome;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public List<Parametro> getParametros() {
	return parametros;
    }

    public void setParametros(List<Parametro> parametros) {
	this.parametros = parametros;
    }

}
