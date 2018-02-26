
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
		success: function(data) {
			console.log(data);
		},
		error: function(error) {
			alert('Ocorreu um erro');
		}
  });
});

});