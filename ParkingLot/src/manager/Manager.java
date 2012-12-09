package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import messaging.AbstractMessage;
import messaging.GateMessage;
import messaging.GateSubscribeMessage;

import util.Config;
import util.ConnectionHandler;
import util.HostPort;
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
		connectionListener.start();
	}

	@Override
	public void onConnectionReceived(Socket newConnection) {
		System.out.println("Got a connection");
		gateListener = new MessageListener(this, newConnection);
		gateListener.setDaemon(true);
		gateListener.start();
		
		Config c = new Config();
		Socket trafficSock = null;
		try {
			trafficSock = new Socket(c.trafficGenerator.iaddr, c.trafficGenerator.port);
		} catch (IOException e) {
			//TODO cry sad sad tears
		}
		if(trafficSock != null){
			trafficGenListener.setDaemon(true);
			trafficGenListener = new MessageListener(this, trafficSock);
			trafficGenListener.start();
			try {
				trafficGenListener.writeMessage(new GateSubscribeMessage(this.connectionListener.getServer().getInetAddress(), this.port));
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		else{
			//TODO close all the things!?!?!
		}
		
	}

	@Override
	public void onServerError(ServerSocket failedServer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageReceived(AbstractMessage message, Socket socket) {
		//TODO figure out what to do
	}

	@Override
	public void onSocketClosed(Socket socket) {
		//TODO 
	}
	
}
