package com.fbuni.compiladores.servico;

import java.util.List;

import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.Metodo;

public interface AnalisadorSintaticoServico {

    /**
     * Faz a análise sintática
     * 
     * @param tabelaSimbolos
     * @return
     */

    public void analisar(List<Classificacao> tabelaSimbolos) throws Exception;

    /**
     * Retorna os métodos declarados com a quantidade de parâmetros
     * 
     * @param tabelaSimbolos
     * @return
     */

    public List<Metodo> obterMetodosDeclarados(List<Classificacao> tabelaSimbolos) throws Exception;

}
