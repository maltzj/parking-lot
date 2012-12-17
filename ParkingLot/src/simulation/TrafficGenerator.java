package simulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.GateMessage;
import messaging.GateSubscribeMessage;
import messaging.ManagerAvailableMessage;
import messaging.TimeMessage;
import messaging.TokenMessage;
import util.Config;
import util.Config.ManagerInfo;
import util.ConnectionHandler;
import util.ConnectionListener;
import util.HostPort;
import util.MessageHandler;
import util.MessageListener;
import car.Car;

/**
 * This class encompases all of the TrafficGeneration capabilities within the program.  It is also somewhat responsible for the redistribution of tokens.
 * 
 */
public class TrafficGenerator extends Thread implements ConnectionHandler, MessageHandler
{
	List<Car> parkingLot = new ArrayList<Car>();
	
	List<MessageListener> carReceivers;
	Map<MessageListener, HostPort> connectedManagers;
	
	List<MessageListener> gates;
	
	ConnectionListener gatePort;
	ConnectionListener managerPort;
	
	Queue<ManagerInfo> availableManagers;
	
	private int currentTime;
	private int simulationLength;
	private Polynomial nextTimePolynomial;
	private Random rdm;
	private static int numGates = 6;
	
	public TrafficGenerator(ManagerInfo[] managers, int simLength, String poly) throws Exception
	{
		this.currentTime = 0;
		this.simulationLength = simLength;
		this.nextTimePolynomial = new Polynomial(poly);
		this.rdm = new Random(System.currentTimeMillis());
		
		this.gates = new ArrayList<MessageListener>();
		this.carReceivers = new ArrayList<MessageListener>();
        System.out.println("I am a traffic generator");
        
        Config config = Config.getSharedInstance();
        
        this.gatePort = new ConnectionListener(this, config.trafficGenerator.gate.port);
        this.gatePort.setDaemon(false);
        this.gatePort.start();
        
        this.availableManagers = new ConcurrentLinkedQueue<ManagerInfo>(); //setup a list of managers
        for(ManagerInfo m: managers){
        	this.availableManagers.add(m);
        }
        
        this.managerPort = new ConnectionListener(this, config.trafficGenerator.manager.port);
        this.managerPort.setDaemon(false);
        this.managerPort.start();
    
        this.connectedManagers = new HashMap<MessageListener, HostPort>();
	}

	public void run()
	{
		System.out.println("Running the run method of traffic generator");
		/**
			You may want to wait for a signal here, instead of start sending a car right away.
			And maybe you want to hard code all your six gates' IP and port number here. That depends your implementation.
		*/
		
		int nextTime;
		int stayTime;
		int nextGate;
		int leavingGate;
		int leavingTime;
		while(currentTime < simulationLength)
		{
			nextTime = (int)nextTime(nextTimePolynomial.evaluate(currentTime));
			stayTime = (int)(Math.abs(rdm.nextGaussian() * ( simulationLength - currentTime )/4 + (simulationLength - currentTime)/2));
			nextGate = (int)(rdm.nextDouble() * ( this.carReceivers.size() + 2));
			
			while(nextGate >= this.carReceivers.size()){ //get a gate that is valid
				nextGate = (int)(rdm.nextDouble() * ( this.carReceivers.size() + 2));
			}
			
			leavingGate = (int)(rdm.nextDouble() * this.carReceivers.size() );
			
			currentTime = currentTime + nextTime;
			leavingTime = currentTime + stayTime;
			
			checkCarLeaving();
			if(currentTime < simulationLength)
			{
				System.out.println("Time: " + currentTime + "\tGate: " + nextGate + "\t\tstayTime: " + stayTime + "\t\tleavingGate: " + leavingGate + "\t\tleavingTime: " + leavingTime);
				Date leavingDate = new Date();
				leavingDate.setTime(leavingTime * 1000);
				try {
					this.carReceivers.get(nextGate).writeMessage(new CarArrivalMessage(getCurrentDate(), leavingDate));
				} catch (IOException e) {
					// TODO WHAT DO WE DO HERE??
					e.printStackTrace();
				}
			}
			notifySubscribers();
			try
  			{
  				sleep(10);  
  			}catch (InterruptedException ie)
  			{
  				System.out.println(ie.getMessage());
  			}
		}
	}

