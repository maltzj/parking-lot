package gates;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.TimeMessage;

import util.MessageReceiver;

public class SimulationMessageListener extends MessageReceiver implements Runnable {

	protected Gate gateListeningFor;
	
	public SimulationMessageListener(int port, Gate listeningFor) {
		super(port);
		this.gateListeningFor = listeningFor;
	}

	@Override
	public void run() {
		while(GateImpl.stillRunning)
		{
			try {
				AbstractMessage messageReceived = AbstractMessage.decodeMessage(this.socket.getInputStream());
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
				//herpderp had trouble learning
			}
		}
		
	}

	
	
}
