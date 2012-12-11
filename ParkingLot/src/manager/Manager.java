package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import messaging.AbstractMessage;
import messaging.GateSubscribeMessage;
import messaging.TokenMessage;
import util.Config;
import util.ConnectionHandler;
import util.ConnectionListener;
import util.MessageHandler;
import util.MessageListener;

public class Manager implements ConnectionHandler, MessageHandler {

	
	MessageListener gateListener;
	MessageListener trafficGenListener;
	
	List<MessageListener> managers = new ArrayList<MessageListener>();
	
	ConnectionListener connectionListener;
	
	int port;
	
	int numberOfTokens;
	int numberOfCars;
	
	/**
	 * 
	 * @param numTokens
	 * @param numCars
	 * @param port
	 * @throws IOException
	 */
	public Manager(int numTokens, int numCars, int port) throws IOException{
		this.numberOfTokens = numTokens;
		this.numberOfCars = numCars;
		this.port = port;
		
		connectionListener = new ConnectionListener(this, this.port);
		connectionListener.start();
	}
	
	/**
	 * In this system, why should a manager be started with a given number of tokens?  Shouldn't that be specified by its gate?
	 * @param port
	 */
	public Manager(int port){
		this.numberOfCars = 0;
		this.numberOfTokens = -1;
		this.port = port;
	}

	@Override
	public void onConnectionReceived(Socket newConnection, int receivedOn) {
		System.out.println("Got a connection");
		gateListener = new MessageListener(this, newConnection);
		gateListener.setDaemon(true);
		gateListener.start();
		
		Config c = Config.getSharedInstance();
		Socket trafficSock = null;
		try { //connect this manager to the traffic generator
			trafficSock = new Socket(c.trafficGenerator.manager.iaddr, c.trafficGenerator.manager.port);
		} catch (IOException e) {
			return;
		}
		trafficGenListener = new MessageListener(this, trafficSock);
		trafficGenListener.setDaemon(true);
		trafficGenListener = new MessageListener(this, trafficSock);
		trafficGenListener.start();
		try { //become subscribed and write a gate subscribe message
			trafficGenListener.writeMessage(new GateSubscribeMessage(this.connectionListener.getServer().getInetAddress(), this.port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}		
	}

	@Override
	public void onServerError(ServerSocket failedServer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessageReceived(AbstractMessage message, Socket socket) {
		synchronized(this){
			if(socket.equals(this.gateListener.getSocketListeningOn())){
				try {
					this.onMessageFromGate(message);
				} catch (IOException e) {
					//TODO figure this out
				}
			}
			else{
				try{
					onMessageFromTraffic(message);		
				}
				catch(IOException e){
					//TODO figure this out
				}
			}
		}
	}

	private void onMessageFromGate(AbstractMessage message) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //pass on any cars to our gate
		{
			this.trafficGenListener.writeMessage(message);
			this.numberOfCars += 1;
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE: //pass on any token messages
		{
			if(this.numberOfTokens == -1){
				this.numberOfTokens = ((TokenMessage) message).getNumberOfTokensSent();
			}
		}
		case AbstractMessage.TYPE_TIME_MESSAGE: //just pass on time messages
		{
			this.gateListener.writeMessage(message);
		}
		}
	}
	
	private void onMessageFromTraffic(AbstractMessage message) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //this means a car is being sent to the parking lot
		{
			this.gateListener.writeMessage(message);
			this.numberOfCars -= 1;
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE:
		{
			if(this.numberOfTokens == -1){ //if they havent sent us how many tokens they have, set that to their value
				this.numberOfTokens+= ((TokenMessage) message).getNumberOfTokensSent();
				this.gateListener.writeMessage(message);
			}
		}
		}
	}
	
	@Override
	public void onSocketClosed(Socket socket) {
		//TODO 
	}
	
}
