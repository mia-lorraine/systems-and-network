//
//  CustomerRegexServer.c
//
//
//  Created by Mia Laurea on 3/6/18.
//  Copyright © 2018 Mia Laurea. All rights reserved.
//

import  java.net.*;
import  java.io.*;
import  java.util.*;
import  java.sql.*;
import java.util.regex.*;


public  class CustomerRegexServer {
    
    public static void main (String argv[]) {
        
        ServerSocket  sSocket = null;
        
        try {
            sSocket =  new  ServerSocket(15060, 10);
        }
        catch (IOException e) {
            System.out.println ("Fail to set up port 15060\n"
                                + "Error type: " + e);
            System.exit(1);
            
        } //end try
        
        
        Socket  cSocket = null;
        PrintStream  toSocket = null;
        
        Scanner fromSocket = null;
        
        
        try {
            //csocket goes to client
            cSocket = sSocket.accept();
            
            //getting from the socket
            fromSocket = new Scanner(cSocket.getInputStream());
            
            toSocket = new PrintStream(cSocket.getOutputStream());
        }
        catch (IOException e) {
            // Lazy exception handling;
            
        }
        
        
        
        try {
            
            
            
            Class.forName("com.mysql.jdbc.Driver");
            
            
            Connection conn = DriverManager.getConnection
            ("jdbc:mysql://localhost/mlaurea?user=mlaurea&password=m9103l");
            Statement stmt = conn.createStatement();
            
            String oneline = fromSocket.nextLine();
            
            Pattern searchPattern = Pattern.compile ("customerid.*\\<(.*)\\>");
            Matcher currentMatch = searchPattern.matcher(oneline);
            
            
            String customerid ="";
            
            if (currentMatch.find()) { customerid = currentMatch.group(1); }
            
            // CORRECT SQL STATEMENT
          //  select customer.customer_id, customer.name,  sales.sales_date, product.product_name, price.product_price, sales.quantity, price.product_price * sales.quantity from customer, sales, product, price where customer.customer_id = sales.customer_id and sales.product_upc = product.product_upc and product.product_upc = price.upc_code and customer.customer_id ='2689881';
           ResultSet customer = stmt.executeQuery("select name from customer where customer_id = "+customerid);
		while(customer.next()){
	toSocket.printf("\n\nPurchase report for " + customerid + "   	%20s\n\n", customer.getString("name"));
	}

           ResultSet customerInfo = stmt.executeQuery
            ("select sales.sales_date, product.product_name, price.product_price, sales.quantity, sales.quantity*price.product_price as amount from customer, sales, product, price where customer.customer_id = sales.customer_id and sales.product_upc = product.product_upc and product.product_upc = price.upc_code and customer.customer_id =\'" + customerid + "\' order by sales.sales_date asc");
           

             toSocket.printf("Date         Product                                                   Price    Quantity      Amount \n\n");
            while(customerInfo.next()){   // In this loop, process ResultSet and send output to client.
               
                toSocket.printf ("%-10s %-60s %-10.2f %-10d %-5.2f \n",
                                 customerInfo.getString("sales_date"),
                                 customerInfo.getString("product_name"),
                                 customerInfo.getFloat("product_price"),
                                 customerInfo.getInt("quantity"),
                                 customerInfo.getFloat("amount"));
                
                                //add one more "amount" calculating.
            }
        }
        catch(SQLException e1)
        {
            toSocket.println(e1.getMessage());
        }
        catch(  ClassNotFoundException e1)
        {
            toSocket.println(e1.getMessage());
        } //end try
        
        
        try {
            toSocket.close();
            sSocket.close();
        }
        catch (IOException e) {
            // Lazy exception handling;
        }
    
        
    } //end main
} //end class CustomerRegexServer


