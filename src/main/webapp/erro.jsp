<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<meta charset="ISO-8859-1">
		<title>Erros</title>
	</head>
	<body>
		<h1>Mensagem de Erro! Entre em contato com a equipe de suporte do sistema.</h1>
		<% out.print(request.getAttribute("msg")); %>
	</body>
</html>