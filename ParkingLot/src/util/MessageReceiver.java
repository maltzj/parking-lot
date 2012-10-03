package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MessageReceiver {
	
	protected InetAddress ipAddress;
	protected int port;
	protected Socket socket;
	
	public MessageReceiver(InetAddress ipAddress, int port) throws IOException {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.socket = new Socket(ipAddress, port);
	}
	
	public MessageReceiver(Socket socket)
	{
		this.socket = socket;
		this.ipAddress = socket.getInetAddress();
		this.port = socket.getPort();
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
	
}