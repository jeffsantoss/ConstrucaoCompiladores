package com.fbuni.compiladores.util;

import java.util.List;

import com.fbuni.compiladores.model.Classificacao;

public class CompiladoresUtil {

    public static IllegalArgumentException estourarExcessao(Integer linha, String mensagem) {

	StringBuilder str = new StringBuilder();
	str.append("Erro na linha: ");
	str.append(linha);
	str.append("<br> DESCRIÇÃO: " + mensagem);

	IllegalArgumentException e = new IllegalArgumentException(str.toString());

	return e;
    }

    public static Integer indiceClassificao(List<Classificacao> classificacoes, Integer codToken) {

	Integer indice = 0;

	for (Classificacao classificacao : classificacoes) {

	    if (classificacao.getToken().getCodToken().equals(codToken)) {
		return indice;
	    }

	    indice++;
	}

	return -1;
    }

    public static Integer indiceClassificao(List<Classificacao> classificacoes, String palavra, String nomeToken) {

	Integer indice = 0;

	for (Classificacao classificacao : classificacoes) {

	    if (classificacao.getLexema().getPalavra().equals(palavra)
		    && classificacao.getToken().getNomeToken().equals(nomeToken)) {
		return indice;
	    }

	    indice++;
	}

	return -1;
    }

    public static Integer indiceClassificao(List<Classificacao> classificacoes, String nomeToken, Integer linha) {

	Integer indice = 0;

	for (Classificacao classificacao : classificacoes) {

	    if (classificacao.getToken().getNomeToken().equals(nomeToken)
		    && classificacao.getLexema().getLinha().equals(linha)) {
		return indice;
	    }

	    indice++;
	}

	return -1;
    }

    public static Integer indiceClassificao(List<Classificacao> classificacoes, Integer indiceStart, String palavra) {

	for (int i = indiceStart; i < classificacoes.size(); i++) {

	    Classificacao classificacao = classificacoes.get(i);

	    if (classificacao.getLexema().getPalavra().equals(palavra)) {
		return i;
	    }

	}

	return -1;
    }

    public static Boolean contemToken(List<Classificacao> classificacoesDaLinha, String nomeToken) {

	for (Classificacao classificacao : classificacoesDaLinha) {
	    if (classificacao.getToken().getNomeToken().equals(nomeToken)) {
		return true;
	    }
	}

	return false;
    }

    public static Classificacao obterClassificacaoPorToken(List<Classificacao> classificacoesDaLinha,
	    String nomeToken) {

	for (Classificacao classificacao : classificacoesDaLinha) {
	    if (classificacao.getToken().getNomeToken().equals(nomeToken)) {
		return classificacao;
	    }
	}

	return null;
    }

    public static Boolean contemPalavra(List<Classificacao> classificacoesDaLinha, String palavra) {

	for (Classificacao classificacao : classificacoesDaLinha) {
	    if (classificacao.getLexema().getPalavra().equals(palavra)) {
		return true;
	    }
	}

	return false;
    }

}
