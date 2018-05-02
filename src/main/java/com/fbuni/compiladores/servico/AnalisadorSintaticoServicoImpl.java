package com.fbuni.compiladores.servico;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.Expressao;

@Service
public class AnalisadorSintaticoServicoImpl implements AnalisadorSintaticoServico {

    @Override
    public Boolean analisar(List<Classificacao> tabelaSimbolos) throws Exception {

	Classificacao classificacaoComErro = obterClassificacaoPoToken(tabelaSimbolos, "ERROR");

	if (classificacaoComErro != null) {
	    throw estourarExcessao(classificacaoComErro.getLexema().getLinha(),
		    "Existe erro na tabela de símbolos. Favor Corrigir.");
	}

	analiseSintatica(tabelaSimbolos);

	return true;
    }

    private void analiseSintatica(List<Classificacao> tabelaSimbolos) throws IllegalArgumentException {

	Integer qtdLinhas = tabelaSimbolos.get(tabelaSimbolos.size() - 1).getLexema().getLinha();

	List<Classificacao> variaveis = tabelaSimbolos.stream().filter(
		c -> c.getToken().getNomeToken().equals("ID") || c.getToken().getNomeToken().equals("ID_VAR_LOCAL"))
		.distinct().collect(Collectors.toList());

	// valida se as váriaveis foram declarado ou não
	validaDeclaracaoDeVariavel(variaveis, tabelaSimbolos);

	// valida se mesma varíavel foi declarada mais de 1x
	validaDeclaracaoVariaveisRepetidas(variaveis, tabelaSimbolos);

	IntStream.range(0, qtdLinhas).forEach(linha -> {

	    List<Classificacao> classificacoesDaLinhaSemDuplicacao = tabelaSimbolos.stream()
		    .filter(x -> x.getLexema().getLinha().equals(linha + 1)).distinct().collect(Collectors.toList());

	    validaPontoVirgula(classificacoesDaLinhaSemDuplicacao, linha + 1);

	    // pega o índice pelo nome do token e a linha;

	    Integer indicePontoVirgulaLinhaAnterior = indiceClassificao(tabelaSimbolos, "FIM_DE_LINHA", linha);

	    Integer indicePontoVirgulaLinhaPosterior = indiceClassificao(tabelaSimbolos, "FIM_DE_LINHA", linha + 1);

	    List<Classificacao> classificacaoDaLinha = new ArrayList<Classificacao>();

	    if (indicePontoVirgulaLinhaAnterior != -1) {
		for (int i = indicePontoVirgulaLinhaAnterior + 1; i <= indicePontoVirgulaLinhaPosterior; i++) {
		    classificacaoDaLinha.add(tabelaSimbolos.get(i));
		}
	    } else {
		for (int i = 0; i <= indicePontoVirgulaLinhaPosterior; i++) {
		    classificacaoDaLinha.add(tabelaSimbolos.get(i));
		}
	    }

	    // expressões.
	    if (contemToken(classificacaoDaLinha, "OPERADOR") || contemToken(classificacaoDaLinha, "ATRIBUIÇÃO")) {

		// pega depois da atribuição

		Integer indiceAposAtribuicao = indiceClassificao(classificacaoDaLinha, "ATRIBUIÇÃO") + 1;
		Integer indiceFimDaLinha = indiceClassificao(classificacaoDaLinha, "FIM_DE_LINHA");

		List<Classificacao> classificaoDeExpressoes = classificacaoDaLinha.subList(indiceAposAtribuicao,
			indiceFimDaLinha);

		validaExpressoes(classificaoDeExpressoes);
	    }

	});

    }

