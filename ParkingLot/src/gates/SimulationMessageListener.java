package gates;

import java.io.IOException;
import java.net.Socket;

import messaging.AbstractMessage;

/**
 * This is an abstraction which allows the gates to communicate with the simulation.  
 * It is just a runnable that sits and continually decodes messages and passes them onto the gate for action
 *
 */
public class SimulationMessageListener implements Runnable {

	private GateImpl gateListeningFor;
	Socket socketListeningOn;
	
	/**
	 * Creates a simulation listener which listens on the given socket
	 * @param gateListening The gate which will act upon the received messages
	 * @param listeningFor The socket upon which this is listening.
	 */
	public SimulationMessageListener(GateImpl gateListening, Socket listeningFor)
	{
		this.socketListeningOn = listeningFor;
		this.gateListeningFor = gateListening;
	}
	
	@Override
	public void run() {
		while(!gateListeningFor.die) //Keep running until we give it permission to die
		{
			AbstractMessage messageReceived;
			try {
				messageReceived = AbstractMessage.decodeMessage(socketListeningOn.getInputStream());
			} catch (IOException e) {
				break;
			}
			gateListeningFor.onMessageArrived(messageReceived);
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

}
