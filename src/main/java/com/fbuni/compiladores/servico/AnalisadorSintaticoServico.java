package com.fbuni.compiladores.servico;

import java.util.List;

import com.fbuni.compiladores.model.Classificacao;

public interface AnalisadorSintaticoServico {

    /**
     * faz a análise sintática
     * 
     * @param tabelaSimbolos
     * @return
     */
    public Boolean analisar(List<Classificacao> tabelaSimbolos) throws Exception;

}
