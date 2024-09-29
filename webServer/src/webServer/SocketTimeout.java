package webServer;

import java.net.Socket;

public class SocketTimeout extends Thread{
	private Socket clientSocket;
	public SocketTimeout(Socket socket) {
		this.clientSocket=socket;
	}
	
	
}
