package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import messaging.AbstractMessage;
import messaging.GateMessage;
import messaging.GateSubscribeMessage;
import messaging.TokenMessage;
import messaging.TokenRequestMessage;
import messaging.TokenRequireMessage;
import messaging.TokenResponseMessage;
import util.Config;
import util.ConnectionHandler;
import util.ConnectionListener;
import util.HostPort;
import util.MessageHandler;
import util.MessageListener;

public class Manager implements ConnectionHandler, MessageHandler {

	
	private static final int ttl = 2;
	
	MessageListener gateListener;
	MessageListener trafficGenListener;
	
	List<MessageListener> neighbors = new ArrayList<MessageListener>();
	
	ConnectionListener gateConnectionListener;
	ConnectionListener managerConnectionListener;
	
	int gatePort;
	int managerPort;
	
	int numberOfTokens;
	int numberOfCars;
	
	List<TokenRequestMessage> tokenRequests;
	
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
	
		this.tokenRequests = new ArrayList<TokenRequestMessage>();
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
			break;
		}
		case AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE:
		{
			TokenRequestMessage request = (TokenRequestMessage) message;
			HostPort hp = new HostPort(listener.getSocketListeningOn().getInetAddress(), listener.getSocketListeningOn().getLocalPort());
			request.getReceivers().push(hp);
			this.gateListener.writeMessage(request);
			break;
		}
		case AbstractMessage.TYPE_TOKEN_RESPONSE_MESSAGE:
		{
			TokenResponseMessage response = (TokenResponseMessage) message;
			HostPort hp = response.getReceivers().pop();
			
			if(hp != null){
				MessageListener neighbor = this.findNeighbor(hp);
				neighbor.writeMessage(new TokenResponseMessage(response.getNumberOfTokens(), response.getReceivers()));
			}
			else{
				this.numberOfTokens += response.getNumberOfTokens();
				System.out.println("gate number is " + this.gatePort + " and number of tokens is now " + this.numberOfTokens);
				this.gateListener.writeMessage(new TokenMessage(response.getNumberOfTokens()));
			}
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
				System.out.println("Received a car arrival message on " + this.gatePort + " with number of tokens " + this.numberOfTokens);
			}
			else{
				System.out.println(this.numberOfTokens + " is the current number of tokens " + " and we currently say the gate has " + this.numberOfTokens + " tokens on " + this.gatePort);
				System.out.println("ONE OF OUR GATES IS BYZANTINE");
			}
			break;
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE: //pass on any token messages
		{
			if(this.numberOfTokens == -1){
				this.numberOfTokens = ((TokenMessage) message).getNumberOfTokensSent();
				System.out.println("Gate# " + this.gatePort + " received a token number " + this.numberOfTokens);
			}
			break;
		}
		case AbstractMessage.TYPE_TOKEN_REQUIRE_MESSAGE:
		{
			System.out.println("Require message received");
			TokenRequireMessage requires = (TokenRequireMessage) message;
			for(MessageListener neighbor: neighbors){
				neighbor.writeMessage(new TokenRequestMessage(requires.getTokensRequired(), new Stack<HostPort>(), Manager.ttl));
			}
			break;
		}
		case AbstractMessage.TYPE_TOKEN_RESPONSE_MESSAGE:
		{
			TokenResponseMessage response = (TokenResponseMessage) message;
			TokenRequestMessage request = this.getMatchingRequest(response);

			if(request != null){ //if we could find a matching request see if we can send it to its destination.
				this.tokenRequests.remove(request);
				HostPort tokenHostPort = request.getReceivers().pop();
				
				if(response.getNumberOfTokens() > 0){ //if we can facilitate this request pop off the right person and send it to them
					MessageListener tokenDestination = this.findNeighbor(tokenHostPort);
					
					if(tokenDestination != null){
						tokenDestination.writeMessage(new TokenResponseMessage(response.getNumberOfTokens(), request.getReceivers()));
					}
				}
				
				int unfilledTokens = request.getTokensRequested() - response.getNumberOfTokens();
				
				if(unfilledTokens > 0){ //if there are still unfilled tokens forward it along
					request.getReceivers().push(tokenHostPort);
					request.setTokensRequested(unfilledTokens);
					this.forwardTokenRequest(request);
				}
				
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
			this.gateListener.writeMessage(message);
			this.numberOfCars += 1;
			break;
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE:
		{
				TokenMessage tokens = (TokenMessage) message;
				this.numberOfTokens += tokens.getNumberOfTokensSent();
				System.out.println("Gate# " + this.gatePort + " has tokens number " + this.numberOfTokens + 
							" after receiving a token message from traffic and it sent " + tokens.getNumberOfTokensSent());
				this.gateListener.writeMessage(tokens);
				break;
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
			System.out.println("GOT A BAD MESSAGE FROM THE TRAFFIC GEN! " + message.getMessageType());
		}
		}
	}
	
	
	@Override
	public void onSocketClosed(Socket socket) {
		//TODO 
	}
	
	@SuppressWarnings("unchecked")
	private TokenRequestMessage getMatchingRequest(TokenResponseMessage message){
		Stack<HostPort> requestChain = message.getReceivers();
		
		synchronized(this.tokenRequests){
			for(Iterator<TokenRequestMessage> iter = this.tokenRequests.iterator(); iter.hasNext();){ //iterate over all of my requests
				TokenRequestMessage testing = iter.next();
				Stack<HostPort> listStackCopy = (Stack<HostPort>) testing.getReceivers().clone();
				Stack<HostPort> chainCopy = (Stack<HostPort>)requestChain.clone(); //clone the request chain so that we can pop from it

				boolean isCopy = true;
				
				while(!requestChain.isEmpty() && !listStackCopy.isEmpty() ){ //pop off everything and check if the paths are the same
					HostPort chainHost = chainCopy.pop();
					HostPort listHost = listStackCopy.pop();
					
					if(!chainHost.equals(listHost)){
						isCopy = false;
						break;
					}
					
				}
				
				if(isCopy){
					return testing;
				}
				
			}
			return null;
		}
		
	}
	
	private MessageListener findNeighbor(HostPort hp){
		
		for(MessageListener neighbor: neighbors){ //loop over all of our neighbors
			Socket neighborSocket = neighbor.getSocketListeningOn();
			
			if(neighborSocket.getInetAddress().equals(hp.iaddr) && neighborSocket.getLocalPort() == hp.port){
				return neighbor;
			}
			
		}
		
		return null;
	}
	
	private void forwardTokenRequest(TokenRequestMessage message){
		Random rdm = new Random(System.currentTimeMillis());
		synchronized(this.neighbors){
			int indexToSend = rdm.nextInt(this.neighbors.size());
			try {
				this.neighbors.get(indexToSend).writeMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
