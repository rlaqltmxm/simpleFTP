import java.io.*;
import java.net.*;
import java.nio.file.Paths;

public class server {
	public static void main(String[] args) throws Exception {

		ServerSocket ss = null;
	    Socket clientsocket = null;
	    BufferedReader fromclient = null;
	    BufferedReader fromkeyboard = null;
	    PrintWriter toclient = null;
	    File nowdirectory = null;
	    
	    System.out.println("Enter the port number");
	    fromkeyboard = new BufferedReader(new InputStreamReader(System.in));
	    
	    int portnum = fromkeyboard.read();
	    ss = new ServerSocket(portnum);
        System.out.println("**Server Started");
        
        while(true) {
        	System.out.println("Waiting for the request...");
            clientsocket = ss.accept();
            System.out.println("**Got a client");

            nowdirectory = new File(".");
            fromclient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
            toclient = new PrintWriter(clientsocket.getOutputStream());
        	
            while(true) {

            	
                String str = fromclient.readLine();
                
                String command, option;
                
                String[] temp = str.split(" ");
            	command = temp[0];
            	
            	if(temp.length == 1) option = null; //no argument
                else option = temp[1];
            	
            	
                if(command.equals("GET"))
                	send(clientsocket.getOutputStream(), option);
                
                else if(command.equals("PUT"))
                	receiveFile(clientsocket.getInputStream(), nowdirectory, option);
                
                else if(command.equals("CD"))
                    nowdirectory = changeDirectory(option, nowdirectory, toclient);
                
                else if(command.equals("LIST"))
                	fileList(option, nowdirectory, toclient);
                
                else if(command.equals("QUIT")) {
                	System.out.println("**Connection terminate.");
                	//System.out.println("");
                	fromclient.close();
                    toclient.close();         
                    clientsocket.close();
                    break;
                }
                
            	//System.out.println("now server directory is "+nowdirectory.getCanonicalPath());    
            }
            
        }
        
    }

	public static File changeDirectory(String dirName, File nowdirectory, PrintWriter toclient) throws Exception{
		
		if(dirName == null) {
			toclient.println(nowdirectory.getCanonicalPath());
		}
		else {
			boolean result = false;
			File dir = new File(dirName);
		
			if(!dir.isDirectory() || !dir.exists())
				toclient.println("Failed - directory name is invalid");
			else {
		
				if(dir.toPath().isAbsolute()) {
					
					nowdirectory = new File(dirName);
					result = (System.setProperty("user.dir", dir.getAbsolutePath()) != null);

				}
				else {
					nowdirectory = new File(dir.getCanonicalPath());
					result = (System.setProperty("user.dir", dir.getAbsolutePath()) != null);
				}
				toclient.println(nowdirectory.getCanonicalPath());
			}
		}
		toclient.flush();
		return nowdirectory;
		
	}
	
	public static void fileList(String dirName, File nowdirectory, PrintWriter toclient) {
				
		File[] files = null;
		
		if(dirName == null) {
			files = nowdirectory.listFiles();
			for(File f: files) {
				String fileName = f.getName();
				
				if(f.isDirectory()) {
					toclient.println(fileName+",-");
				}
				else {
					toclient.println(fileName+","+f.length());
				}
			}
		}
		
		else{
			File dir = new File(dirName);
			if(!dir.isDirectory())
				toclient.println("Failed - directory name is invalid");			
			
			else {
				
				files = dir.listFiles();
				for(File f: files) {
					String fileName = f.getName();
					
					if(f.isDirectory()) {
						toclient.println(fileName+",-");
					}
					else {
						toclient.println(fileName+","+f.length());
					}
				}
			}
		}
		toclient.println("EOF");
		toclient.flush();
		
	}
	
    public static void send(OutputStream toclient, String option) {
    	
		PrintWriter pr = new PrintWriter(toclient);

    	try{
    		
    		File myFile = new File(option);
    		byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            System.out.println("Sending...");
            toclient.write(mybytearray, 0, mybytearray.length);
            //pr.write((int)myFile.length());
            toclient.flush();
    	} catch (Exception e) {
    		pr.write("Failed - Such file does not exist!");
    		pr.flush();
    	}
        
    }

    public static void receiveFile(InputStream fromclient, File nowdirectory, String option) throws Exception {
        
    	try {
    		int bytesRead;
            byte[] mybytearray = new byte[6666666];
            System.out.println(nowdirectory.getCanonicalPath());
            File myfile = new File(nowdirectory.getCanonicalPath(), option);

            FileOutputStream fos = new FileOutputStream(myfile.getCanonicalPath());
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = fromclient.read(mybytearray, 0, mybytearray.length);

            bos.write(mybytearray, 0, bytesRead);
            bos.flush();
            bos.close();
            //System.out.println("receieve success");
    	}catch (Exception e) {
    	}
        
    }
} 

