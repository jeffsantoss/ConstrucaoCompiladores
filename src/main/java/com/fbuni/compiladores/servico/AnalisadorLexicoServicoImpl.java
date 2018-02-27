package com.fbuni.compiladores.servico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fbuni.compiladores.enumeration.ExpressoesRegulares;
import com.fbuni.compiladores.enumeration.Linguagem;
import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.LinguagemAlvo;
import com.fbuni.compiladores.model.Token;

@Service
public class AnalisadorLexicoServicoImpl implements AnalisadorLexicoServico {

	private Integer CODIGO_SIMBOLO = 0;

	@Override
	public List<Classificacao> analisar(LinguagemAlvo linguagemAlvo) throws Exception {

		if (!linguagemAlvo.getNomeLinguagem().equalsIgnoreCase(Linguagem.JS.getNome()))
			throw new Exception("Analisador feito somente para javascript");

		String[] linhas = linguagemAlvo.getCodigoFonte().split("\n");

		return montaTabelaSimbolosJavaScript(linhas);
	}

	private List<Classificacao> montaTabelaSimbolosJavaScript(String[] linhas) {

		List<Classificacao> tabela = new ArrayList<Classificacao>();

		Integer numerolinha = 1;

		for (String linha : linhas) {

			List<String> palavrasDaLinha = obterPalavrasDaLinha(linha);

			for (String palavraCorrente : palavrasDaLinha) {
				if (!palavraCorrente.matches(ExpressoesRegulares.CARACETERES_A_DESCARTAR.getExpressao())) {
					classificarPalavra(palavraCorrente, tabela, numerolinha);
				}
			}
		}

		return tabela;
	}

	@SuppressWarnings("unlikely-arg-type")
	private void classificarPalavra(String palavraCorrente, List<Classificacao> classificacoes, Integer linha) {

		Classificacao classificar = new Classificacao();

		Boolean ehPalavraValida = false;

		for (ExpressoesRegulares expressao : ExpressoesRegulares.values()) {

			if (palavraCorrente.matches(expressao.getExpressao())) {

				ehPalavraValida = true;

				classificar.setLexema(palavraCorrente);
				classificar.setSignificado(expressao.toString());

				if (expressao.toString().equals(ExpressoesRegulares.NUMERICO.toString())) {
					if (palavraCorrente.contains(".")) {
						classificar.setSimbolo("PONTO_FLUTUANTE");
					} else {
						classificar.setSimbolo("INTEIRO");
					}

					if (!verificaJaExisteIdentificador(classificacoes, classificar.getLexema())) {
						classificar.setCodigoSimbolo(CODIGO_SIMBOLO++);
					}
				}

				else if (expressao.toString().equals(ExpressoesRegulares.IDENTIFICADOR.toString())) {

					if (!verificaJaExisteIdentificador(classificacoes, classificar.getLexema())) {
						classificar.setCodigoSimbolo(CODIGO_SIMBOLO++);
					}
				}

				classificar.setToken(new Token(expressao.toString()));
				classificacoes.add(classificar);
				break;
			}
		}

		if (ehPalavraValida) {
			classificar.setLinha(linha);
		} else {
			classificar.setLexema(palavraCorrente);
			classificar.setSignificado("SEM PADRÕES - ERRO");
		}

	}

	private Boolean EhFuncao(String palavra) {

		Boolean contemChavesCorretas = palavra.contains("(") && palavra.contains(")");
		Boolean contemApenasChaveAbertura = palavra.contains("(");

		if (contemChavesCorretas) {
			return true;
		} else if (contemApenasChaveAbertura) {
			return true;
		}

		return false;
	}

	private void classificarChamadaDeFuncao(String[] chamadas, List<Classificacao> classificacoes, Integer linha) {

		for (String chamada : chamadas) {
			Classificacao classificacao = new Classificacao();

			classificacao.setLexema(chamada);
			classificacao.setSignificado("IDENTIFICADOR");

			Token token = new Token("IDENTIFICADOR");

			if (!verificaJaExisteIdentificador(classificacoes, classificacao.getLexema())) {
				classificacao.setCodigoSimbolo(CODIGO_SIMBOLO++);
				token.setValorAtribuido(CODIGO_SIMBOLO++);
			}

			classificacao.setToken(new Token("IDENTIFICADOR"));
			classificacoes.add(classificacao);

		}

	}

	private boolean verificaJaExisteIdentificador(List<Classificacao> classificacoes, String lexema) {

		for (Classificacao classificacao : classificacoes) {
			if (classificacao.getLexema().equals(lexema)) {
				return true;
			}
		}

		return false;
	}

	// desconsidera espaço de strings

	public List<String> obterPalavrasDaLinha(String linha) {

		List<String> palavras = new ArrayList<>();

		String palavraCorrente = "";

		for (Character caractere : linha.toCharArray()) {

			if (caractere.toString().matches(ExpressoesRegulares.SEPARADORES_CARACTERES.getExpressao())) {
				if (!palavraCorrente.isEmpty()) {
					palavras.add(palavraCorrente.trim());
				}

				palavras.add(caractere.toString());

				palavraCorrente = "";

				continue;
			}

			if (!palavraCorrente.contains("\"")) {

				if (caractere == ' ') {
					if (!palavraCorrente.isEmpty()) {
						palavras.add(palavraCorrente.trim());
					}

					palavraCorrente = "";
				}
			}

			palavraCorrente += caractere.toString();
		}

		return palavras;
	}
}
