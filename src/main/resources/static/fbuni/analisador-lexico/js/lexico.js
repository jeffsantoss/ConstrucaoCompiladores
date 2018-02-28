
$(function(){

$("#btAnalisar").click(function(){
	
	var dados = {
		codigoFonte: $('#codigoFonte').val(),
		nomeLinguagem : 'javascript'
	};
	
	console.log(dados);
	
	$.ajax({
		type: "POST",
		url: 'analisar',
		dataType: "json",
		contentType:'application/json',
		data: JSON.stringify(dados),	 
		success: function(retorno) {
			$(retorno.tabela).each(function(i, classificacao){
				//$("#tabelaToken").empty();
				//console.log(classificacao.lexema)
				var coluna = classificacao.lexema.colunaFinal ? "(" + classificacao.lexema.colunaInicial + "-" + classificacao.lexema.colunaFinal + ")" : classificacao.lexema.colunaInicial;
				
				$("#tabelaToken tbody").append(
						$("<tr/>").append(
								$("<td/>").text(classificacao.lexema.palavra)).append(
								$("<td/>").text(classificacao.lexema.padrao.descricao)).append(
								$("<td/>").text(classificacao.lexema.linha)).append(								
								$("<td/>").text(coluna)).append(
								$("<td/>").text(classificacao.token.tokenFormatado)));
			});
			
			$("#divTabela").show();
		},
		error: function(error) {
			alert('Ocorreu um erro');
		}
  });
});

});