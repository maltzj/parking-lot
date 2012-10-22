package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import messaging.*;

/** A message receiver implements a ServerSocket and listens on messages of all types.
 * In order to subclass this, you have to implement onCarArrivalMessage received and onTimeUpdateMessage received 
 */
public abstract class MessageReceiver implements Runnable {
	
	private final int BACKLOG = 10; //Number of concurrent connections to socket
	
	public InetAddress ipAddress;
    public int port;
    protected ServerSocket serverSocket;
	protected MessageReceiver(ServerSocket socket)
	{
		this.serverSocket = socket;
		this.ipAddress = socket.getInetAddress();
		this.port = socket.getLocalPort();
	}

    protected boolean die = false;
	
    /** defaults to localhost 
     * @throws IOException 
     * @throws UnknownHostException */
    protected MessageReceiver(int port) throws UnknownHostException, IOException
    {
        	this(InetAddress.getLocalHost(), port); 
    }
	
    /**
     * Initializes a MessageReceiver with a given ipAddress and port.
     * @param ipAddress
     * @param port
     * @throws IOException
     */
	protected MessageReceiver(InetAddress ipAddress, int port) throws IOException {
		this.ipAddress = ipAddress;
		this.port = port;
		this.serverSocket = new ServerSocket(port, BACKLOG, ipAddress);
		this.serverSocket.setReuseAddress(true);
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

    /** Implement these abstract methods. 
     * @throws IOException */
	public abstract void onMessageArrived(AbstractMessage message) throws IOException;

	@Override
	public void run() {
		
		while(!die)
		{
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				AbstractMessage messageReceived = AbstractMessage.decodeMessage(clientSocket.getInputStream());
                this.onMessageArrived(messageReceived);
                clientSocket.close();
			} catch (IOException e) {

			}
			finally{
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
