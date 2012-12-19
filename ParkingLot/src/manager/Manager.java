package manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import messaging.AbstractMessage;
import messaging.GateMessage;
import messaging.GateSubscribeMessage;
import messaging.ManagerAvailableMessage;
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

/**
 * 
 * The Manager is our source of truth within this system.  Managers intially start and ar listening for connections from gates
 * Once a gate is connected to the manager, it queries the gate for the number of tokens it has, once that is done.
 * Once that is completed, it connects to the traffic generator and starts receiving cars and passing them on to its paired gate
 * This layer removes the possibility of byzantine gates as the Manager keeps track of any necessary data and alerts the system when its gate is byzantine 
 *
 */
public class Manager implements ConnectionHandler, MessageHandler {

	private static final int ttl = 3;
	
	MessageListener gateListener;
	MessageListener trafficGenListener;
	
	List<MessageListener> neighbors = new ArrayList<MessageListener>();
	
	ConnectionListener gateConnectionListener;
	ConnectionListener managerConnectionListener;
	
	int gatePort;
	int managerPort;
	
	int numberOfTokens;
	int numberOfCars;
	
	Vector<TokenRequestMessage> tokenRequests;
	
	/**
	 * Initializes the Manager to have all the necessary information for it to run
	 * @param numTokens The number of tokens that it should start with
	 * @param numCars The number of cars that that are intiially with gates
	 * @param gatePort The port that this will use to accept connections from gates
	 * @param managerPort The port that this will use to accept connections from managers
	 * @throws IOException If servers cannot be started for one reason or another
	 */
	public Manager(int numTokens, int numCars, int gatePort, int managerPort) throws IOException{
		this.numberOfTokens = numTokens;
		this.numberOfCars = numCars;
		this.gatePort = gatePort;
		
		gateConnectionListener = new ConnectionListener(this, this.gatePort);
		gateConnectionListener.setDaemon(true);
		gateConnectionListener.start();
		
	
		this.managerPort = managerPort;
		this.managerConnectionListener = new ConnectionListener(this, this.managerPort);
		this.managerConnectionListener.setDaemon(true);
		this.managerConnectionListener.start();
	
		this.tokenRequests = new Vector<TokenRequestMessage>();
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
		this.managerPort = managerPort;
	
		gateConnectionListener = new ConnectionListener(this, this.gatePort);
		gateConnectionListener.start();
		
		managerConnectionListener = new ConnectionListener(this, this.managerPort);
		managerConnectionListener.start();
	}
	

	@Override
	public void onConnectionReceived(Socket newConnection, int receivedOn) {
		synchronized(this){
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
					trafficGenListener.start();
					try { //become subscribed and write a gate subscribe message
						trafficGenListener.writeMessage(new GateSubscribeMessage(this.managerConnectionListener.getServer().getInetAddress(), this.managerPort));
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
			}
			else{  //otherwise we received this from another manager
				MessageListener listener = new MessageListener(this, newConnection);
				listener.setDaemon(false);
				listener.start();
				this.neighbors.add(listener);
			}
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
	
	@SuppressWarnings("unchecked")
	private void onMessageFromManager(AbstractMessage message, MessageListener listener) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_TOKEN_MESSAGE:
		{
			if(this.gateListener != null){
				TokenMessage tokenMessage = (TokenMessage) message;
				this.numberOfTokens += tokenMessage.getNumberOfTokensSent();
				this.gateListener.writeMessage(tokenMessage);
			}
			break;
		}
		case AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE:
		{
			synchronized(this.tokenRequests){
				if(this.gateListener != null){
					TokenRequestMessage request = (TokenRequestMessage) message;
					HostPort hp = new HostPort(listener.getSocketListeningOn().getLocalAddress(), listener.getSocketListeningOn().getPort());
					request.getReceivers().push(hp);
				
					TokenRequestMessage copy = new TokenRequestMessage(request.getTokensRequested(), (Stack<HostPort>) request.getReceivers().clone(), request.getTtl());

					this.tokenRequests.add(copy);

					this.gateListener.writeMessage(request);
				}
				else{
					TokenRequestMessage request = (TokenRequestMessage) message;
					if(request.getTtl() > 0){
						request.getReceivers().push(new HostPort(listener.getSocketListeningOn().getLocalAddress(), listener.getSocketListeningOn().getPort()));
						request.setTtl(request.getTtl() - 1);
						this.forwardTokenRequest(request);
					}
				}
			}
			
			break;
		}
		case AbstractMessage.TYPE_TOKEN_RESPONSE_MESSAGE:
		{
			TokenResponseMessage response = (TokenResponseMessage) message;
			
			if(response.getReceivers().size() != 0){ //if there are still elements to send, it along
				HostPort hp = response.getReceivers().pop();
				MessageListener neighbor = this.findNeighbor(hp);
				System.out.println("hp that we are checking is " + hp);
				neighbor.writeMessage(new TokenResponseMessage(response.getNumberOfTokens(), response.getReceivers()));
			
			}
			else{ //otherwise it is due for us
				if(this.gateListener != null){
					this.numberOfTokens += response.getNumberOfTokens();
					this.gateListener.writeMessage(response);
				}
			}
			break;
		}
		}
	}

