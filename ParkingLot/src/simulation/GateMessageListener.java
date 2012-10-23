package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import messaging.AbstractMessage;

public class GateMessageListener extends Thread{
	
	TrafficGenerator generator;
	InetAddress ipAddress;
	int port;
	Socket socketListeningOn;
	
	public GateMessageListener(TrafficGenerator generator, Socket socketConnectingTo)
	{
		this.socketListeningOn = socketConnectingTo;
		this.ipAddress = socketConnectingTo.getInetAddress();
		this.port = socketConnectingTo.getPort();
		this.generator = generator;
	}
	
	public void run(){
        while(!generator.die){
            AbstractMessage messageReceived;
            try {
                messageReceived = AbstractMessage.decodeMessage(socketListeningOn.getInputStream());
                generator.onMessageArrived(messageReceived, this);
            } catch (IOException e) {
                //do stuff
                break;
            }		
        }	
	}

    public void killMyself()
    {
        try {
            this.socketListeningOn.close();
        } 
        catch(Exception e)
        {
            e.printStackTrace();
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
		if(other instanceof GateMessageListener)
		{
			return ((GateMessageListener) other).getSocketListeningOn().getPort() == this.socketListeningOn.getPort() && ((GateMessageListener) other).getSocketListeningOn().getInetAddress().equals(this.socketListeningOn.getInetAddress());
		}
		return false;
	}
	

}
