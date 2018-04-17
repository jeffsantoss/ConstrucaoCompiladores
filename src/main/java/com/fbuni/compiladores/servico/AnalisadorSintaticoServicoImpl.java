package com.fbuni.compiladores.servico;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.Token;

@Service
public class AnalisadorSintaticoServicoImpl implements AnalisadorSintaticoServico {

    @Override
    public Boolean analisar(List<Classificacao> tabelaSimbolos) throws Exception {

	analiseSintatica(tabelaSimbolos);

	return true;
    }

    private void analiseSintatica(List<Classificacao> tabelaSimbolos) throws IllegalArgumentException {

	Integer qtdLinhas = tabelaSimbolos.get(tabelaSimbolos.size() - 1).getLexema().getLinha();

	List<Classificacao> variaveis = tabelaSimbolos.stream().filter(
		c -> c.getToken().getNomeToken().equals("ID") || c.getToken().getNomeToken().equals("ID_VAR_LOCAL"))
		.collect(Collectors.toList());

	// valida se as váriaveis foram declarado ou não
	validaDeclaracaoDeVariavel(variaveis, tabelaSimbolos);

	// valida se mesma varíavel foi declarada mais de 1x
	validaDeclaracaoVariaveisRepetidas(variaveis);

	IntStream.range(0, qtdLinhas).forEach(linha -> {

	    List<Classificacao> classificacoesDaLinha = tabelaSimbolos.stream()
		    .filter(x -> x.getLexema().getLinha().equals(linha + 1)).collect(Collectors.toList());

	    validaPontoVirgula(classificacoesDaLinha.get(classificacoesDaLinha.size() - 1).getToken(), linha + 1);

	    // expressões com parênteses.
	    if (contemToken(classificacoesDaLinha, "OPERADOR")) {
		validaExpressoesComParentese(classificacoesDaLinha);
	    }

	});

    }

    private void validaDeclaracaoDeVariavel(List<Classificacao> variaveis, List<Classificacao> tabelaSimbolos) {

	for (Classificacao variavel : variaveis) {

	    Integer codToken = variavel.getToken().getCodToken();

	    Classificacao classificacaoAnterior = obterClassificacaoPorCodigo(tabelaSimbolos, codToken - 1);

	    if (classificacaoAnterior == null
		    || classificacaoAnterior.getToken().getNomeToken() != "PALAVRA_RESERVADA") {
		throw estourarExcessao(variavel.getLexema().getLinha(),
			"Variável " + variavel.getLexema().getPalavra() + " não declarada");
	    }
	}

    }

    private void validaDeclaracaoVariaveisRepetidas(List<Classificacao> identificadoresVariavel)
	    throws IllegalArgumentException {

	for (int i = 0; i < identificadoresVariavel.size(); i++) {
	    for (int j = i + 1; j < identificadoresVariavel.size(); j++) {
		if (identificadoresVariavel.get(i).getLexema().getPalavra()
			.equals(identificadoresVariavel.get(j).getLexema().getPalavra())) {
		    throw estourarExcessao(identificadoresVariavel.get(j).getLexema().getLinha(), "a variável >"
			    + identificadoresVariavel.get(i).getLexema().getPalavra() + "< já foi declarada");
		}
	    }
	}

    }

    private Classificacao obterClassificacaoPorCodigo(List<Classificacao> classificacoesDaLinha, Integer codToken) {

	for (Classificacao classificacao : classificacoesDaLinha) {
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

    private void validaExpressoesComParentese(List<Classificacao> classificacoesDaLinha)
	    throws IllegalArgumentException {

	if (contemToken(classificacoesDaLinha, "ID")) {

	}

	Integer numlinha = classificacoesDaLinha.get(0).getLexema().getLinha();

	Long qtdAbertura = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ABERTURA_FUNCAO_ESCOPO_INDEXACAO")).count();

	Long qtdFechamento = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO")).count();

	if (qtdAbertura > qtdFechamento) {
	    throw estourarExcessao(numlinha, "expressão inválida, falta parênteses a fechar");
	} else if (qtdFechamento > qtdAbertura) {
	    throw estourarExcessao(numlinha, "expressão inválida, falta parênteses a abrir");
	}

    }

    private void validaPontoVirgula(Token token, Integer linha) {

	if (!token.getNomeToken().equals("FIM_DE_LINHA")) {
	    throw estourarExcessao(linha, "não contém ponto e virgula no final da linha");
	}
    }

    private IllegalArgumentException estourarExcessao(Integer linha, String mensagem) {

	StringBuilder str = new StringBuilder();
	str.append("Erro na linha: ");
	str.append(linha);

	str.append("\ndescrição do erro: " + mensagem);

	IllegalArgumentException e = new IllegalArgumentException(str.toString());

	return e;
    }

}