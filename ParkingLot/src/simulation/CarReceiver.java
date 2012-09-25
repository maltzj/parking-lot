package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import car.Car;

public class CarReceiver {
	
	private Socket connection;
	
	public CarReceiver(Socket newConnection)
	{
		connection = newConnection;
	}
	
	public CarReceiver(InetAddress location, int port) throws IOException
	{
		connection = new Socket(location, port);
	}
	
	public void sendCar(Car carSending)
	{
		//TODO: IMPLEMENT THIS SHIT 
	}

}
