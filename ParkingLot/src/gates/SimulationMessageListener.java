package gates;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.TimeMessage;
import util.MessageReceiver;

public class SimulationMessageListener extends MessageReceiver implements Runnable {

	protected Gate gateListeningFor;
	
	public SimulationMessageListener(int port, Gate listeningFor) throws UnknownHostException, IOException {
		super(port);
		this.gateListeningFor = listeningFor;
	}

	@Override
	public void run() {
		while(GateImpl.stillRunning)
		{
			try {
				Socket clientSocket = serverSocket.accept();
				
				AbstractMessage messageReceived = AbstractMessage.decodeMessage(clientSocket.getInputStream());
				switch(messageReceived.getMessageType())
				{
					case AbstractMessage.TYPE_CAR_ARRIVAL:
					{
						gateListeningFor.onCarArrived((CarArrivalMessage) messageReceived);
					}
					case AbstractMessage.TYPE_TIME_MESSAGE:
					{
						gateListeningFor.onTimeUpdate((TimeMessage) messageReceived);
					}
					default:
					{
						//Do something
					}
				}
			} catch (IOException e) {
			}
		}
		
	}

	
	
}
