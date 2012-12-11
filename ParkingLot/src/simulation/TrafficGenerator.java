package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.AbstractMessage;
import messaging.GateMessage;
import util.Config;
import util.Config.ManagerInfo;
import util.ConnectionHandler;
import util.ConnectionListener;
import util.MessageHandler;
import util.MessageListener;

/**
 * This class encompases all of the TrafficGeneration capabilities within the program.  It is also somewhat responsible for the redistribution of tokens.
 * 
 */
public class TrafficGenerator implements ConnectionHandler, MessageHandler
{
	List<MessageListener> carReceivers;
	List<MessageListener> gates;
	
	ConnectionListener gatePort;
	ConnectionListener managerPort;
	
	Queue<ManagerInfo> managers;
	
	public TrafficGenerator(ManagerInfo[] managers) throws Exception
	{
		this.gates = new ArrayList<MessageListener>();
		this.carReceivers = new ArrayList<MessageListener>();
        System.out.println("I am a traffic generator");
        
        Config config = Config.getSharedInstance();
        this.managers = new ConcurrentLinkedQueue<ManagerInfo>();
        for(ManagerInfo m: managers){
        	this.managers.add(m);
        }
        
        this.gatePort = new ConnectionListener(this, config.trafficGenerator.gate.port);
        this.gatePort.setDaemon(false);
        this.gatePort.start();
    }

    public void onConnectionReceived(Socket connection, int receivedOn)
    { 
        
    	/*When a gate subscribes add it to the listen and start listening
    	 *We shouldn't hear any communication from it, we should just send it a manager
    	 */
    	if(receivedOn == gatePort.getPort()){ 
    		onGateSubscribe(connection);
    	}
    	else{
    		onManagerSubscribe(connection);
    	}
    	
        MessageListener msgListener = new MessageListener(this, connection);
        msgListener.setDaemon(false);
        msgListener.start();
    
    }
    
    private void onManagerSubscribe(Socket sock){
    	MessageListener listener = new MessageListener(this, sock);
    	listener.setDaemon(false);
    	this.carReceivers.add(listener);
    }
    
    private void onGateSubscribe(Socket sock){
    	MessageListener listener = new MessageListener(this, sock);
    	listener.setDaemon(false);
    	this.gates.add(listener);
    }

    public void onServerError(ServerSocket failedSocket)
    {
        System.out.println("There is sadness in the world");
    }

	@Override
	public void onMessageReceived(AbstractMessage message, Socket socket) {
		for(int i = 0; i < this.gates.size(); i++){ //check if a gate sent it to us
			if(this.gates.get(i).getSocketListeningOn().equals(socket)){
				try{
					this.onMessageFromGate(message, socket);
				}
				catch(IOException e){
					//TODO worry about that later
				}
				return;
			}
		}
		onMessageFromManager(message);
	}
	
	private void onMessageFromGate(AbstractMessage message, Socket sock) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CONNECT:
		{
			ManagerInfo m = this.managers.poll();
			if(m == null){ //if we don't have any lfet on the queue do nothing
				return;
			}
			AbstractMessage.encodeMessage(sock.getOutputStream(), new GateMessage(m.hostport));
			break;
		}
		default:
		{
			System.out.println("Received an invalid message type from a gate");
			break;
		}
		
		}
	}
	
	private void onMessageFromManager(AbstractMessage message){
		
	}

	@Override
	public void onSocketClosed(Socket socket) {
		for(int i = 0; i < this.gates.size(); i++){ //If it is a gate, remove it from the list of gates
			if(this.gates.get(i).getSocketListeningOn().equals(socket)){
				this.gates.remove(i);
			}
		}
	}
}
