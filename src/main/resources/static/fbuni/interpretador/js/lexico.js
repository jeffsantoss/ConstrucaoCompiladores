var metodosDeclarados;

$(function(){

$("#btSintatico").click(function(){
	
	var dados = {
		codigoFonte: $('#codigoFonte').val(),
		nomeLinguagem : 'javascript'
	};
	
	$.ajax({
		
		url: 'sintatico',
		type: "POST",
		dataType: "json",
		contentType:'application/json',
		data: JSON.stringify(dados),	 
		success: function(retorno) {
			
		    montarTabela(retorno.tabelaSimbolos);
		    
		    metodosDeclarados = retorno.metodosDeclarados;
			
			toastr.success(retorno.mensagem);
			
			$('#btVerMetodos').show();			
		},
		error: function(xhr, message) {			
			toastr.error(xhr.responseJSON.message);			
		}
  });
});


$("#btVerMetodos").click(function(){
	
	$("#divTabelaMetodos").show();
	$("#divTabela").hide();
	
	$("#qtdMetodosDeclarados").text(metodosDeclarados.length);
	
	metodosDeclarados.forEach(function(metodo,indice) {
		
		$("#metodos").append("<h1 ALIGN='center'> Método:" + metodo.nome +"</h1>")
		
//		$("#metodos").append("<h1 ALIGN='center'>Parâmetros:</h1>")
		
//		for (let parametro of metodo.parametros) {			
//			$("#metodos").append("<p ALIGN='center'> <b>" + parametro.nome +"</b></p>")			
//		}
				
		for (let classificacao of metodo.escopo) {
			$("#metodos").append("<p ALIGN='center'> <b>" + classificacao.lexema.palavra +"</b> &nbsp "+ classificacao.token.tokenFormatado + "</p>")
			$("#metodos").append("<br>")
		}
				
	});

	
	$('#btVerMetodos').hide();
});

$("#btLexico").click(function(){
	
	var dados = {
		codigoFonte: $('#codigoFonte').val(),
		nomeLinguagem : 'javascript'
	};
	
	$.ajax({
		
		type: "POST",
		url: 'lexico',
		dataType: "json",
		contentType:'application/json',
		data: JSON.stringify(dados),	 
		success: function(retorno) {			
		    montarTabela(retorno.tabelaSimbolos);			
		},
		error: function(xhr, message) {			
			toastr.error(xhr.responseJSON.message);			
		}
  });
});

function montarTabela(tabelaSimbolos) {
	
	$("#tabelaToken tbody").empty();
	
	$(tabelaSimbolos).each(function(i, classificacao){
		
		var coluna = classificacao.lexema.colunaFinal ? "(" + classificacao.lexema.colunaInicial + "," + classificacao.lexema.colunaFinal + ")" : classificacao.lexema.colunaInicial;
		
		$("#tabelaToken tbody").append(
				$("<tr/>").append(
						$("<td/>").text(classificacao.lexema.palavra)).append(
						$("<td/>").text(classificacao.lexema.padrao.descricao)).append(
						$("<td/>").text(classificacao.lexema.linha)).append(								
						$("<td/>").text(coluna)).append(
						$("<td/>").text(classificacao.token.tokenFormatado)));
	});
	
	$("#divTabela").show();

}

});