	public double nextTime(double expectedValue)
	{
		return -Math.log(1 - rdm.nextDouble()) / expectedValue;
	}

    public long getCurrentTime()
    {
		return this.currentTime;
    }
    
    /**
     * Get the current time as a Date
     * @return A date representing the current time
     */
    public Date getCurrentDate(){
    	Date d = new Date();
    	d.setTime(this.currentTime * 1000);
    	return d;
    }

    private void checkCarLeaving()
    {
    	Date curr = this.getCurrentDate();
    	synchronized(this.parkingLot){
    		for(Iterator<Car> iter = this.parkingLot.iterator(); iter.hasNext();){ //iterate over parking lot
    			Car c = iter.next();
    			if(c.getTimeDeparts().compareTo(curr) <= 0){ //if a car is past its leaving time send it
    				int gate = rdm.nextInt(this.carReceivers.size());
    				System.out.println("sending a token to gate " + gate);
    				try {
						this.carReceivers.get(gate).writeMessage(new TokenMessage(1));
					} catch (IOException e) {
						// TODO DEAL WITH THAT
					}
    				iter.remove();
    				
    			}
    		}	
    	}
    }
    
    private void notifySubscribers()
    {
    	synchronized(this){
    		for(MessageListener listener: this.carReceivers){
    			try {
    				listener.writeMessage(new TimeMessage(this.getCurrentDate()));
    			} catch (IOException e) {
    				//TODO Auto-generated catch block
    			}
    		}
    	}
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
    	
    
    }
    
    private void onManagerSubscribe(Socket sock){
    	MessageListener listener = new MessageListener(this, sock);
    	listener.setDaemon(false);
    	this.carReceivers.add(listener);
    	listener.start();
    	if(this.carReceivers.size() == numGates){ //if we have the required number of gates
    		this.start();
    	}
    
    }
    
    private void onGateSubscribe(Socket sock){
    	MessageListener listener = new MessageListener(this, sock);
    	listener.setDaemon(false);
    	this.gates.add(listener);
    	listener.start();
    	
    }

    public void onServerError(ServerSocket failedSocket)
    {
        System.out.println("There is sadness in the world");
    }

	@Override
	public void onMessageReceived(AbstractMessage message, MessageListener listener) {
		
		for(int i = 0; i < this.gates.size(); i++){ //check if a gate sent it to us
			if(this.gates.get(i).equals(listener)){
				try{
					this.onMessageFromGate(message, listener);
				}
				catch(IOException e){
					//TODO worry about that later
				}
				return;
			}
		}
		
		try { //if it wasn't a message from a gate it is from a manager
			onMessageFromManager(message, listener);
		} catch (IOException e) {
			//TODO Do it!
		} 
	}
	
