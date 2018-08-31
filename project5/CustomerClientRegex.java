import  java.net.*;
import  java.io.*;
import  java.util.*;


public  class  CustomerClientRegex {

    public static void main (String argv[]) {

        Socket  connectSocket = null;
	try {
            connectSocket =  new Socket("10.15.14.26", 15060);
	}
        catch (IOException e) {
	    System.out.println ("Fail to connect at port 15060\n"
				        + "Error type: " + e);
	    System.exit(1);

        } //end try

	System.out.println ("Enter a command to send to the server:"); 

	Scanner UserInput = new Scanner(System.in);
	String inputBuffer = UserInput.nextLine();

        try {
	     PrintStream toServer = new PrintStream(connectSocket.getOutputStream());

	     toServer.printf("%s\n", inputBuffer);

		
	    Scanner  networkIn = new Scanner(connectSocket.getInputStream());


	    while (networkIn.hasNextLine())  {
                String  readBuffer =  networkIn.nextLine();
                System.out.println (readBuffer);
            }

            networkIn.close();
 	    connectSocket.close();
	}
        catch (IOException e) {
	    // Lazy exception handling;

        } //end try

    } //end main
} //end class InputClient


