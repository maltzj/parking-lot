package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import messaging.AbstractMessage;

import util.ConnectionHandler;
import util.MessageHandler;
import gates.ConnectionListener;
import gates.MessageListener;

public class Manager implements ConnectionHandler, MessageHandler {

	MessageListener gateListener;
	MessageListener trafficGenListener;
	
	ConnectionListener connectionListener;
	
	int port;
	
	int numberOfTokens;
	int numberOfCars;
	
	public Manager(int numTokens, int numCars, int port) throws IOException{
		this.numberOfTokens = numTokens;
		this.numberOfCars = numCars;
		this.port = port;
		
		connectionListener = new ConnectionListener(this, this.port);
		
	}

	@Override
	public void onConnectionReceived(Socket newConnection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServerError(ServerSocket failedServer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageReceived(AbstractMessage message, Socket socket) {
		//figure out what the hell I need to do here
	}

	@Override
	public void onSocketClosed(Socket socket) {
		
	}
	
}
