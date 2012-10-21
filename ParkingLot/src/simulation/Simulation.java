package simulation;

import java.io.IOException;

import messaging.GateSubscribeMessage;
import car.Car;

public interface Simulation {

	public void onCarGenerated(Car newestCar);
	public void onGateSubscribe(GateSubscribeMessage gateSubscribing) throws IOException;
	
}
