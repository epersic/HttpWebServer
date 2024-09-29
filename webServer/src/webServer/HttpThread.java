package webServer;
import java.io.*;

import java.io.ObjectInputStream.GetField;
import java.awt.desktop.PrintFilesEvent;
import java.awt.print.PrinterAbortException;
import java.net.Socket;
import java.nio.file.*;
import java.nio.file.Paths;

import java.util.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public class HttpThread extends Thread{
	private  Socket clientSocket;
	private  Gui gui;
	
	public HttpThread(Socket socket,Gui gui) {
		this.clientSocket = socket;
		this.gui = gui;
	}
	
	@Override
	public void run(){
		boolean keepAlive = false;
		do {
			try {
				
				keepAlive = false;
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
				String requestLine = in.readLine();  //parse the first line of request to determine the req. method;
				
				if(requestLine!=null && !requestLine.isBlank()) {
					String firstReqLine[] = requestLine.split(" ");		//extract the method and the requested file from the http request
					String method = firstReqLine[0];
					String fileParams[] = firstReqLine[1].substring(1).split("\\?");
					String file = fileParams[0];
					String reqParams[] = null;
					
					Path filePath = Paths.get(file);
					
					if(fileParams.length>1) {			//extract get params if the exist
						reqParams = fileParams[1].split("&");
						
					}
					
					Hashtable<String, String>httpRequestParams = new Hashtable<>(); //extract the parameters of the http request
					
					while((requestLine=in.readLine()).contains(":")) {
						String[] param = requestLine.split(":");
						if(param.length>1) {
							httpRequestParams.put(param[0].trim(),param[1].trim());
						}
					}
					
					if(httpRequestParams.get("Connection").equals("keep-alive")) {
						keepAlive = true;
					}
					
					
					
					if(method.equals("GET") && fileParams.length == 1) {		//if the request is static, just return the file
						OutputStream dataOut = clientSocket.getOutputStream();
						
						if(Files.exists(filePath)) {
							
							handleGetRequest(filePath, out,dataOut);
						
						}else {

							sendResourceNotFound(dataOut);
						}
						
					}else if ((method.equals("GET") && fileParams.length > 1) || method.equals("POST")) {		//if not static, put the request in a file
						handleParamGetPostRequest(httpRequestParams, method, reqParams);
						
					}else {
						sendMethodNotAllowed(out,clientSocket.getOutputStream());
					}
				}
					
				
					
			} catch (IOException e) {
					
			}
		}while(keepAlive);
		
		System.out.println("Connection to the client "+clientSocket.getInetAddress().toString().substring(1)+" closed"); // close the connection 
		try {clientSocket.close();} catch (IOException e) {e.printStackTrace();}
		
	}
	
	
	private void handleParamGetPostRequest(Hashtable<String, String>httpRequestParams,String method, String reqParams[] ) throws IOException {
		String methodFile = (method.equals("GET")) ? "GET.in" : "POST.in";
		File file = new File(methodFile);
		
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fileWriter = new FileWriter(file);
		
		for(String param : reqParams) {
			fileWriter.write(param+"\n");
		}
		
		Set<Entry<String,String> > entrySet = httpRequestParams.entrySet();
		
		for(Entry<String,String> entry : entrySet) {
			fileWriter.write(entry.getKey()+":"+entry.getValue()+"\n");
		}
		
		fileWriter.write("\n");
		fileWriter.close();
	}
	
	private void handleGetRequest(Path resource, PrintWriter out,OutputStream dataOut) throws IOException {
		
		byte[] fileBytes = Files.readAllBytes(resource);
		
        // Send HTTP response header
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: "+fileBytes.length);
        out.println("Connection: keep-alive");
        out.println();  // Blank line between headers and content
        out.flush();  
        
        dataOut.write(fileBytes);
        dataOut.flush();
        return;
    }
	
	private void sendMethodNotAllowed(PrintWriter out,OutputStream dataOut) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get("./httpResponse/405.html"));
		dataOut.write(fileBytes);
		dataOut.flush();
		clientSocket.close();
		return;
    }
	
	private void sendResourceNotFound(OutputStream dataOut) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get("./httpResponse/404.html"));
		dataOut.write(fileBytes);
		dataOut.flush();
		clientSocket.close();
		return;
	}
}