	/**
	 * Specifies how a Manager should handle messages from its paired gate
	 * @param message The message which was received
	 * @throws IOException If an IOException occurs
	 */
	private void onMessageFromGate(AbstractMessage message) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //pass on any cars to our gate
		{
			if(this.numberOfTokens > 0){
				this.trafficGenListener.writeMessage(message);
				this.numberOfCars--;
				this.numberOfTokens--;
				}
			else{
				System.out.println(this.numberOfTokens + " is the current number of tokens " + " and we currently say the gate has " + this.numberOfTokens + " tokens on " + this.gatePort);
				System.out.println(" ONE OF OUR GATES IS BYZANTINE and tried to send a car when it doesn't have enough tokens ");
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
		case AbstractMessage.TYPE_TOKEN_REQUIRE_MESSAGE:
		{
			TokenRequireMessage requires = (TokenRequireMessage) message;
	
			System.out.println("Require message received for " + requires.getTokensRequired() + " tokens on manager " + this.gatePort);
			this.forwardTokenRequest(new TokenRequestMessage(requires.getTokensRequired(), new Stack<HostPort>(), Manager.ttl));
			
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
					this.gateListener.writeMessage(new TokenResponseMessage(response.getNumberOfTokens() * -1, new Stack<HostPort>()));
					this.numberOfTokens -= response.getNumberOfTokens();
				}
				
				int unfilledTokens = request.getTokensRequested() - response.getNumberOfTokens();
				
				if(unfilledTokens > 0 && request.getTtl() > 0){ //if there are still unfilled tokens forward it along
					request.getReceivers().push(tokenHostPort);
					request.setTtl(request.getTtl() - 1);
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
	
	/**
	 * Specifies how to deal with a message from the traffic simulation
	 * @param message The message that was received from the traffic simulator
	 * @throws IOException If an IOException occurs
	 */
	private void onMessageFromTraffic(AbstractMessage message) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //this means a car is being sent to the parking lot
		{
			if(this.gateListener != null){
				this.gateListener.writeMessage(message);
				this.numberOfCars += 1;
				break;
			}
		}
		case AbstractMessage.TYPE_TOKEN_MESSAGE:
		{
			if(this.gateListener != null){
				TokenMessage tokens = (TokenMessage) message;
				this.numberOfTokens += tokens.getNumberOfTokensSent();
				this.gateListener.writeMessage(tokens);
				break;
			}
		}
		case AbstractMessage.TYPE_TIME_MESSAGE: //just pass on time messages
		{
			if(this.gateListener != null){
				this.gateListener.writeMessage(message);
			}
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
		case AbstractMessage.TYPE_DONE:
		{
			if(this.gateListener != null){
				closeShop();
				this.gateListener.writeMessage(message);
			}
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
		synchronized(this){
			if( gateListener != null && socket.equals(gateListener.getSocketListeningOn())){//
				try {
					this.onGateDisconnect();
				} catch (IOException e) {
					// TODO don't worry about it
				}
			}
			else if(trafficGenListener != null && socket.equals(trafficGenListener.getSocketListeningOn())){
				this.onTrafficDisconnect();
			}
			else{
				
				for(Iterator<MessageListener> iter = neighbors.iterator(); iter.hasNext();){
					MessageListener neighbor = iter.next();
					try {
						closeManager(neighbor);
					} catch (IOException e) {
						// TODO Do nothing now
					}
					iter.remove();
				}
			
			}
		}
	}
	
	/**
	 * Closes up the manager, closing all the managers and closing down the servers
	 * @throws IOException
	 */
	private void closeShop() throws IOException{
		
		for(MessageListener neighbor: this.neighbors){
			closeManager(neighbor);
		}
		
		this.managerConnectionListener.getServer().close();
		this.gateConnectionListener.getServer().close();
		this.trafficGenListener.close();
	}
	
	/**
	 * Closes a manager and performs any necessary cleanup
	 * @param manager, The manager which will be closed
	 * @throws IOException
	 */
	private void closeManager(MessageListener manager) throws IOException{
		manager.close();
	}
	
	/**
	 * Gets the TokenRequestMessage which matches the response we just received
	 * A request is matching if it has the same path and number of tokens as the response
	 * This is useful when a Gate sends you a TokenResponse and you need to find which one they responded for
	 * @param message The TokenResponseMessage which was we are matching
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TokenRequestMessage getMatchingRequest(TokenResponseMessage message){
		Stack<HostPort> requestChain = message.getReceivers();
		
		synchronized(this.tokenRequests){
			for(Iterator<TokenRequestMessage> iter = this.tokenRequests.iterator(); iter.hasNext();){ //iterate over all of my requests
				TokenRequestMessage testing = iter.next();
				Stack<HostPort> listStackCopy = (Stack<HostPort>) testing.getReceivers().clone();				
				Stack<HostPort> chainCopy = (Stack<HostPort>)requestChain.clone(); //clone the request chain so that we can pop from it

				boolean isCopy = true;
				
				int requestSize = requestChain.size();
				int listSize = listStackCopy.size();
				
				while(!requestChain.isEmpty() && !listStackCopy.isEmpty() ){ //pop off everything and check if the paths are the same
					HostPort chainHost = chainCopy.pop();
					HostPort listHost = listStackCopy.pop();
					
					if(!chainHost.equals(listHost)){
						isCopy = false;
						break;
					}
					
				}
				
				if(isCopy && listSize > 0 && requestSize > 0){  //ensure that we didn't just skip everything
					return testing;
				}
				
			}
			return null;
		}
		
	}
	
	/**
	 * Finds the neighbor who is listening at a given HostPort
	 * @param hp The HostPort that we are trying to find a match for
	 * @return
	 */
	private MessageListener findNeighbor(HostPort hp){
		
		for(MessageListener neighbor: neighbors){ //loop over all of our neighbors and check if their ports match
			Socket neighborSocket = neighbor.getSocketListeningOn();		
			
			if(neighborSocket.getPort() == hp.port){
				return neighbor;
			}
			
		}
		
		return null;
	}
	
	/**
	 * Forwards a token request message to neighbors.  To accomplish this we evenly distribute the token request among our neighbors
	 * @param message The token request that is getting sent along
	 * @throws IOException If an IOException occurs
	 */
	private void forwardTokenRequest(TokenRequestMessage message) throws IOException{
		
		if(this.neighbors.size() == 0){
			return;
		}
		
		int tokensPerNeighbor = message.getTokensRequested() /this.neighbors.size();
		int leftoverTokens = message.getTokensRequested() - tokensPerNeighbor;
		synchronized(this.neighbors){
			
			for(MessageListener neighbor: this.neighbors){
				int tokensToSend = tokensPerNeighbor;
				
				if(leftoverTokens > 0){
					tokensToSend++;
					leftoverTokens--;
				}
				neighbor.writeMessage(new TokenRequestMessage(tokensToSend, message.getReceivers(), message.getTtl()));
			}
		}
	}
	
	/**
	 * Specifies what to do when a gate disconnects
	 * @throws IOException
	 */
	private void onGateDisconnect() throws IOException{;
		//disconnect the gate
		this.gateListener.die = true;
		this.gateListener.close();
		this.gateListener = null;
		this.numberOfCars = 0;
		this.numberOfTokens = -1;
		
		distributeTokens();
	
		this.trafficGenListener.writeMessage(new ManagerAvailableMessage(this.gateConnectionListener.getServer().getInetAddress(), gatePort, managerPort));
	}
	
	/**
	 * Distributes the remaining tokens amongst a managers neighbors
	 * @throws IOException
	 */
	private void distributeTokens() throws IOException{
		
		if(this.neighbors.size() == 0){
			return;
		}
		
		//send our tokens to our neighbors
		synchronized(this.neighbors){
			
			int tokensPerNeighbor = this.numberOfTokens / this.neighbors.size();
			int leftoverTokens = this.numberOfTokens - tokensPerNeighbor;

			for(MessageListener neighbor: this.neighbors){
				int tokensToSend = tokensPerNeighbor;

				//make sure we send any leftover tokens
				if(leftoverTokens > 0){
					tokensToSend++;
					leftoverTokens--;
				}

				neighbor.writeMessage(new TokenMessage(tokensToSend));
			}
		}
	}
	
	/**
	 * Specifies what to do when the traffic generator disconnects
	 */
	private void onTrafficDisconnect(){
		this.trafficGenListener.die = true;
		this.trafficGenListener = null;
		
	}
	
}
