package com.fbuni.compiladores.model;

import com.fbuni.compiladores.enumeration.TipoVariavel;

public class Variavel<T> {

    private String nome;
    private TipoVariavel tipo;
    private T valor;

    public T getValor() {
	return valor;
    }

    public void setValor(T valor) {
	this.valor = valor;
    }

    public String getNome() {
	return nome;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public TipoVariavel getTipo() {
	return tipo;
    }

    public void setTipo(TipoVariavel tipo) {
	this.tipo = tipo;
    }

}
