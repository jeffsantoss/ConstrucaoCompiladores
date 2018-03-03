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

		Lexema comentario = verificaComentarios(linguagemAlvo);

		Classificacao classificacaoComentario = null;

		if (!comentario.getPalavra().isEmpty()) {
			classificacaoComentario = new Classificacao();
			classificacaoComentario.setLexema(comentario);
			Token token = new Token();
			token.setCodToken(CODIGO_SIMBOLO++);
			token.setNomeToken("COMENTÁRIO");
			classificacaoComentario.setToken(token);
		}

		List<Classificacao> tabela = montaTabelaSimbolosJavaScript(linguagemAlvo.getCodigoFonte().split("\n"));

		if (classificacaoComentario != null) {
			tabela.add(classificacaoComentario);
		}

		return tabela;
	}

	private List<Classificacao> montaTabelaSimbolosJavaScript(String[] linhas) throws Exception {

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

	private Lexema verificaComentarios(LinguagemAlvo linguagem) throws Exception {

		String comentarioAbertura = "/*";
		String comentarioFechamento = "*/";
		Integer linha = 1;
		Lexema lexema = new Lexema("");
		String comentario = "";
		String codigoFonte = linguagem.getCodigoFonte();

		if (codigoFonte.contains(comentarioAbertura) && !codigoFonte.contains(comentarioFechamento)) {
			throw new Exception("Comentário aberto não foi fechado");
		} else if (codigoFonte.contains(comentarioFechamento) && !codigoFonte.contains(comentarioAbertura)) {
			throw new Exception("Comentário não foi aberto");
		}

		if (codigoFonte.contains(comentarioAbertura) && codigoFonte.contains(comentarioFechamento)) {

			if (codigoFonte.indexOf(comentarioAbertura) > codigoFonte.indexOf(comentarioFechamento)) {
				throw new Exception("Comentário de fechamento antes do de abertura");
			}

			for (int i = codigoFonte.indexOf(comentarioAbertura); i <= codigoFonte.indexOf(comentarioFechamento)
					+ 1; i++) {

				if (codigoFonte.charAt(i) != '\n') {
					comentario += codigoFonte.charAt(i);
					linguagem.setCodigoFonte(this.replace(linguagem.getCodigoFonte(), i, Character.MIN_VALUE));
				} else {
					linha++;
				}

			}

		}
		if (!comentario.isEmpty()) {
			lexema.setPalavra(comentario);
			Padrao padrao = new Padrao();
			padrao.setDescricao("Comentário");
			lexema.setPadrao(padrao);
			lexema.setLinha(linha);
		}

		return lexema;
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

				if (!verificaJaExisteIdentificador(classificacoes, classificacao)) {
					token.setCodToken(CODIGO_SIMBOLO++);
				}

				token.setNomeToken(expressao.toString());
				classificacao.setToken(token);

				classificacoes.add(classificacao);
				break;
			}
		}

		if (lexema.getPadrao() == null) {
			token.setNomeToken("ERRO - PADRÃO NÃO DEFINIDO");
			classificacao.setToken(token);
			classificacoes.add(classificacao);
		}
	}

	private boolean verificaJaExisteIdentificador(List<Classificacao> classificacoes, Classificacao classificacao) {

		for (Classificacao classific : classificacoes) {
			if (classific.equals(classificacao)) {
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
							coluna - 1));
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

	private String replace(String str, int index, char replace) {
		if (str == null) {
			return str;
		} else if (index < 0 || index >= str.length()) {
			return str;
		}
		char[] chars = str.toCharArray();
		chars[index] = replace;
		return String.valueOf(chars);
	}

}
