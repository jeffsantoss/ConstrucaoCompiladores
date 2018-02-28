package com.fbuni.compiladores.servico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fbuni.compiladores.enumeration.ExpressaoRegular;
import com.fbuni.compiladores.enumeration.Linguagem;
import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.Lexema;
import com.fbuni.compiladores.model.LinguagemAlvo;
import com.fbuni.compiladores.model.Padrao;
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

			List<Lexema> lexemas = obterPalavrasDaLinha(linha, numerolinha++);

			for (Lexema lexema : lexemas) {
				classificarLexema(lexema, tabela, numerolinha);
			}
		}

		return tabela;
	}

	private void classificarLexema(Lexema lexema, List<Classificacao> classificacoes, Integer linha) {

		Classificacao classificacao = new Classificacao();

		Token token = new Token();

		for (ExpressaoRegular expressao : ExpressaoRegular.values()) {

			if (lexema.getPalavra().matches(expressao.getExpressao())) {

				lexema.setPadrao(new Padrao(expressao));

				classificacao.setLexema(lexema);

				if (expressao.toString().equals(ExpressaoRegular.NUMERICO.toString())) {

					if (lexema.getPalavra().contains(".")) {
						token.setNomeToken("PONTO_FLUTUANTE");
					} else {
						token.setNomeToken("INTEIRO");
					}
				}

				else if (expressao.toString().equals(ExpressaoRegular.IDENTIFICADOR.toString())) {

					// if (!verificaJaExisteIdentificador(classificacoes,
					// classificacao.getLexema())) {
					// token.setValorAtribuido(lexema.getPalavra());
					// }
				}

				token.setNomeToken(lexema.getPalavra());
				token.setCodToken(CODIGO_SIMBOLO++);
				classificacao.setToken(token);

				classificacoes.add(classificacao);
				break;
			}
		}

		if (lexema.getPadrao() == null) {
			token.setNomeToken("ERRO");
			classificacao.setToken(token);
			classificacoes.add(classificacao);
		}
	}

	private boolean verificaJaExisteIdentificador(List<Classificacao> classificacoes, Lexema lexema) {

		for (Classificacao classificacao : classificacoes) {
			if (classificacao.getLexema().equals(lexema)) {
				return true;
			}
		}

		return false;
	}

	// usa os separadores de lexemas para fazer o split.

	public List<Lexema> obterPalavrasDaLinha(String linha, Integer posicaoLinha) {

		List<Lexema> lexemas = new ArrayList<>();

		String palavraCorrente = "";
		Integer coluna = 1;

		for (Character caractere : linha.toCharArray()) {

			if (caractere.toString().matches(ExpressaoRegular.SEPARADORES_LEXEMAS.getExpressao())) {
				if (!palavraCorrente.isEmpty()) {

					lexemas.add(new Lexema(palavraCorrente.trim(), posicaoLinha, coluna - palavraCorrente.length(),
							coluna));
				}

				lexemas.add(new Lexema(caractere.toString(), posicaoLinha, coluna));

				palavraCorrente = "";

				coluna++;
				continue;
			}

			if (!palavraCorrente.contains("\"")) {

				if (caractere == ' ') {
					if (!palavraCorrente.isEmpty()) {
						lexemas.add(new Lexema(palavraCorrente.trim(), posicaoLinha, coluna - palavraCorrente.length(),
								coluna));
					}

					palavraCorrente = "";
				}
			}

			palavraCorrente += caractere.toString();
			coluna++;
		}

		return lexemas;
	}

}
