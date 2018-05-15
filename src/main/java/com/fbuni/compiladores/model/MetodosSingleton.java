package com.fbuni.compiladores.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MetodosSingleton {

    public List<Metodo> metodos;

    public MetodosSingleton() {
	metodos = new ArrayList<Metodo>();
    }

    public List<Metodo> getInstancia() {
	return metodos;
    }

    public void setInstancia(List<Metodo> metodos) {
	this.metodos = metodos;
    }

}
