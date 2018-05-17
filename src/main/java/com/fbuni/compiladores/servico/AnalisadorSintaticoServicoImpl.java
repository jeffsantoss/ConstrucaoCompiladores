package com.fbuni.compiladores.servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fbuni.compiladores.model.Classificacao;
import com.fbuni.compiladores.model.Expressao;
import com.fbuni.compiladores.model.Metodo;
import com.fbuni.compiladores.model.MetodosSingleton;
import com.fbuni.compiladores.model.Parametro;
import com.fbuni.compiladores.model.Token;
import com.fbuni.compiladores.util.CompiladoresUtil;

@Service
public class AnalisadorSintaticoServicoImpl implements AnalisadorSintaticoServico {

    @Autowired
    private MetodosSingleton metodosSingleton;

    @Override
    public void analisar(List<Classificacao> tabelaSimbolos) throws Exception {

	Classificacao classificacaoComErro = CompiladoresUtil.obterClassificacaoPorToken(tabelaSimbolos, "ERROR");

	if (classificacaoComErro != null) {
	    throw CompiladoresUtil.estourarExcessao(classificacaoComErro.getLexema().getLinha(),
		    "Existe erro na tabela de símbolos. Favor Corrigir.");
	}

	// seta no singleton armazenando em memória os métodos declarados
	capturarMetodosDeclarados(tabelaSimbolos);

	// valida todas as assinaturas
	validaAssinaturaMetodos(tabelaSimbolos);

	// analisa código que não tem escopo
	analisarEscopo(obterEscopoPrincipal(tabelaSimbolos), null, tabelaSimbolos);

	// analisa escopo por escopo de todos métodos armazenados em memória
	for (Metodo metodo : metodosSingleton.getInstancia()) {
	    analisarEscopo(metodo.getEscopo(), metodo.getParametros(), tabelaSimbolos);
	}

	// inclui na tabela de símbolos o escopo dos tokens
	for (Metodo metodo : metodosSingleton.getInstancia()) {
	    adicionarEscopoTabelaSimbolos(metodo, tabelaSimbolos);
	}
    }

    private void validaAssinaturaMetodos(List<Classificacao> tabelaSimbolos) {

	List<Classificacao> todosMetodos = tabelaSimbolos.stream()
		.filter(c -> c.getLexema().getPalavra().equals("function")).collect(Collectors.toList());

	for (Classificacao classificacao : todosMetodos) {

	    Integer indice = CompiladoresUtil.indiceClassificao(tabelaSimbolos, classificacao.getLexema().getPalavra(),
		    classificacao.getLexema().getLinha());

	    Integer aberturaEscopo = CompiladoresUtil.indiceClassificao(tabelaSimbolos, indice, "{");

	    List<Classificacao> assinatura = tabelaSimbolos.subList(indice, aberturaEscopo);

	    if (assinatura.stream().filter(a -> a.getLexema().getPalavra().equals("(")).count() != 1
		    || assinatura.stream().filter(a -> a.getLexema().getPalavra().equals(")")).count() != 1
		    || assinatura.stream().filter(a -> a.getToken().getNomeToken().equals("OPERADOR")).count() > 0
		    || assinatura.stream().filter(a -> a.getToken().getNomeToken().equals("ATRIBUICAO")).count() > 0
		    || assinatura.stream().filter(a -> a.getToken().getNomeToken().equals("PALAVRA_RESERVADA"))
			    .count() > 1) {

		StringBuilder assinaturaString = new StringBuilder();

		for (Classificacao lexema : assinatura) {
		    assinaturaString.append(lexema.getLexema().getPalavra() + " ");
		}

		throw CompiladoresUtil.estourarExcessao(assinatura.get(0).getLexema().getLinha(),
			"Assinatura " + assinaturaString.toString() + " inválida.");
	    }

	}

    }

    private List<Classificacao> obterEscopoPrincipal(List<Classificacao> tabelaSimbolos) {

	Integer indiceFechamentoUltimoMetodo = 0;

	for (int i = tabelaSimbolos.size() - 1; i >= 0; i--) {
	    if (tabelaSimbolos.get(i).getLexema().getPalavra().equals("}")) {
		indiceFechamentoUltimoMetodo = i;
		break;
	    }
	}

	return tabelaSimbolos.subList(indiceFechamentoUltimoMetodo + 1, tabelaSimbolos.size());
    }