	private void onMessageFromGate(AbstractMessage message, MessageListener listener) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CONNECT:
		{
			ManagerInfo m = this.availableManagers.poll();
			if(m == null){ //if we don't have any lfet on the queue do nothing
				return;
			}
			listener.writeMessage(new GateMessage(m.hostport));
			break;
		}
		default:
		{
			System.out.println("Received an invalid message type from a gate");
			break;
		}
		
		}
	}
	
	private void onMessageFromManager(AbstractMessage message, MessageListener listener) throws IOException{
		switch(message.getMessageType()){
		case AbstractMessage.TYPE_CAR_ARRIVAL: //if one of the managers sent us a car arrival, add it to the parking lot
		{
			synchronized(this.parkingLot){
				CarArrivalMessage arrival = (CarArrivalMessage) message;
				this.parkingLot.add(new Car(arrival.getCarSentTime(), arrival.getCarReturnTime()));
			}
			break;
		}
		case AbstractMessage.TYPE_GATE_SUBSCRIBE: //treat these as ways to broadcast manager ports
		{
			GateSubscribeMessage gateSub = (GateSubscribeMessage) message;
			synchronized (this.connectedManagers) {
				Set<MessageListener> connectedManagersSet = this.connectedManagers.keySet();
				Iterator<MessageListener> managerIter = connectedManagersSet.iterator();
				
				if(connectedManagers.size() <= 3){ //if there are less than three connected managers send them all
					
					while(managerIter.hasNext()){
						HostPort toSend = this.connectedManagers.get(managerIter.next());
						listener.writeMessage(new GateMessage(toSend));
					}
				}
				
				else{ //otherwise just generate three random numbers
					List<MessageListener> managersAsList =  Arrays.asList(connectedManagersSet.toArray(new MessageListener[0])); //give me a list of managers
					Collections.shuffle(managersAsList);
					for(int i = 0; i < 3; i++){
						listener.writeMessage(new GateMessage(this.connectedManagers.get(managersAsList.get(i))));
					}
				}
				
			}
			
			
			this.connectedManagers.put(listener, new HostPort(gateSub.getAddressOfGate(), gateSub.getPort()));
			break;
		}
		case AbstractMessage.TYPE_MANAGER_AVAILABLE:
		{
			synchronized(this){
				ManagerAvailableMessage available = (ManagerAvailableMessage) message;
				this.connectedManagers.remove(listener);
				this.carReceivers.remove(listener);
				listener.die = true;
				listener.getSocketListeningOn().close();
				ManagerInfo info = Config.getSharedInstance().new ManagerInfo();
				info.hostport = new HostPort(available.getAddr(), available.getGatePort());
				info.managerPort = new HostPort(available.getAddr(), available.getManagerPort());
				info.money = 0;
				info.tokens = 0;
				this.availableManagers.add(info);
			}
		}
		}
	}

	@Override
	public void onSocketClosed(Socket socket) {
		for(int i = 0; i < this.gates.size(); i++){ //If it is a gate, remove it from the list of gates
			if(this.gates.get(i).getSocketListeningOn().equals(socket)){
				this.gates.remove(i);
			}
		}
	}
	
	
	/**You don't need to change the rest of code*/
	private class Polynomial
	{
		private ArrayList<Integer> exponent;
		private ArrayList<Double> coefficient; 		
		public Polynomial(String str)
		{
			createPolynomial(str);
		}

		private void createPolynomial(String poly)
		{
			int i;
			int exp;
			double coeff;
	
			i = poly.indexOf(',');
			exp = Integer.parseInt(poly.substring(0,i));
			poly = poly.substring(i+1);
			i = poly.indexOf(',');
			if(i == -1)
			{
				coeff = Double.parseDouble(poly);
				poly = "";
			}
			else
			{
				coeff = Double.parseDouble(poly.substring(0,i));
				poly = poly.substring(i+1);
			}

			exponent = new ArrayList<Integer>();
			coefficient = new ArrayList<Double>();
			exponent.add(exp);
			coefficient.add(coeff);

			while(!poly.equals(""))
			{
				i = poly.indexOf(',');
				exp = Integer.parseInt(poly.substring(0,i));
				poly = poly.substring(i+1);
				i = poly.indexOf(',');
				if(i == -1)
				{
					coeff = Double.parseDouble(poly);
					poly = "";
				}
				else
				{
					coeff = Double.parseDouble(poly.substring(0,i));
					poly = poly.substring(i+1);
				}
				exponent.add(exp);
				coefficient.add(coeff);
			}	
		}	
		
		public double evaluate(double x)
		{
			double sum = 0;
			for(int i = 0; i < exponent.size(); i++)
			{
				sum = sum + coefficient.get(i) * Math.pow(x, exponent.get(i));
			}
			return sum;
		}
	}

}
