$(document).ready(function(){
	var code = $("#codigoFonte")[0];
	
	var editor = CodeMirror.fromTextArea(code, {
		lineNumbers : true
	});
});