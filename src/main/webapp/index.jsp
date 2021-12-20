<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<meta charset="ISO-8859-1">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Curso JSP Tadeu - Login</title>
		<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
		
		<style>
			form {
				position: absolute;
				top: 40%;
				left: 35%;
				right: 35%;
			}
			h5 {
				position: absolute;
				top: 30%;
				left: 35%
			}
			.msg {
				position: absolute;
				top: 10%;
				left: 35%;
				font-size: 15px;
				color: #664d03;
				background-color: #ffff3cd; 
				border-color: #ffecb5
			}
		</style>
	</head>
	<body>
		<h5>Bem-vindo ao curso de JSP</h5>
		<% out.print(""); %>
		
		<!-- <h3>Form login</h3> -->
		<form action="<%= request.getContextPath() %>/ServletLogin" method="post" class="row g-3 needs-validation" novalidate>		
			<input type="hidden" value="<%= request.getParameter("url") %>" name="url"/>
			
			<div class="mb-3">
				<label class="form-label" for="login">Login</label>			
				<input class="form-control" id="login" name="login" type="text" required />
				<div class="invalid-feedback">
					Informe o login
				</div>
				<div class="valid-feedback">
					ok
				</div>
			</div>
			
			<div class="mb-3">
				<label class="form-label" for="senha">Senha</label>			
				<input class="form-control" id="senha" name="senha" type="password" required />
				<div class="invalid-feedback">
					Informe a senha
				</div>
				<div class="valid-feedback">
					ok
				</div>
			</div>
			
			<div class="mb-3">
				<input type="submit" class="btn btn-primary" value="Acessar" />
			</div>
		</form>
		
		<h5 class="msg">${msg}</h5>
		
		<!-- <hr />
		
		<h3>Form receber nome</h3>
		<form action="receber-nome.jsp">
		
			<input name="nome" />
			<input name="idade" />
			
			<input type="submit" value="Enviar" />
		</form> -->
		
		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
		
		<script type="text/javascript">
			// Example starter JavaScript for disabling form submissions if there are invalid fields
			(function () {
			  'use strict'
	
			  // Fetch all the forms we want to apply custom Bootstrap validation styles to
			  var forms = document.querySelectorAll('.needs-validation')
	
			  // Loop over them and prevent submission
			  Array.prototype.slice.call(forms)
			    .forEach(function (form) {
			      form.addEventListener('submit', function (event) {
			        if (!form.checkValidity()) {
			          event.preventDefault()
			          event.stopPropagation()
			        }
	
			        form.classList.add('was-validated')
			      }, false)
			    })
			})()
		</script>
	</body>
</html>