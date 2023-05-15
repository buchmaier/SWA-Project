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
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class RestEncodeServlet extends HttpServlet {
   
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		   try {
			  //scan input from main program
		      Scanner scanner = new Scanner(request.getInputStream());
		      String json = scanner.nextLine();
		      Gson gson = new Gson();
		      JsonObject obj = gson.fromJson(json, com.google.gson.JsonObject.class); 
		      String inhalt = obj.get("content").getAsString();
		      List<String> liste = gson.fromJson(inhalt, java.util.ArrayList.class);
		      
		      //last element = password, is saved and removed afterwards
		      String password = (String) liste.get(liste.size()-1);
		      liste.remove(liste.size()-1);
		      
		      //create encoded Text
		      List<String> encodedText = CryptoUtil.create_encoded(password, liste);
		      
		      //send back encoded text and date
		      JsonObject jsonResult = new JsonObject();
		      jsonResult.addProperty("encryption date", LocalDateTime.now().toString());
		      String outputJson = gson.toJson(encodedText);  
		      jsonResult.addProperty("result", outputJson);
		      String mesg = gson.toJson(jsonResult);
		      PrintWriter writer = response.getWriter(); 
		      writer.println(mesg); writer.close();
		   
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	} 
} 