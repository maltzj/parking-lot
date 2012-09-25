package simulation;

import java.net.InetAddress;

public class MessageReceiver {
	
	InetAddress ipAddress;
	int port;
	
	public MessageReceiver(InetAddress ipAddress, int port) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
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
	
	
	
}