    @Override
    public List<Metodo> obterMetodosDeclarados() throws Exception {
	return metodosSingleton.getInstancia();
    }

    private void capturarMetodosDeclarados(List<Classificacao> tabelaSimbolos) {

	metodosSingleton.setInstancia(new ArrayList<Metodo>());

	List<Classificacao> metodos = tabelaSimbolos.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ID_DECLARAÇÃO_FUNCAO")).collect(Collectors.toList());

	for (Classificacao classificacao : metodos) {

	    Integer indiceFuncao = CompiladoresUtil.indiceClassificao(tabelaSimbolos,
		    classificacao.getLexema().getPalavra(), "ID_DECLARAÇÃO_FUNCAO");

	    Integer fechamentoEscopo = CompiladoresUtil.indiceClassificao(tabelaSimbolos, indiceFuncao, "}");
	    Integer aberturaEscopo = CompiladoresUtil.indiceClassificao(tabelaSimbolos, indiceFuncao, "{");
	    Integer aberturaParantese = CompiladoresUtil.indiceClassificao(tabelaSimbolos, indiceFuncao, "(");
	    Integer fechamentoParentese = CompiladoresUtil.indiceClassificao(tabelaSimbolos, indiceFuncao, ")");

	    if (fechamentoEscopo == -1 || fechamentoParentese == -1) {
		throw CompiladoresUtil.estourarExcessao(classificacao.getLexema().getLinha(),
			"O escopo da função " + classificacao.getLexema().getPalavra() + " não foi fechado.");
	    } else if (aberturaEscopo == -1 || aberturaParantese == -1) {
		throw CompiladoresUtil.estourarExcessao(classificacao.getLexema().getLinha(),
			"O escopo da função " + classificacao.getLexema().getPalavra() + " não foi aberto.");
	    }

	    String nomeMetodo = classificacao.getLexema().getPalavra();

	    Metodo metodo = new Metodo();
	    metodo.setNome(nomeMetodo);
	    metodo.setParametros(obterParametros(tabelaSimbolos, indiceFuncao, false));
	    metodo.setEscopo(
		    tabelaSimbolos.subList(CompiladoresUtil.indiceClassificao(tabelaSimbolos, indiceFuncao, nomeMetodo),
			    CompiladoresUtil.indiceClassificao(tabelaSimbolos, indiceFuncao, "}") + 1));

	    if (CompiladoresUtil.indiceClassificao(metodo.getEscopo(), "function", "PALAVRA_RESERVADA") != -1) {
		throw CompiladoresUtil.estourarExcessao(classificacao.getLexema().getLinha(),
			"Não é permitido declarar função dentro de um escopo");
	    }

	    metodosSingleton.getInstancia().add(metodo);
	}
    }

    private void adicionarEscopoTabelaSimbolos(Metodo metodo, List<Classificacao> tabelaSimbolos) {

	for (Classificacao classificacaoDoEscopo : metodo.getEscopo()) {

	    Classificacao classificacao = tabelaSimbolos.stream()
		    .filter(c -> c.getToken().getCodToken().equals(classificacaoDoEscopo.getToken().getCodToken()))
		    .findFirst().get();

	    classificacao.getLexema().getPadrao()
		    .setDescricao("Indica um token dentro do escopo do método: " + metodo.getNome());

	    classificacao.getToken().setNomeToken(
		    classificacao.getToken().getNomeToken() + "_ESCOPO_" + metodo.getNome().toUpperCase());

	}

    }

