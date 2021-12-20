<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<meta charset="ISO-8859-1">
		<title>Receber Nome</title>
	</head>
	<body>
		<% 	
			// http://localhost:8080/curso-jsp-tadeu/receber-nome.jsp?nome=Tadeu
			String nome = request.getParameter("nome");
			out.println("Nome: " + nome);		
			
			// http://localhost:8080/curso-jsp-tadeu/receber-nome.jsp?nome=Tadeu&idade=35
			String idade = request.getParameter("idade");
			out.println("Idade: " + idade);
		%>
	</body>
</html>