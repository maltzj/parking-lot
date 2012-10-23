package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import simulation.TrafficGenerator;
import messaging.*;

/** A message receiver implements a ServerSocket and listens on messages of all types.
 * In order to subclass this, you have to implement onCarArrivalMessage received and onTimeUpdateMessage received 
 */
public class MessageReceiver implements Runnable {
	
	private final int BACKLOG = 10; //Number of concurrent connections to socket
	
	public InetAddress ipAddress;
    public int port;
    protected TrafficGenerator generator;
	ServerSocket server;
    protected MessageReceiver(TrafficGenerator gen) throws IOException
	{
		Config c = new Config();
		
		this.ipAddress = c.trafficGenerator.iaddr;
		this.port = c.trafficGenerator.port;
		this.server = new ServerSocket(port);
		
		this.generator = gen;
		
	}

    public boolean die = false;
	
    /** defaults to localhost 
     * @throws IOException 
     * @throws UnknownHostException */
    protected MessageReceiver(int port) throws UnknownHostException, IOException
    {
        	this(InetAddress.getLocalHost(), port, null); 
    }
	
    /**
     * Initializes a MessageReceiver with a given ipAddress and port.
     * @param ipAddress
     * @param port
     * @throws IOException
     */
	public MessageReceiver(InetAddress ipAddress, int port, TrafficGenerator generator) throws IOException {
		this.ipAddress = ipAddress;
		this.port = port;
		this.server = new ServerSocket(port, BACKLOG, ipAddress);
		this.generator = generator;
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}

	public ServerSocket getSocket() {
		return server;
	}

    /** Implement these abstract methods. 
     * @throws IOException */
	public void onConnectionReceived(Socket socket) throws IOException{
		this.generator.onConnectionReceived(socket);
		
	}

	@Override
	public void run() {
		
		while(!TrafficGenerator.die)
		{
			Socket clientSocket = null;
			try {
				clientSocket = server.accept();
				System.out.println("got a socket");
                this.onConnectionReceived(clientSocket);
			} catch (IOException e) {
                e.printStackTrace();

			}
		}
	}
}
