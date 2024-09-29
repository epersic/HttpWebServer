package webServer;

import java.awt.Frame;
import java.io.*;
import java.net.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
public class MainServer {
	private static final int SERVER_PORT = 8080;  //port running the server
	
	public static void main(String[] args) throws Exception {
		
		Gui gui = new Gui();
		gui.appendMessage("Configure the server");

		boolean isRunning = gui.isServerOn();
		while(!isRunning) {
			System.out.println(isRunning);
			 isRunning = gui.isServerOn();
		}
		int guiPort = Integer.parseInt(gui.getPort());
		
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream keyStoreInput = new FileInputStream("keystore.jks");
		keyStore.load(keyStoreInput,"password".toCharArray());
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore,"password".toCharArray());
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(),null,null);
		
		SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
		
		
		
		try(SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(guiPort)){
			//serverSocket.setSoTimeout(10000);
			
			gui.appendMessage("Server running on port: "+guiPort);
			System.out.println("Server ready on port: "+guiPort);
			
			while(isRunning) {		//start accepting requests
				Socket clientSocket = sslServerSocket.accept();
				System.out.println("Client connected: "+clientSocket.getInetAddress().toString().substring(1));
				new HttpThread(clientSocket,gui).start();
				isRunning=gui.isServerOn();
			}
			
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gui.appendMessage("[ERROR]: Unknown host exception");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gui.appendMessage("[ERROR]: IOexception");
		}

	}

}
