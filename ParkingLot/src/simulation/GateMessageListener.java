package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import util.HostPort;

import messaging.AbstractMessage;

/**
 * This class specifies the message listener for the Simulation.  Much like the one for the gates this class basically just sits in an event loop
 */
public class GateMessageListener extends Thread{
	
	TrafficGenerator generator;
	InetAddress ipAddress;
	int port;
	Socket socketListeningOn;
	
	
	
	/**
	 * Creates a gate listener which reports to a given traffic generator and listens on a particular socket
	 * @param generator The generator which this listener reports to
	 * @param socketConnectingTo The socket which the listener listens on
	 */
	public GateMessageListener(TrafficGenerator generator, Socket socketConnectingTo)
	{
		this.socketListeningOn = socketConnectingTo;
		this.ipAddress = socketConnectingTo.getInetAddress();
		this.port = socketConnectingTo.getPort();
		this.generator = generator;
	}
	
	public void run(){
        while(!TrafficGenerator.die  && !socketListeningOn.isClosed()){
            AbstractMessage messageReceived;
            try {
                messageReceived = AbstractMessage.decodeMessage(socketListeningOn.getInputStream());
                generator.onMessageArrived(messageReceived, this);
            } catch (IOException e) {
                this.killMyself();
                break;
            }		
        }	
	}

	/**
	 * Kills the socket that is being listened on
	 */
    public void killMyself()
    {
        try {
            this.socketListeningOn.close();
        } 
        catch(Exception e)
        {
           //do whatever the fuck we want
        }
    }
	
	public void writeMessage(AbstractMessage messageToSend) throws IOException
	{
		AbstractMessage.encodeMessage(socketListeningOn.getOutputStream(), messageToSend);
	}

	public TrafficGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(TrafficGenerator generator) {
		this.generator = generator;
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

	public Socket getSocketListeningOn() {
		return socketListeningOn;
	}

	public void setSocketListeningOn(Socket socketListeningOn) {
		this.socketListeningOn = socketListeningOn;
	}
	
	public int hashCode(){
		return (""+this.port).hashCode();
	}
	
	public boolean equals(Object other){
		if(other instanceof GateMessageListener){
			return ((GateMessageListener) other).getSocketListeningOn().getPort() == this.socketListeningOn.getPort() 
					&& ((GateMessageListener) other).getSocketListeningOn().getInetAddress().equals(this.socketListeningOn.getInetAddress());
		}
		else if(other instanceof HostPort){
			HostPort port = (HostPort) other;
			System.out.println("port has " + port.port + " and socket is " + this.socketListeningOn.getPort());
			return port.port == this.socketListeningOn.getPort() && this.socketListeningOn.getInetAddress().equals(port.iaddr);
		}
		return false;
	}
	

}