    private void validaDeclaracaoDeVariavel(List<Classificacao> variaveis, List<Classificacao> tabelaSimbolos) {

	for (Classificacao variavel : variaveis) {

	    Integer codToken = variavel.getToken().getCodToken();

	    Classificacao classificacaoAnterior = obterClassificacaoPorCodigo(tabelaSimbolos, codToken - 1);

	    if (classificacaoAnterior != null && classificacaoAnterior.getToken().getNomeToken().equals("SEPARADOR")) {
		continue;
	    }

	    if (classificacaoAnterior == null
		    || classificacaoAnterior.getToken().getNomeToken() != "PALAVRA_RESERVADA") {
		throw estourarExcessao(variavel.getLexema().getLinha(),
			"Variável >" + variavel.getLexema().getPalavra() + "< não declarada");
	    }
	}

    }

    private void validaDeclaracaoVariaveisRepetidas(List<Classificacao> identificadoresVariavel,
	    List<Classificacao> tabelaSimbolos) throws IllegalArgumentException {

	for (int i = 0; i < identificadoresVariavel.size(); i++) {
	    for (int j = i + 1; j < identificadoresVariavel.size(); j++) {
		if (identificadoresVariavel.get(i).getLexema().getPalavra()
			.equals(identificadoresVariavel.get(j).getLexema().getPalavra())) {

		    Classificacao classificacao = obterClassificacaoPorCodigo(tabelaSimbolos,
			    identificadoresVariavel.get(j).getToken().getCodToken());

		    Integer indiceClassificacaoIgual = tabelaSimbolos.indexOf(classificacao);

		    if (tabelaSimbolos.get(indiceClassificacaoIgual - 1).getToken()
			    .getNomeToken() == "PALAVRA_RESERVADA") {
			throw estourarExcessao(identificadoresVariavel.get(j).getLexema().getLinha(), "a variável >"
				+ identificadoresVariavel.get(i).getLexema().getPalavra() + "< já foi declarada");
		    }
		}
	    }
	}

    }

    private Classificacao obterClassificacaoPorCodigo(List<Classificacao> classificacaoes, Integer codToken) {

	for (Classificacao classificacao : classificacaoes) {
	    if (classificacao.getToken().getCodToken().equals(codToken)) {
		return classificacao;
	    }
	}

	return null;
    }

    private Boolean contemToken(List<Classificacao> classificacoesDaLinha, String nomeToken) {

	for (Classificacao classificacao : classificacoesDaLinha) {
	    if (classificacao.getToken().getNomeToken().equals(nomeToken)) {
		return true;
	    }
	}

	return false;
    }

    private Classificacao obterClassificacaoPoToken(List<Classificacao> classificacoesDaLinha, String nomeToken) {

	for (Classificacao classificacao : classificacoesDaLinha) {
	    if (classificacao.getToken().getNomeToken().equals(nomeToken)) {
		return classificacao;
	    }
	}

	return null;
    }

    private Boolean contemPalavra(List<Classificacao> classificacoesDaLinha, String palavra) {

	for (Classificacao classificacao : classificacoesDaLinha) {
	    if (classificacao.getLexema().getPalavra().equals(palavra)) {
		return true;
	    }
	}

	return false;
    }

