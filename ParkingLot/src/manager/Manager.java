package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import messaging.AbstractMessage;
import messaging.GateMessage;
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
	
	List<MessageListener> neighbors = new ArrayList<MessageListener>();
	
	ConnectionListener gateConnectionListener;
	ConnectionListener managerConnectionListener;
	
	int gatePort;
	int managerPort;
	
	int numberOfTokens;
	int numberOfCars;
	
	/**
	 * 
	 * @param numTokens
	 * @param numCars
	 * @param port
	 * @throws IOException
	 */
	public Manager(int numTokens, int numCars, int gatePort, int managerPort) throws IOException{
		this.numberOfTokens = numTokens;
		this.numberOfCars = numCars;
		this.gatePort = gatePort;
		
		gateConnectionListener = new ConnectionListener(this, this.gatePort);
		gateConnectionListener.start();
	
		this.managerPort = managerPort;
		this.managerConnectionListener = new ConnectionListener(this, this.managerPort);
		this.managerConnectionListener.start();
	}
	
	/**
	 * In this system, why should a manager be started with a given number of tokens?  Shouldn't that be specified by its gate?
	 * @param port
	 * @throws IOException 
	 */
	public Manager(int gatePort, int managerPort) throws IOException{
		this.numberOfCars = 0;
		this.numberOfTokens = -1;
		this.gatePort = gatePort;
	
		gateConnectionListener = new ConnectionListener(this, this.gatePort);
		gateConnectionListener.start();
	}
	

	@Override
	public void onConnectionReceived(Socket newConnection, int receivedOn) {
		
		if(receivedOn == gatePort){  //if a gate connected
			if(this.gateListener == null){
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
					trafficGenListener.writeMessage(new GateSubscribeMessage(this.gateConnectionListener.getServer().getInetAddress(), this.managerPort));
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		else{  //otherwise we received this from another manager
			System.out.println("Manager #"+ this.managerPort + " Got a connection on our manager port!!");
			MessageListener listener = new MessageListener(this, newConnection);
			listener.setDaemon(false);
			listener.start();
		}
		
	}

	@Override
	public void onServerError(ServerSocket failedServer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessageReceived(AbstractMessage message, MessageListener listener) {
		synchronized(this){
			if(listener.equals(this.gateListener)){
				try {
					this.onMessageFromGate(message);
				} catch (IOException e) {
					//TODO figure this out
				}
			}
			else if(listener.equals(this.trafficGenListener)){ //if we got a message from traffic
				try{
					onMessageFromTraffic(message);		
				}
				catch(IOException e){
					//TODO figure this out
				}
			}
			else{ //otherwise it is message from a manager
				try {
					onMessageFromManager(message, listener);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void onMessageFromManager(AbstractMessage message, MessageListener listener) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_TOKEN_MESSAGE:
		{
			TokenMessage tokenMessage = (TokenMessage) message;
			this.numberOfTokens += tokenMessage.getNumberOfTokensSent();
			this.gateListener.writeMessage(tokenMessage);
		}
		case AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE:
		{
			//TODO figure out what to do here
		}
		}
	}

	private void onMessageFromGate(AbstractMessage message) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //pass on any cars to our gate
		{
			if(this.numberOfTokens > 0){
				this.trafficGenListener.writeMessage(message);
				this.numberOfCars--;
				this.numberOfTokens--;
			}
			break;
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE: //pass on any token messages
		{
			if(this.numberOfTokens == -1){
				this.numberOfTokens = ((TokenMessage) message).getNumberOfTokensSent();
			}
			break;
		}
		default:
		{
			
			System.out.println("GOT A BAD MESSAGE FROM A GATE " + message.getMessageType());
		}
		}
	}
	
	private void onMessageFromTraffic(AbstractMessage message) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //this means a car is being sent to the parking lot
		{
			System.out.println("A manager received a car from traffic ");
			this.gateListener.writeMessage(message);
			this.numberOfCars += 1;
			break;
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE:
		{
			if(this.numberOfTokens == -1){ //if they havent sent us how many tokens they have, set that to their value
				this.numberOfTokens+= ((TokenMessage) message).getNumberOfTokensSent();
				this.gateListener.writeMessage(message);
			}
		}
		case AbstractMessage.TYPE_TIME_MESSAGE: //just pass on time messages
		{
			this.gateListener.writeMessage(message);
			break;
		}
		case AbstractMessage.TYPE_GATE: //use this to note other managers
		{
			GateMessage gateMess = (GateMessage) message;
			Socket sock = new Socket(gateMess.getAddr(), gateMess.getPort());
			MessageListener neighbor = new MessageListener(this, sock);
			neighbor.start();
			
			this.neighbors.add(neighbor);
			break;
		}
		default:
		{
			System.out.println("Message type is " + message.getMessageType());
			System.out.println("GOT A BAD MESSAGE FROM THE TRAFFIC GEN!");
		}
		}
	}
	
	@Override
	public void onSocketClosed(Socket socket) {
		//TODO 
	}
	
}
