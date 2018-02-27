
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
				$("#tabelaToken").empty();
				
				$("#tabelaToken tbody").append(
						$("<tr/>").append(
								$("<td/>").text(classificacao.lexema)).append(
								$("<td/>").text(classificacao.simbolo)).append(
								$("<td/>").text(classificacao.significado)).append(
								$("<td/>").text(classificacao.codigoSimbolo)));
			});
			
			$("#divTabela").show();
		},
		error: function(error) {
			alert('Ocorreu um erro');
		}
  });
});

});