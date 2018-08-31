import  java.net.*;
import  java.io.*;
import  java.util.*;
import  java.util.regex.*;
import  java.sql.*;

/*
 *
 */

public  class  project6server {
    
    final  static  int  SOCKET_NUMBER = 15060;
    
   
    public static void main (String argv[]) {
        
        String  htmlHeader1NotFound = "HTTP/1.1 404 Not Found\r\n";
        String  htmlHeader1 = "HTTP/1.1 200 OK\r\n";
        String  htmlHeader2 = "Date: Tue, 23 Feb 2016 16:41:01 GMT\r\n";
        String  htmlHeader3 = "Connection: close\r\n";
        String  htmlHeader4 = "Content-Type: text/html\r\n";
        
        // The following String will be overriden later
        //
        String  htmlHeader5 = "Content-Length:      \r\n\r\n";
        
        int   loopCounter;
        
        
        ServerSocket  listeningSocket = null;
        
        try {
            listeningSocket = new ServerSocket(SOCKET_NUMBER);
        }
        catch (IOException e) {
            System.out.println ("Fail to allocate IP port "
                                + SOCKET_NUMBER + "\n Error type: " + e);
            System.exit(1);
            
        } //end try
        
        
        System.out.println ("\nServer is now listening on port " + SOCKET_NUMBER + "\n");
        
        
        for (loopCounter = 1;  loopCounter <= 5;  loopCounter++)
        {
            
            Socket  connectSocket = null;
            try {
                connectSocket = listeningSocket.accept();
            }
            catch (IOException e) {
                System.out.println ("Fail to accept connection at IP port "
                                    + SOCKET_NUMBER + "\n Error type: " + e);
                System.exit(1);
                
            } //end try
            
            try {
                
                
                Scanner  networkIn = new Scanner(connectSocket.getInputStream());
                
                DataOutputStream  networkOut = new DataOutputStream(
                                                                    connectSocket.getOutputStream());
                
                
                //  Looking for the GET line and extract the url
                
                int iLoc1;
                String url = "";
                
                
                
                String stringBuffer = networkIn.nextLine();
                while(!(stringBuffer.equals("")))
                {
                    System.out.println(stringBuffer);
                    
                    
                    if  (stringBuffer.startsWith("GET"))
                    {
                        iLoc1 = stringBuffer.indexOf("/");
                        url = stringBuffer.substring(iLoc1);
                    }
                    stringBuffer = networkIn.nextLine();
                    
                    
                }
                
                //  generate the appropriate html page
                //
                
                boolean  keywordFound = false;
                String   htmlContent;
                
                if  (url.startsWith("/simple"))  {
                    keywordFound = true;
                    htmlContent = generateSimplePage();
                }
                else if  (url.startsWith("/hello"))  {
                    keywordFound = true;
                    htmlContent = generateForm();
                }
                else if  (url.startsWith("/verify"))  {
                    keywordFound = true;
                    htmlContent = generateVerifyPage(url);
                }
                else  {
                    htmlContent = generateNotFoundPage(url);
                }
                
                
                System.out.println ("\nNow sending html to client\n");
                
                
                //      override the predefined Content-Length parameter.
                //
                
                //              htmlHeader5 = "Content-Length:  " + contentLen + "\r\n\r\n";
                
                if  (keywordFound)  {
                    networkOut.writeBytes (htmlHeader1);
                }
                else  {
                    networkOut.writeBytes (htmlHeader1NotFound);
                }
                networkOut.writeBytes (htmlHeader2);
                networkOut.writeBytes (htmlHeader3);
                networkOut.writeBytes (htmlHeader4);
                networkOut.writeBytes (htmlHeader5);
                
                networkOut.writeBytes (htmlContent);
                
                networkOut.flush();
                
                networkOut.close();
                connectSocket.close();
            }
            catch (IOException e) {
                // Lazy exception handling;
                
            } //end try
            
            
        } //end for loopCounter
        
    } //end main
    
    
    static  String  generateForm ()  {
        StringBuffer html3 = new StringBuffer();
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection
            ("jdbc:mysql://localhost/mlaurea?user=mlaurea&password=m9103l");   // modify this line
            
            Statement stmt = conn.createStatement();
            
            ResultSet product = stmt.executeQuery
            ("select product.product_upc, product.product_name from product");
            
            
            while(product.next()){
                
                String product_name = product.getString("product_name");
                String product_upc = product.getString("product_upc");
                
                html3.append
                ("<option value = \'" + product_upc + "\'>" + product_name + "</option>");
                
            } // end while
            
        }// end try SQL
        catch (ClassNotFoundException e)
        {
            System.out.println("JDBC connector not found. Error message is: " + e.getMessage());
            System.exit(1);
            
        } //end try
        catch (SQLException e){
            
        }
        
        
        String html1 =
        "<html>\r\n"
        + "<head><title>Greetings for Users</title></head>\r\n"
        + "<body style = 'text-align: center;'>"
        + "<form name =\"greeting\" "
        + "action=\"http://10.15.14.26:" + SOCKET_NUMBER + "/verify\" "
        + "method=\"GET\">\r\n";
        
