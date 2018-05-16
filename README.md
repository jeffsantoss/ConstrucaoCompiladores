A aplicação foi desenvolvida em Spring Boot e comporta um conjunto de serviços web para analisar o código fonte de uma determinada linguagem.

No momento, as implementações das interfaces abragem apenas a linguagem JavaScript.

Segue as URLs:
<br>
(POST)
/fbuni/interpretador/lexico
<br>
/fbuni/interpretador/sintatico
<br>
Exemplo de payload em JSON de ambos paths:

<pre>
{
  "nomeLinguagem" : "javascript",
  "codigoFonte": "var a = 1; \n var b = ((6 / 3 ) * 5))) - 6;"
}
</pre>
