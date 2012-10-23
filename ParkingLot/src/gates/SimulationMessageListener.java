package gates;

import java.io.IOException;
import java.net.Socket;

import messaging.AbstractMessage;

public class SimulationMessageListener implements Runnable {

	private GateImpl gateListeningFor;
	Socket socketListeningOn;
	
	public SimulationMessageListener(GateImpl gateListening, Socket listeningFor)
	{
		this.socketListeningOn = listeningFor;
		this.gateListeningFor = gateListening;
	}
	
	@Override
	public void run() {
		while(!gateListeningFor.die)
		{
			AbstractMessage messageReceived;
			try {
				messageReceived = AbstractMessage.decodeMessage(socketListeningOn.getInputStream());
			} catch (IOException e) {
                //e.printStackTrace();
                gateListeningFor.killMyself();
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
	
	public void writeMessage(AbstractMessage messageToSend) throws IOException
	{
		AbstractMessage.encodeMessage(socketListeningOn.getOutputStream(), messageToSend);
	}

}