    private void analisarEscopo(List<Classificacao> escopo, List<Parametro> parametros,
	    List<Classificacao> tabelaSimbolos) throws IllegalArgumentException {

	if (escopo == null || escopo.isEmpty())
	    return;

	List<Classificacao> variaveis = escopo.stream().filter(
		c -> c.getToken().getNomeToken().equals("ID") || c.getToken().getNomeToken().equals("ID_VAR_LOCAL"))
		.distinct().collect(Collectors.toList());

	// valida se as váriaveis foram declarado ou não
	validaDeclaracaoDeVariavel(variaveis, tabelaSimbolos, parametros);

	// valida se mesma varíavel foi declarada mais de 1x
	validaDeclaracaoVariaveisRepetidas(variaveis, tabelaSimbolos);

	validaChamadaDeFuncoes(escopo);

	for (int linha = escopo.get(0).getLexema().getLinha(); linha < escopo.get(escopo.size() - 1).getLexema()
		.getLinha(); linha++) {

	    final int linhaCorrente = linha;

	    List<Classificacao> classificacoesDaLinhaSemDuplicacao = escopo.stream()
		    .filter(x -> x.getLexema().getLinha().equals(linhaCorrente)).distinct()
		    .collect(Collectors.toList());

	    if (CompiladoresUtil.contemPalavra(classificacoesDaLinhaSemDuplicacao, "{")
		    || CompiladoresUtil.contemPalavra(classificacoesDaLinhaSemDuplicacao, "}")) {
		continue;
	    }

	    validaPontoVirgula(classificacoesDaLinhaSemDuplicacao, linhaCorrente);

	    // pega o índice pelo nome do token e a linha;
	    Integer indicePontoVirgulaLinhaAnterior = CompiladoresUtil.indiceClassificao(escopo, ";",
		    escopo.get(linhaCorrente - 1).getLexema().getLinha());

	    Integer indicePontoVirgulaLinhaPosterior = CompiladoresUtil.indiceClassificao(escopo, ";", linhaCorrente);

	    List<Classificacao> classificacaoDaLinha = new ArrayList<Classificacao>();

	    if (indicePontoVirgulaLinhaAnterior != -1) {
		for (int i = indicePontoVirgulaLinhaAnterior + 1; i <= indicePontoVirgulaLinhaPosterior; i++) {
		    classificacaoDaLinha.add(escopo.get(i));
		}
	    } else {

		Integer indiceAnterior = 0;

		if (CompiladoresUtil.indiceClassificao(escopo, "{", linhaCorrente - 1) != -1) {

		    indiceAnterior = CompiladoresUtil.indiceClassificao(escopo, "{", linhaCorrente - 1);

		} else if (CompiladoresUtil.indiceClassificao(escopo, "}", linhaCorrente - 1) != -1) {
		    indiceAnterior = CompiladoresUtil.indiceClassificao(escopo, "}", linhaCorrente - 1);
		}

		for (int i = indiceAnterior + 1; i <= indicePontoVirgulaLinhaPosterior; i++) {
		    classificacaoDaLinha.add(escopo.get(i));
		}
	    }

	    // expressões.
	    if (CompiladoresUtil.contemToken(classificacaoDaLinha, "ATRIBUIÇÃO")) {

		// pega depois da atribuição

		Integer indiceAposAtribuicao = CompiladoresUtil.indiceClassificao(classificacaoDaLinha, "=",
			"ATRIBUIÇÃO") + 1;
		Integer indiceFimDaLinha = CompiladoresUtil.indiceClassificao(classificacaoDaLinha, ";",
			"FIM_DE_LINHA");

		List<Classificacao> classificaoDeExpressoes = classificacaoDaLinha.subList(indiceAposAtribuicao,
			indiceFimDaLinha);

		validaExpressoes(classificaoDeExpressoes);
	    }

	}

    }

    private void validaChamadaDeFuncoes(List<Classificacao> escopo) {

	List<Classificacao> chamadaDeFuncoes = escopo.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ID_CHAMADA_FUNCAO")).distinct()
		.collect(Collectors.toList());

	for (Classificacao classificacao : chamadaDeFuncoes) {

	    Integer indiceFuncao = CompiladoresUtil.indiceClassificao(escopo, classificacao.getLexema().getPalavra(),
		    "ID_CHAMADA_FUNCAO");

	    Integer qtd = obterParametros(escopo, indiceFuncao, true).size();

	    String nomeMetodo = classificacao.getLexema().getPalavra();

	    Optional<Metodo> metodoExistente = metodosSingleton.getInstancia().stream()
		    .filter(m -> m.getNome().equals(nomeMetodo)).findAny();

	    if (!metodoExistente.isPresent()) {
		throw CompiladoresUtil.estourarExcessao(classificacao.getLexema().getLinha(),
			"Método: " + nomeMetodo + " não foi declarado");
	    } else if (metodoExistente.get().getParametros().size() != qtd) {
		throw CompiladoresUtil.estourarExcessao(classificacao.getLexema().getLinha(),
			"Chamada ao método: " + nomeMetodo + " não contém o número de parâmetros esperado");
	    }

	}
    }