        //  In the code following this, the customer is found in the database
        String  html2 =
        "<p>"
        + "Enter Customer ID: <br> <input type=text  name=\"id\"  size=30>"
        + "</p>\r\n"
        + "<br> Select a Product <br>"
        + "<select name =\"product\">";
        
        
        String  html4 =
        "</select>\r\n"
        + "<p>"
        + "Enter a Quantity: <br> <input type=text  name=\"quantity\"  size=30>"
        + "</p>\r\n";
        
        
        String  html5 =
        "<p>"
        +   "<input type=\"submit\" value=\"Buy\">\r\n"
        + "</p>\r\n"
        + "</form>"
        + "</body>"
        + "</html>\r\n";
        
        
        // process the remaining rows of  ResultSet  in a while loop
        //   Note the use of StringBuffer so that we can append to html5
        
        return  (html1 + html2 + html3 + html4 + html5);
       
    } //end generateForm
    
    
    static  String  generateSimplePage ()  {
        
        String  htmlText1 = "<html> <head> <title>A simple page</title> </head> \r\n";
        String  htmlText2 = "<body> <h1>Welcome to a Simple Page!</h1> <hr>\r\n"
        + " <p> This paragraph will display on your web browser.  It \r\n"
        + "shows that text can be entered in free form without \r\n"
        + "any fancy formatting in the html source file. </p>\r\n";
        String  htmlText3 = "<center><b>February 23, 2016 \r\n"
        + "</b></center> \r\n </body> </html>                      \r\n";
        
        return (htmlText1 + htmlText2 + htmlText3);
        
    } //end generateSimplePage
    
    
    static  String  generateVerifyPage (String url)  {
        
        /* first extract the parameters from the  id=   and  lang=
         of the url and store them in the following strings */
        
        String   idFromClient = "";
        String   productFromClient = "";
        String   clientQuantity = "";
        
        Pattern  getID = Pattern.compile("id\\=(.*?)[& ]");
        Matcher  currentMatch  = getID.matcher(url);
        
        if  (currentMatch.find())
        {
            idFromClient = currentMatch.group(1);
        }
        
        Pattern  getProduct = Pattern.compile("product\\=(.*?)[& ]");
        currentMatch  = getProduct.matcher(url);
        
        if  (currentMatch.find())
        {
            productFromClient =  currentMatch.group(1);
        }
        
        Pattern findQuantity = Pattern.compile("quantity\\=(.*?)[& ]");
        currentMatch = findQuantity.matcher(url);
        
        if  (currentMatch.find())
        {
            clientQuantity =  currentMatch.group(1);
        }
        
        String name = "";
        double balance = 0.0;
        double price = 0.0;
        String productName = "";
        double totalPrice = 0.0;
        double calcBalance = 0.0;
        
        
        
        try{
            Class.forName("com.mysql.jdbc.Driver"); //SQL driver for java
           
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/mlaurea?user=mlaurea&password=m9103l"); //connection string for SQL server
            
            Statement stmt = conn.createStatement();
            
            ResultSet getName = stmt.executeQuery("select name from customer where customer_id ="+idFromClient);
            while(getName.next() ){
                name = getName.getString("name");
            }

            ResultSet acct_balance = stmt.executeQuery("select account_balance from customer where customer_id ="+idFromClient);
            while(acct_balance.next() ){
                balance = acct_balance.getFloat("account_balance");
            }
            
            ResultSet get_product = stmt.executeQuery("select product_name from product where product_upc="+productFromClient);
            while(get_product.next() ){
                productName = get_product.getString("product_name");
            }
            
            ResultSet priceProduct = stmt.executeQuery("select product_price from price where upc_code="+productFromClient);
            while(priceProduct.next() ){
                price = priceProduct.getFloat("product_price");
            }
            
            
            
        }catch (SQLException e1){
            System.out.println(e1.getMessage());
        }
        catch (ClassNotFoundException e2){
            System.out.println(e2.getMessage());
        }
        
        int receivedQuant = Integer.parseInt(clientQuantity);
        
        
        totalPrice = receivedQuant * price;
        
        calcBalance = balance + totalPrice;
        
        String formatPrice =  String.format("%8.2f", price);
        String formatTotalPrice = String.format("%8.2f", totalPrice);
        String formatCalcBalance =  String.format("%8.2f", calcBalance);
        String formatBalance = String.format("%8.2f", balance);
        
        /* generating html */
        
        String  htmlText1 = "<html> ";
        String  htmlText2 = "<body> <p> "
        + idFromClient
        + " "
        + name
        + " "
        + "Account Balance = "
        + formatBalance
        + "<br><br><br></p>\r\n";

        
        String  htmlText3 = "<p>  "
        + productName
        + " "
        + clientQuantity
        + " x "
        +  formatPrice
        + " = "
        + formatTotalPrice
        + "<br><br><br><br> New Account Balance = "
        + formatCalcBalance
        + "</body> </html> \r\n";
        
        
        String  htmlTEMP = "<html> <head> <title> Under Construction </title> </head> \r\n"
        + "<body>  <p> This is under construction. </p> \r\n"
        + "</body> </html>   \r\n";
        
        // return (htmlTEMP);
        
        return (htmlText1 + htmlText2 + htmlText3);
        
    } //end generateVerifyPage
    
    
    static  String  generateNotFoundPage (String url)  {
        
        String  htmlText1 = "<html> <body> \r\n";
        String  htmlText2 = "<h1>Not Found</h1>"
        + "<p>The requested URL "
        + url
        + " was not found on this server.</p>\r\n";
        String  htmlText3 = "</body> </html> \r\n";
        
        return (htmlText1 + htmlText2 + htmlText3);
        
    } //end generateNotFoundPage
    
    
} //end class SimpleWebServer

