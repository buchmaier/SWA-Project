
<!DOCTYPE html>
<html><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Crypto service</title>

</head>
<body>
<center><h3>Crypto Service</h3></center>



	<%@ page import="java.util.List" %>
	<form action="crypto" method="post">
	<br>
	<label>Plain Text</label>
	<br>
	<textarea name="plainText" rows="15" cols="80"><%
			List<String> plain =  (List<String>) request.getAttribute("plain");
			if (plain != null) {
				for (String resultLine : plain){
					out.println(resultLine);
					
				}

			}else{
				out.println("error");
	}%></textarea>
	
	<br>
	<br>
	<label>Encoded Text</label>	
	<br>
	<textarea name="encodedText" rows="15" cols="80"><%
			
			List<String> encoded = (List<String>) request.getAttribute("encoded");
			if (encoded != null) {
				for (String resultLine : encoded){
					out.println(resultLine);
				}

			} else {
				out.println("error");
	}%></textarea>
		
	<br>

		
	password: <input type="password" id="pw" name="password" placeholder="Enter your Password">
	decode: <input type="radio" id="decode" name="mode" value="DECODE" checked>
	encode: <input type="radio" id="decode" name="mode" value="ENCODE">
		
	<br>
	
	<input type="submit" value=" execute ">
	</form>
	
	<br>
	
	
	

</body></html>