    private List<Parametro> obterParametros(List<Classificacao> classificacoes, Integer indiceStart,
	    Boolean ehChamada) {

	List<Parametro> parametros = new ArrayList<Parametro>();

	Integer indiceUltimoParentese = 0;

	if (ehChamada) {
	    for (int i = indiceStart; i < classificacoes.size(); i++) {
		if (classificacoes.get(i).getLexema().getPalavra().equals(";")) {
		    indiceUltimoParentese = i - 1;
		    break;
		}
	    }
	} else {
	    indiceUltimoParentese = CompiladoresUtil.indiceClassificao(classificacoes, indiceStart, ")") + 1;
	}

	List<Classificacao> parametrosExtraidos = classificacoes
		.subList(CompiladoresUtil.indiceClassificao(classificacoes, indiceStart, "("), indiceUltimoParentese);

	for (Classificacao classificacao : parametrosExtraidos) {

	    if (classificacao.getLexema().getPalavra().equals(",") || classificacao.getLexema().getPalavra().equals(")")
		    || classificacao.getLexema().getPalavra().equals("("))
		continue;

	    Parametro parametro = new Parametro();
	    parametro.setNome(classificacao.getLexema().getPalavra());
	    parametros.add(parametro);
	}

	if (parametros.isEmpty()) {
	    return parametros;
	}

	List<Classificacao> chamadasFuncao = parametrosExtraidos.stream()
		.filter(p -> p.getToken().getNomeToken().equals("ID_CHAMADA_FUNCAO")).collect(Collectors.toList());

	if (!chamadasFuncao.isEmpty()) {

	    for (Classificacao chamadaFuncao : chamadasFuncao) {

		Integer indiceChamadaFuncao = parametrosExtraidos.indexOf(chamadaFuncao);

		List<Parametro> parametrosDesconsiderados = obterParametros(parametrosExtraidos, indiceChamadaFuncao,
			false);

		for (Parametro parametro : parametrosDesconsiderados) {
		    parametros.remove(parametro);
		}
	    }
	}

	return parametros;
    }

    private void validaDeclaracaoDeVariavel(List<Classificacao> variaveis, List<Classificacao> tabelaSimbolos,
	    List<Parametro> parametros) {

	for (Classificacao variavel : variaveis) {

	    Integer codToken = variavel.getToken().getCodToken();

	    Classificacao classificacaoAnterior = obterClassificacaoPorCodigo(tabelaSimbolos, codToken - 1);

	    // parâmetro de função
	    if (parametros != null && parametros.stream()
		    .filter(p -> p.getNome().equals(variavel.getLexema().getPalavra())).findAny().isPresent()) {
		continue;
	    }

	    if (classificacaoAnterior != null && classificacaoAnterior.getToken().getNomeToken().equals("SEPARADOR")) {
		continue;
	    }

	    if (classificacaoAnterior == null
		    || classificacaoAnterior.getToken().getNomeToken() != "PALAVRA_RESERVADA") {
		throw CompiladoresUtil.estourarExcessao(variavel.getLexema().getLinha(),
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

		    if (tabelaSimbolos.get(indiceClassificacaoIgual - 1).getLexema().getPalavra().equals("var")) {
			throw CompiladoresUtil.estourarExcessao(identificadoresVariavel.get(j).getLexema().getLinha(),
				"a variável >" + identificadoresVariavel.get(i).getLexema().getPalavra()
					+ "< já foi declarada");
		    }
		}
	    }
	}

    }

    public Classificacao obterClassificacaoPorCodigo(List<Classificacao> classificacaoes, Integer codToken) {

	for (Classificacao classificacao : classificacaoes) {
	    if (classificacao.getToken().getCodToken().equals(codToken)) {
		return classificacao;
	    }
	}

	return null;
    }