    private void validaExpressoes(List<Classificacao> classificacoesDaLinha) throws IllegalArgumentException {

	Integer numlinha = classificacoesDaLinha.get(0).getLexema().getLinha();

	// parênteses
	Long qtdAbertura = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ABERTURA_FUNCAO_ESCOPO_INDEXACAO")).count();

	Long qtdFechamento = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO")).count();

	if (qtdAbertura > qtdFechamento) {
	    throw estourarExcessao(numlinha, "expressão inválida, falta parênteses a fechar");
	} else if (qtdFechamento > qtdAbertura) {
	    throw estourarExcessao(numlinha, "expressão inválida, falta parênteses a abrir");
	}

	List<Classificacao> parenteseFechando = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO"))
		.collect(Collectors.toList());

	List<Classificacao> parenteseAbrindo = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ABERTURA_FUNCAO_ESCOPO_INDEXACAO"))
		.collect(Collectors.toList());

	for (int i = 0; i < parenteseFechando.size(); i++) {

	    Integer indiceFechando = indiceClassificao(classificacoesDaLinha,
		    parenteseFechando.get(i).getToken().getCodToken());
	    Integer indiceAbrindo = indiceClassificao(classificacoesDaLinha,
		    parenteseAbrindo.get(i).getToken().getCodToken());

	    Boolean contemOperador = false;

	    for (int j = indiceAbrindo; j < indiceFechando; j++) {
		if (classificacoesDaLinha.get(j).getToken().getNomeToken().equals("OPERADOR")) {
		    contemOperador = true;
		}
	    }

	    if (!contemOperador) {
		throw estourarExcessao(numlinha, "não contém operador dentro da expressão entre '('' ')'' ");
	    }

	    try {
		if (classificacoesDaLinha.get(indiceFechando + 1).getToken()
			.getNomeToken() == "FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO")
		    continue;

		if (classificacoesDaLinha.get(indiceFechando + 1).getToken().getNomeToken() != "OPERADOR") {
		    throw estourarExcessao(numlinha, "Verifique se existe operadores entre as expressões");
		}
	    } catch (Exception e) {

	    }

	}

	List<Classificacao> classificacoesSemParentese = classificacoesDaLinha.stream().filter(item -> {

	    if (item.getToken().getNomeToken().equals("ABERTURA_FUNCAO_ESCOPO_INDEXACAO")
		    || item.getToken().getNomeToken().equals("FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO")) {
		return false;
	    }

	    return true;

	}).collect(Collectors.toList());

	if (classificacoesSemParentese.isEmpty() || classificacoesSemParentese == null) {
	    throw estourarExcessao(numlinha, "Não contém expressões dentro dos parênteses");
	}

	List<Classificacao> operadores = classificacoesSemParentese.stream()
		.filter(item -> item.getToken().getNomeToken().equals("OPERADOR")).collect(Collectors.toList());

	List<Expressao> expressoes = new ArrayList<Expressao>();

	for (int i = 0; i < operadores.size(); i++) {

	    Integer indiceOperador = indiceClassificao(classificacoesSemParentese,
		    operadores.get(i).getToken().getCodToken());

	    try {
		expressoes.add(new Expressao(classificacoesSemParentese.get(indiceOperador - 1), operadores.get(i),
			classificacoesSemParentese.get(indiceOperador + 1)));

	    } catch (IllegalArgumentException e) {
		throw estourarExcessao(numlinha, e.getMessage());
	    } catch (Exception e) {
		throw estourarExcessao(numlinha, "Expressão inválida! Não foi possível montar uma expressão");
	    }

	}

    }

    private void validaPontoVirgula(List<Classificacao> classificacoesDaLinha, Integer linha) {

	if (contemPalavra(classificacoesDaLinha, "{") || contemPalavra(classificacoesDaLinha, "}")) {
	    return;
	}

	if (!classificacoesDaLinha.get(classificacoesDaLinha.size() - 1).getToken().getNomeToken()
		.equals("FIM_DE_LINHA")) {
	    throw estourarExcessao(linha, "não contém ponto e virgula no final da linha");
	}
    }

    private IllegalArgumentException estourarExcessao(Integer linha, String mensagem) {

	StringBuilder str = new StringBuilder();
	str.append("Erro na linha: ");
	str.append(linha);
	str.append("<br> DESCRIÇÃO: " + mensagem);

	IllegalArgumentException e = new IllegalArgumentException(str.toString());

	return e;
    }

    private Integer indiceClassificao(List<Classificacao> classificacoes, Integer codToken) {

	Integer indice = 0;

	for (Classificacao classificacao : classificacoes) {

	    if (classificacao.getToken().getCodToken().equals(codToken)) {
		return indice;
	    }

	    indice++;
	}

	return -1;
    }

    private Integer indiceClassificao(List<Classificacao> classificacoes, String nomeToken) {

	Integer indice = 0;

	for (Classificacao classificacao : classificacoes) {

	    if (classificacao.getToken().getNomeToken().equals(nomeToken)) {
		return indice;
	    }

	    indice++;
	}

	return -1;
    }

    private Integer indiceClassificao(List<Classificacao> classificacoes, String nomeToken, Integer linha) {

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

}
