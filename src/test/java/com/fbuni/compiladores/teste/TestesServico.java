package com.fbuni.compiladores.teste;

import org.junit.Test;

import com.fbuni.compiladores.servico.AnalisadorLexicoServicoImpl;

public class TestesServico {

    AnalisadorLexicoServicoImpl servico;

    @Test
    public void testeQuebrarLinhas() {
	String valor = "1.91";
	String valor2 = "191";

	System.out.println("1.911111".matches("[-+]?[0-9]*\\.?[0-9]+"));

    }

}
