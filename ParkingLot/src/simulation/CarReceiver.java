package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import util.MessageReceiver;

import car.Car;

public class CarReceiver extends MessageReceiver {
	
	public CarReceiver(Socket newConnection) throws IOException
	{
		super(newConnection.getInetAddress(), newConnection.getPort());
	}
	
	public CarReceiver(InetAddress location, int port) throws IOException
	{ 
		super(location, port);
	}
	
	public void sendCar(Car carSending)
	{
		//TODO: IMPLEMENT THIS SHIT 
	}

}
