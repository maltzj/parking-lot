package simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import util.MessageReceiver;

import car.Car;

import messaging.GateSubscribeMessage;
import messaging.TimeSubscribeMessage;

public class SimulationImpl implements Chronos, Simulation {

	List<MessageReceiver> subscribedTimeElements = new ArrayList<MessageReceiver>();
	List<CarReceiver> subscribedGates = new ArrayList<CarReceiver>();
	Date currentTime = new Date();

	@Override
	public long getCurrentTime() {
		return currentTime.getTime();
	}

	@Override
	public void onSubscribeReceived(TimeSubscribeMessage messageRecieved) {
		MessageReceiver messageRecieverToAdd;
		try {
			messageRecieverToAdd = new MessageReceiver(
					messageRecieved.getAddressSubscribing(),
					messageRecieved.getPortSubscribingOn());
			this.subscribedTimeElements.add(messageRecieverToAdd);
		} catch (IOException e) {
			// do something
		}

	}

	@Override
	public void onCarGenerated(Car newestCar) {

		synchronized (subscribedGates) {
			if (subscribedGates.size() == 0) // If there are no gates then we can't really do anything
				return;

			Random rand = new Random(System.currentTimeMillis());
			int gateToSendTo = rand.nextInt(subscribedGates.size());
			subscribedGates.get(gateToSendTo).sendCar(newestCar);
		}
	}

	@Override
	public void onGateSubscribe(GateSubscribeMessage gateSubscribing) throws IOException {
			subscribedGates.add(new CarReceiver(gateSubscribing.getAddressOfGate(), gateSubscribing.getPort()));
	}

}
