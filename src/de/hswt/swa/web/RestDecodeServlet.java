package de.hswt.swa.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Scanner;

public class RestDecodeServlet extends HttpServlet {
	

	
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		   try {
			  //read input from the Client
		      Scanner scanner = new Scanner(request.getInputStream());
		      String json = scanner.nextLine();
		      Gson gson = new Gson();
		      JsonObject obj = gson.fromJson(json, com.google.gson.JsonObject.class); 
		      String inhalt = obj.get("content").getAsString();
		      List<String> liste = gson.fromJson(inhalt, java.util.ArrayList.class);
		      
		      //last element = password is saved and removed afterwards
		      String password = (String) liste.get(liste.size()-1);
		      liste.remove(liste.size()-1);
		      
		      //create decoded text
		      List<String> decodedText = CryptoUtil.create_decoded(password, liste);
		      
		      //send back result
		      JsonObject jsonResult = new JsonObject();
		      jsonResult.addProperty("decryption date", LocalDateTime.now().toString());
		      String outputJson = gson.toJson(decodedText);  
		      jsonResult.addProperty("result", outputJson);
		      String mesg = gson.toJson(jsonResult);
		      PrintWriter writer = response.getWriter(); 
		      writer.println(mesg); writer.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	} 
} 