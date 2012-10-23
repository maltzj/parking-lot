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
		while(GateImpl.stillRunning == true)
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeMessage(AbstractMessage messageToSend) throws IOException
	{
		AbstractMessage.encodeMessage(socketListeningOn.getOutputStream(), messageToSend);
	}

}
