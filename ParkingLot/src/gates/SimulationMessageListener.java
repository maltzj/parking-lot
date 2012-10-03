package gates;

import java.net.InetAddress;

import util.MessageReceiver;

public class SimulationMessageListener extends MessageReceiver implements Runnable {

	Gate gateListeningFor;
	
	public SimulationMessageListener(InetAddress ipAddress, int port, Gate listeningFor) {
		super(ipAddress, port);
		this.gateListeningFor = listeningFor;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	
}
