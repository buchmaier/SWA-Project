package de.hswt.swa.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CryptoServlet extends HttpServlet {
	
	private List<String> encoded;
	
	public void doGet (HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
	}
	

    public void doPost (HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
    	
    	String mode = request.getParameter("mode");
    	String password = request.getParameter("password");
    	if(mode!=null) {
    		if(mode.equals("DECODE")) {
        		String encodedtextAsString = request.getParameter("encodedText");
        		String plaintextAsString = request.getParameter("plainText");
                    
        		List<String> encoded =  Arrays.asList(encodedtextAsString.split("\\\r\n"));
        		List<String> plain_changed = CryptoUtil.create_decoded(password, encoded);
        		List<String> plain_unchanged = Arrays.asList(plaintextAsString.split("\\\r\n"));
        		
        		//only update plain text if decryption was successful!
        		if(!plain_changed.isEmpty()) {
        			request.setAttribute("plain", plain_changed);
        		}else{
        			request.setAttribute("plain", plain_unchanged);
        		}
    	    	request.setAttribute("encoded", encoded);
    	    	
    	        RequestDispatcher dispatcher = request.getRequestDispatcher("/crypto.jsp");
    	        dispatcher.forward(request, response);
        	    
        	}else if(mode.equals("ENCODE")) {
        		
        		String plaintextAsString = request.getParameter("plainText");
        		
        		List<String> plain =  Arrays.asList(plaintextAsString.split("\\\n"));
        		
        		List<String> encoded = CryptoUtil.create_encoded(password, plain);
        		
        		
        		request.setAttribute("encoded", encoded);
        		
        		
        		request.setAttribute("plain", plain);
        		
        		
      
                RequestDispatcher dispatcher = request.getRequestDispatcher("/crypto.jsp");
                dispatcher.forward(request, response);
        	}
    	} 
        
    }
}
