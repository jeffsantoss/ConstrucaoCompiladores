package com.fbuni.compiladores.teste;

import java.util.List;

import org.junit.Test;

import com.fbuni.compiladores.servico.AnalisadorLexicoServicoImpl;

public class TestesServico {

	AnalisadorLexicoServicoImpl servico;

	@Test
	public void testeObterPalavrasLinhas() {
		servico = new AnalisadorLexicoServicoImpl();
		String linha = "console.log(\"hello word\", \"teste\")";

		List<String> palavras = servico.obterPalavrasDaLinha(linha);
		//
		// // remove espaço em branco dentro colchete
		// linha.replaceAll("\\s+(?=[^()]*\\))", "");
		//
		// linha.replaceAll(" ", "|");
		//
		// // remove espaço em branco dentro de espaços
		// linha.replaceAll("$1", " ");

		// String[] palavras = linha.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
		// String[] palavras = linha.split(" ");

		for (String palavra : palavras) {
			System.out.println(palavra + "\n");

		}

	}

}
