package com.fbuni.compiladores.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.LinguagemAlvo;
import com.fbuni.compiladores.servico.AnalisadorLexicoServico;
import com.fbuni.compiladores.servico.AnalisadorSintaticoServico;

@Controller
@RequestMapping(value = "/fbuni/interpretador")
public class InterpretadorController {

    @Autowired
    AnalisadorLexicoServico servicoLexico;

    @Autowired
    AnalisadorSintaticoServico servicoSintatico;

    @RequestMapping(value = "/")
    public String principal() {
	return "principal";
    }

    @RequestMapping(value = "lexico", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> analisadorLexico(@RequestBody LinguagemAlvo linguagemAlvo) throws Exception {

	Map<String, Object> retorno = new HashMap<String, Object>();

	try {
	    retorno.put("tabelaSimbolos", tabelaSimbolosSemValorRepetido(servicoLexico.analisar(linguagemAlvo)));
	} catch (Exception e) {
	    throw new Exception(e.getMessage());
	}

	return retorno;
    }

    @RequestMapping(value = "sintatico", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> analisadorSintatico(@RequestBody LinguagemAlvo linguagemAlvo) throws Exception {

	Map<String, Object> retorno = new HashMap<String, Object>();

	try {

	    List<Classificacao> tabelaSimbolos = servicoLexico.analisar(linguagemAlvo);

	    servicoSintatico.analisar(tabelaSimbolos);

	    retorno.put("mensagem", "Código interpretado com sucesso");

	    retorno.put("metodosDeclarados", servicoSintatico.obterMetodosDeclarados(tabelaSimbolos));

	    retorno.put("tabelaSimbolos", tabelaSimbolosSemValorRepetido(tabelaSimbolos));

	} catch (Exception e) {
	    throw new Exception(e.getMessage());
	}

	return retorno;
    }

    List<Classificacao> tabelaSimbolosSemValorRepetido(List<Classificacao> tabela) {
	return tabela.stream().distinct().collect(Collectors.toList());
    }
}
