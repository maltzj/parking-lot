package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class MessageReceiver {
	
	private final int BACKLOG = 10; //Number of concurrent connections to socket
	
	protected InetAddress ipAddress;
	protected int port;
	
    protected ServerSocket serverSocket;

	public MessageReceiver(ServerSocket socket)
	{
		this.serverSocket = socket;
		this.ipAddress = socket.getInetAddress();
		this.port = socket.getLocalPort();
	}
	
    /** defaults to localhost 
     * @throws IOException 
     * @throws UnknownHostException */
    public MessageReceiver(int port) throws UnknownHostException, IOException
    {
        	this(InetAddress.getLocalHost(), port); 
    }
	
	public MessageReceiver(InetAddress ipAddress, int port) throws IOException {
		this.ipAddress = ipAddress;
		this.port = port;
		this.serverSocket = new ServerSocket(port, BACKLOG, ipAddress);
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}

	public ServerSocket getSocket() {
		return serverSocket;
	}
	
}
