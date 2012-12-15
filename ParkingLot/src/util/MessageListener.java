package util;

import java.io.IOException;
import java.net.Socket;


import messaging.AbstractMessage;

/**
 * This is an abstraction which allows the gates to communicate with the simulation.  
 * It is just a runnable that sits and continually decodes messages and passes them onto the gate for action
 */
public class MessageListener extends Thread {

	private MessageHandler handler;
	private Socket socketListeningOn;
	
	/**
	 * Creates a simulation listener which listens on the given socket
	 * @param gateListening The gate which will act upon the received messages
	 * @param listeningFor The socket upon which this is listening.
	 */
	public MessageListener(MessageHandler gateListening, Socket listeningFor)
	{
		this.socketListeningOn = listeningFor;
		this.handler = gateListening;
	}
	
	@Override
	public void run() {
		while(!this.socketListeningOn.isClosed()) //Keep running until we give it permission to die
		{
			AbstractMessage messageReceived;
			try {
				messageReceived = AbstractMessage.decodeMessage(socketListeningOn.getInputStream());
				
				if(messageReceived == null){ //if we got sent a bad message don't worry about it
					continue;
				}
				
			} catch (IOException e) {
                //e.printStackTrace();
                try {
					this.socketListeningOn.close();
				} catch (IOException e1) {//it's already closed so we don't need to worry about that		
				}
                
                handler.onSocketClosed(this.socketListeningOn);
				break;
			}
			handler.onMessageReceived(messageReceived, this);
		}
		try {
			socketListeningOn.close();
		} catch (IOException e) {
			//cry
		}
	}
	
	/**
	 * Writes a message to the socket that this object is listening on
	 * @param messageToSend The message which is being sent.
	 * @throws IOException
	 */
	public void writeMessage(AbstractMessage messageToSend) throws IOException
	{
		AbstractMessage.encodeMessage(socketListeningOn.getOutputStream(), messageToSend);
	}
	
	public boolean equals(Object o){
		if(!(o instanceof MessageListener)){
			return false;
		}
		MessageListener other = (MessageListener) o;
		return other.getSocketListeningOn().equals(this.socketListeningOn);
	}
	
	public void close() throws IOException {
		this.socketListeningOn.close();
	}

	public MessageHandler getHandler() {
		return handler;
	}

	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}

	public Socket getSocketListeningOn() {
		return socketListeningOn;
	}

	public void setSocketListeningOn(Socket socketListeningOn) {
		this.socketListeningOn = socketListeningOn;
	}
	
	

}
