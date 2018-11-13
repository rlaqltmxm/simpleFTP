import java.io.*;
import java.net.*;

public class client {
	public static void main(String[] args) throws Exception {

		Socket sock = null;
	    BufferedReader fromkeyboard = null;
	    BufferedReader fromserver = null;
	    PrintWriter toserver = null;
	    //InputStreamReader isr = null;
	    String str;
	    String command, option;
        
	    System.out.println("Enter the port number");
        fromkeyboard = new BufferedReader(new InputStreamReader(System.in));
        int portnum = fromkeyboard.read();
        String checkline = fromkeyboard.readLine();

	    sock = new Socket();
	    sock.connect(new InetSocketAddress("127.0.0.1", portnum), 10000);
	    sock.setSoTimeout(10000);
        System.out.println("Connecting...");
        
        fromserver = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        toserver = new PrintWriter(sock.getOutputStream());
        
        while(true) {
        	
        	
        	System.out.println("Enter the message");
            str = fromkeyboard.readLine();
            toserver.println(str);
            toserver.flush();
            //System.out.println("count//");
            String[] temp = str.split(" ");
        	command = temp[0];
        	
            if(temp.length == 1) option = null; //no argument
            else option = temp[1];

            
            if(command.equals("GET")) 
            	receieve(sock.getInputStream(), option);
            
            else if(command.equals("PUT"))
            	send(sock.getOutputStream(), option);
            
            else if(command.equals("CD"))
            	changeDirectory(fromserver);
            
            else if(command.equals("LIST"))
            	fileList(fromserver);
            
            else if(command.equals("QUIT")) break;
            
            else
            	System.out.print("Invalid command. ");
            
        }
        
        System.out.println("Connection terminate.");
        toserver.close();
        fromserver.close();
        sock.close();

    }
	
	public static void changeDirectory(BufferedReader fromserver) throws Exception{
		
		String dirName = fromserver.readLine();
		System.out.println(dirName);
	}
	
	public static void fileList(BufferedReader fromserver) throws Exception{
		
		String line;
		while (!(line = fromserver.readLine()).equals("EOF")) {
	        System.out.println(line);
	        //System.out.println("test2");
	    }
	}
	
	public static void send(OutputStream toserver, String option) throws Exception {

		PrintWriter pr = new PrintWriter(toserver);

        try {
        	File myFile = new File(option);
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            System.out.println("Sending...");
            toserver.write(mybytearray, 0, mybytearray.length);
            toserver.flush();
            
            System.out.println(myFile.getName()+" transferred / "+myFile.length()+" bytes");
        }catch (Exception e) {
        	System.out.println("No such file exist");
        }
		
    }
	
	public static void receieve(InputStream fromserver, String option) throws Exception {
		
		try{
			int bytesRead;
	        byte[] mybytearray = new byte[6666666];
	        File myfile = new File(option);
	        FileOutputStream fos = new FileOutputStream(myfile.getName());
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	        bytesRead = fromserver.read(mybytearray, 0, mybytearray.length);
	        //current = bytesRead;

	        /*do {
	            bytesRead = fromserver.read(mybytearray, current, (mybytearray.length - current));
	            if (bytesRead >= 0)
	                current += bytesRead;
	        } while (bytesRead > -1);
	        */

	        bos.write(mybytearray, 0, bytesRead);
	        bos.flush();
	        bos.close();
        	System.out.println("Receieved "+myfile.getName()+" / "+myfile.length()+" bytes");

		}catch (Exception e) {
		}
        
	}

} 