    private void validaExpressoes(List<Classificacao> classificacoesDaLinha) throws IllegalArgumentException {

	Integer numlinha = classificacoesDaLinha.get(0).getLexema().getLinha();

	// parênteses
	Long qtdAbertura = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ABERTURA_FUNCAO_ESCOPO_INDEXACAO")).count();

	Long qtdFechamento = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO")).count();

	if (qtdAbertura > qtdFechamento) {
	    throw CompiladoresUtil.estourarExcessao(numlinha, "expressão inválida, falta parênteses a fechar");
	} else if (qtdFechamento > qtdAbertura) {
	    throw CompiladoresUtil.estourarExcessao(numlinha, "expressão inválida, falta parênteses a abrir");
	}

	List<Classificacao> parenteseFechando = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO"))
		.collect(Collectors.toList());

	List<Classificacao> parenteseAbrindo = classificacoesDaLinha.stream()
		.filter(c -> c.getToken().getNomeToken().equals("ABERTURA_FUNCAO_ESCOPO_INDEXACAO"))
		.collect(Collectors.toList());

	for (int pf = 0, pa = parenteseAbrindo.size() - 1; pf < parenteseFechando.size(); pf++, pa--) {

	    Integer indiceFechando = CompiladoresUtil.indiceClassificao(classificacoesDaLinha,
		    parenteseFechando.get(pf).getToken().getCodToken());

	    Integer indiceAbrindo = CompiladoresUtil.indiceClassificao(classificacoesDaLinha,
		    parenteseAbrindo.get(pa).getToken().getCodToken());

	    Integer qtdOperadores = 0;
	    Integer qtdNumeros = 0;

	    for (int j = indiceAbrindo; j < indiceFechando; j++) {

		if (classificacoesDaLinha.get(j).getToken().getNomeToken().equals("OPERADOR")
			|| classificacoesDaLinha.get(j).getToken().getNomeToken().equals("SEPARADOR")) {
		    qtdOperadores++;
		}

		Token tokenCorrente = classificacoesDaLinha.get(j).getToken();

		if (tokenCorrente.getNomeToken().equals("PONTO_FLUTUANTE")
			|| tokenCorrente.getNomeToken().equals("INTEIRO") || tokenCorrente.getNomeToken().equals("ID")
			|| tokenCorrente.getNomeToken().equals("ID_VAR_LOCAL")) {
		    qtdNumeros++;
		}
	    }

	    Boolean expressoesCorreta = qtdOperadores == qtdNumeros - 1 ? true : false;

	    if (!expressoesCorreta) {
		throw CompiladoresUtil.estourarExcessao(numlinha,
			"não contém operador dentro da expressão entre '('' ')'' ");
	    }

	    try {
		if (classificacoesDaLinha.get(indiceFechando + 1).getToken()
			.getNomeToken() == "FECHAMENTO_FUNCAO_ESCOPO_INDEXACAO")
		    continue;

		if (classificacoesDaLinha.get(indiceFechando + 1).getToken().getNomeToken() != "OPERADOR") {
		    throw CompiladoresUtil.estourarExcessao(numlinha,
			    "Verifique se existe operadores entre as expressões");
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
	    throw CompiladoresUtil.estourarExcessao(numlinha, "Não contém expressões dentro dos parênteses");
	}

	List<Classificacao> operadores = classificacoesSemParentese.stream()
		.filter(item -> item.getToken().getNomeToken().equals("OPERADOR")).collect(Collectors.toList());

	List<Expressao> expressoes = new ArrayList<Expressao>();

	for (int i = 0; i < operadores.size(); i++) {

	    Integer indiceOperador = CompiladoresUtil.indiceClassificao(classificacoesSemParentese,
		    operadores.get(i).getToken().getCodToken());

	    try {
		expressoes.add(new Expressao(classificacoesSemParentese.get(indiceOperador - 1), operadores.get(i),
			classificacoesSemParentese.get(indiceOperador + 1)));

	    } catch (IllegalArgumentException e) {
		throw CompiladoresUtil.estourarExcessao(numlinha, e.getMessage());
	    } catch (Exception e) {
		throw CompiladoresUtil.estourarExcessao(numlinha,
			"Expressão inválida! Não foi possível montar uma expressão");
	    }

	}

    }

    private void validaPontoVirgula(List<Classificacao> classificacoesDaLinha, Integer linha) {

	if (!classificacoesDaLinha.isEmpty()) {
	    if (!classificacoesDaLinha.get(classificacoesDaLinha.size() - 1).getLexema().getPalavra().equals(";")) {
		throw CompiladoresUtil.estourarExcessao(linha, "não contém ponto e virgula no final da linha");
	    }
	}
    }

}
