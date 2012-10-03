package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import util.MessageReceiver;

import car.Car;

public class CarReceiver extends MessageReceiver {
	
	private Socket connection;
	
	public CarReceiver(Socket newConnection)
	{
		super(newConnection.getInetAddress(), newConnection.getPort());
		connection = newConnection;
	}
	
	public CarReceiver(InetAddress location, int port) throws IOException
	{ 
		super(location, port);
		connection = new Socket(location, port);
	}
	
	public void sendCar(Car carSending)
	{
		//TODO: IMPLEMENT THIS SHIT 
	}

}
