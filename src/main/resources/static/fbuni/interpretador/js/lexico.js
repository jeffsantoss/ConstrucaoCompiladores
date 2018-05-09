
$(function(){

$("#btSintatico").click(function(){
	
	var dados = {
		codigoFonte: $('#codigoFonte').val(),
		nomeLinguagem : 'javascript'
	};
	
	$.ajax({
		
		type: "POST",
		url: 'sintatico',
		dataType: "json",
		contentType:'application/json',
		data: JSON.stringify(dados),	 
		success: function(retorno) {
			
		    montarTabela(retorno.tabelaSimbolos);
			
			toastr.success(retorno.mensagem);
		},
		error: function(xhr, message) {			
			toastr.error(xhr.responseJSON.message);			
		}
  });
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