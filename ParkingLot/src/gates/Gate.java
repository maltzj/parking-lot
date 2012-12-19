package gates;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.GateDoneMessage;
import messaging.GateMessage;
import messaging.SimpleMessage;
import messaging.TimeMessage;
import messaging.TokenMessage;
import messaging.TokenRequestMessage;
import messaging.TokenRequireMessage;
import messaging.TokenResponseMessage;
import tokentrading.GlobalTokenTrader;
import tokentrading.NoTokenTrader;
import tokentrading.ProfitTokenTrader;
import tokentrading.TokenTrader;
import util.Config;
import util.MessageHandler;
import util.MessageListener;
import car.Car;

/**
 * A concrete implementation of the Gate interface.  This is responsible for handling all the responsibilities of a Gate
 * This includes trading tokens, listening for messages, and allowing cars into the parking lot.
 *
 */
public class Gate implements MessageHandler{

	public static final int NO_TRADING_POLICY = 1;
	public static final int GLOBAL_TRADING_POLICY = 2;
	public static final int PROFIT_TRADING_POLICY = 3;

	public static boolean stillRunning = true;

	ConcurrentLinkedQueue<CarWrapper> waitingCars = new ConcurrentLinkedQueue<CarWrapper>();
	long amountOfTimeToWait; //Seconds

	MessageListener simulationMessageListener;
	MessageListener manager;

	int numberOfTokens;
	int amountOfMoney;
	int moneyPerCarPassed;
	int costPerToken;

	InetAddress addrListeningOn;
	int portListeningOn;
	private int realPort;

	int numberOfSadnessCars = 0;
	int totalCarWait = 0;
	int numberOfCarsLetThrough = 0;

	TokenTrader trader;

	/**
	 * Initializes a gate with all the information necessary to get running
	 * @param timeToWait, The amount of time a gate should allow cars to wait in the queue before kicking them out
	 * @param tokensToStartWith, The number of tokens to start with.
	 * @param moneyToStartWith, The amount of money to start with.
	 * @param tokenPolicy, The token trading policy to use for this gate.
	 * @param addr, The IPAddress to initialize this Gate at
	 * @param port, The port this gate will be listening on.
	 * @throws Exception
	 */
	public Gate(long timeToWait, int tokensToStartWith, int moneyToStartWith, 
			InetAddress addr, int port, int moneyPerCarPassed, int tradingPolicy, int costOfTokens) throws Exception
			{

		Config c = Config.getSharedInstance();

		this.addrListeningOn = addr;
		this.portListeningOn = port;

		this.amountOfTimeToWait = timeToWait*1000; //dates deal with milliseconds, we want to expose all APIs as seconds
		this.amountOfMoney = moneyToStartWith;
		this.numberOfTokens = tokensToStartWith;
		this.costPerToken = costOfTokens;
		
		this.moneyPerCarPassed = moneyPerCarPassed;

		/*Connect to the simulation*/
		Socket s = new Socket(c.trafficGenerator.gate.iaddr, c.trafficGenerator.gate.port);
		simulationMessageListener = new MessageListener(this, s);
		simulationMessageListener.setDaemon(true);
		simulationMessageListener.start();
		simulationMessageListener.writeMessage(new SimpleMessage(AbstractMessage.TYPE_CONNECT));

		realPort = s.getLocalPort();
		
		switch(tradingPolicy){
		case Gate.NO_TRADING_POLICY:
		{
			this.trader = new NoTokenTrader(this);
			break;
		}
		case Gate.GLOBAL_TRADING_POLICY:
		{
			this.trader = new GlobalTokenTrader(this);
			break;
		}
		case Gate.PROFIT_TRADING_POLICY:
		{
			this.trader = new ProfitTokenTrader(this);
			break;
		}

		}

	}

	/**
	 * Specifies what to do when a car arrives at the Gate.
	 * First the car is added to the queue of waiting cars,
	 * Then, while there are still tokens left cars are let in
	 * @param arrival The information about the car that just arrived
	 */
	public void onCarArrived(CarArrivalMessage arrival) {
		
		Car carToQueue = new Car(arrival.getCarSentTime(), arrival.getCarReturnTime());
		
		//Add Car to queue
		long timeArrived = arrival.getCarSentTime().getTime();
		long leavingTime = timeArrived + this.amountOfTimeToWait;

		Date timeToLeave = new Date();
		timeToLeave.setTime(leavingTime);
		
		CarWrapper carWrapper = new CarWrapper(carToQueue, timeToLeave);
		waitingCars.add(carWrapper);

		while(this.numberOfTokens > 0 && this.waitingCars.size() > 0){
			CarWrapper c = this.waitingCars.poll();
			this.sendCarToParkingLot(c);
		}
		
		this.checkTokenStatus();
		
	}


	/**
	 * Updates the state of the Gate based on a new published canonical time of the system
	 * On time updates, we loop over our list of waiting cars.  
	 * If any are due to walk away from the gate because they have waited too long we let them go
	 * @param messageFromChronos The new time published by Chronos
	 */
	public void onTimeUpdate(TimeMessage messageFromChronos){

		Date newTime = messageFromChronos.getNewTime();
		Calendar timeToCheckAgainst = Calendar.getInstance();
		timeToCheckAgainst.setTime(newTime);

		boolean allowedCar = false;
		
		for(Iterator<CarWrapper> iter = this.waitingCars.iterator(); iter.hasNext(); )
		{
			CarWrapper currentCar = iter.next();
			Calendar carLeaveQueueTime = Calendar.getInstance();
			carLeaveQueueTime.setTime(currentCar.timeToLeaveQueue);
			
			//Car waited too long and left
			if(timeToCheckAgainst.after(carLeaveQueueTime)) {
				numberOfSadnessCars++;
				iter.remove();
				this.totalCarWait += currentCar.timeToLeaveQueue.getTime() - currentCar.getCarRepresenting().getTimeSent().getTime();
			} else {
				//we have enough tokens.
				if(this.numberOfTokens > 0) {
					this.numberOfTokens--;
					this.sendCarToParkingLot(currentCar);
					this.amountOfMoney += moneyPerCarPassed;


					this.numberOfCarsLetThrough++;

					this.totalCarWait += newTime.getTime() - currentCar.getCarRepresenting().getTimeSent().getTime();

					iter.remove();
					
					allowedCar = true;
				} 
			}
		}
		
		if(allowedCar){
			checkTokenStatus();
		}
	}

	/**
	 * Specifies what action a Gate should take when it is queried for the token amount
	 * Currently this just writes a message with its total amount of tokens to the simulator
	 */
	public void onTokenAmountQuery(){
		System.out.println("Token amount query! Implement this shit!");
	}


	/**
	 * Specifies what the Gate should do when it receives a query for the amount of money it currently has
	 * Currently it just responds to the simulation with the amount of money the gate has
	 */
	public void onMoneyAmountQuery(){

		System.out.println("Money amount query, implement this shit too");
	}

	/**
	 * Kills the gate
	 */
	public void killMyself()
	{
		try {
			//close the simulation listener if it is open
			if(this.simulationMessageListener != null){
				this.simulationMessageListener.close();
			}
			//close the manager connection if it is still open
			if(this.manager != null){
				this.manager.close();
			}
		} catch (IOException e) {
			//Already closed
		}

		System.out.println("Gate #" + this.realPort + " ended with $" + this.amountOfMoney + " from " + this.numberOfCarsLetThrough +
				" let through and " + this.numberOfSadnessCars + " cars which had to be kicked out and " + this.numberOfTokens +
				" tokens leftover");
	}

	/**
	 * Specifies the different actions to take with given messages
	 * @param message The message which is being acted upon
	 */

	public void onMessageReceived(AbstractMessage message, MessageListener receivedFrom) {
		synchronized(this){
			switch(message.getMessageType())
			{
			case AbstractMessage.TYPE_CAR_ARRIVAL:
			{
				this.onCarArrived((CarArrivalMessage) message);
				break;
			}
			case AbstractMessage.TYPE_TIME_MESSAGE:
			{
				this.onTimeUpdate((TimeMessage) message);
				break;
			}
			case AbstractMessage.TYPE_CLOSE_CONNECTION:
			{
				killMyself();
				break;
			}
			case AbstractMessage.TYPE_TOKEN_MESSAGE:
			{
				TokenMessage tokenMessage = (TokenMessage) message;
				this.numberOfTokens += tokenMessage.getNumberOfTokensSent();
				break;
			}
			case AbstractMessage.TYPE_MONEY_QUERY_MESSAGE:
			{
				this.onMoneyAmountQuery();
				break;
			}
			case AbstractMessage.TYPE_TOKEN_QUERY_MESSAGE:
			{
				this.onTokenAmountQuery();
				break;
			}
			case AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE:
			{
				TokenRequestMessage request = (TokenRequestMessage) message;
				int tokensToSend = trader.onTokenRequestReceived();
				
				if(tokensToSend >= request.getTokensRequested()){
					tokensToSend = request.getTokensRequested();
				}
				
				TokenResponseMessage response = new TokenResponseMessage(tokensToSend, request.getReceivers());
				try {
					this.manager.writeMessage(response);
				} catch (IOException e) {
					//TODO Do stuff
				}
				break;
			}
			case AbstractMessage.TYPE_GATE:
			{
				GateMessage gateMessage = (GateMessage) message;
				try {
					Socket sock = new Socket(gateMessage.getAddr(), gateMessage.getPort());
					MessageListener listener = new MessageListener(this, sock);
					this.manager = listener;
					this.manager.start();
					this.manager.writeMessage(new TokenMessage(this.numberOfTokens));
					this.simulationMessageListener.getSocketListeningOn().close();
				} catch (IOException e) {
					System.out.println("ERROR WHEN CONNECTING TO THE MANAGER");
					return;
				}

				break;
			}
			case AbstractMessage.TYPE_TOKEN_RESPONSE_MESSAGE:
			{
				this.onTokenResponseReceived((TokenResponseMessage) message);
				break;
			}
			case AbstractMessage.TYPE_DONE:
			{
				killMyself();
				break;
			}
			default:
			{
				System.out.println("What are you doing Message Type = "+message.getMessageType());
				break;
				//Do something
			}
			}
		}
	}
	
	/**
	 * Updates the number of tokens that are sent to us after we have received or given up tokens in a trade.
	 * Also, if we are using a profit token trader policy this method will adjust for any changes in our money
	 * @param message The token
	 */
	protected void onTokenResponseReceived(TokenResponseMessage message){

		System.out.println("Gate# " + this.realPort + " received " + message.getNumberOfTokens() + " tokens ");
		
		//if we have a profit token trader, add the necessary amnt of money
		if(this.trader instanceof ProfitTokenTrader){
			
			int moneyToChange = -1 * message.getNumberOfTokens() * this.costPerToken;
			System.out.println("Have a profit tokenTrader " + moneyToChange);
			this.amountOfMoney += -1 * message.getNumberOfTokens() * this.costPerToken;
		}

		this.numberOfTokens += message.getNumberOfTokens();

	}

	/**
	 * Checks to see if the gate needs to trade for more tokens.  If it does, the gate sends a request for more tokens
	 */
	protected void checkTokenStatus(){
		int tokensToRequest = this.trader.requestTokens();
	
		if(tokensToRequest > 0){
			try {
				this.manager.writeMessage(new TokenRequireMessage(tokensToRequest));
			} catch (IOException e) {
				// TODO Don't do anything???
			}
		}
	}

	@Override
	public void onSocketClosed(Socket socket) {
		//TODO implement this!

	}

	/**
	 * Returns the number of cars currently waiting to enter
	 * @return The number of cars in the queue
	 */
	public int getCarsWaiting() {
		return waitingCars.size();
	}

	/**
	 * Addes tokens to the number that the gate has
	 * @param tokens The number of tokens being added
	 */
	public void onTokensAdded(int tokens) {
		this.numberOfTokens += tokens;
	}
	
	/**
	 * Gets the number of tokens that this gate currently has
	 * @return The number of tokens the gate has
	 */
	public int getNumberTokens() {
		return this.numberOfTokens;
	}
	
	/**
	 * Removes tokens from this gate
	 * @param numberOfTokensToReceive The number of tokens to remove
	 * @return Whether or not the tokens could be removed
	 */
	public boolean removeTokens(int numberOfTokensToReceive) {
		if(this.numberOfTokens - numberOfTokensToReceive > 0)
		{
			this.numberOfTokens -= numberOfTokensToReceive;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the amount of money the gate has
	 * @return The amount of money the gate has
	 */
	public int getAmountOfMoneyLeft() {
		return this.amountOfMoney;
	}


	/**
	 * Sends a message to the TrafficSimulator that this gate has completed its responsibilities.
	 */
	public void sendDone()
	{
		GateDoneMessage message = new GateDoneMessage(this.addrListeningOn, this.portListeningOn);
		try {
			this.manager.writeMessage(message);
		} catch (IOException e) {
			//do stuff
		}

	}

	/**
	 * Sends a Car to the ParkingLot
	 * @param carWrapper, The car which is being sent to the Parking Lot
	 */
	public void sendCarToParkingLot(CarWrapper carWrapper)
	{
		System.out.println("Gate #"+realPort +": Sending a car to the parking lot. It will leave at " 
				+ carWrapper.carRepresenting.getTimeDeparts() +" Tokens: "+this.numberOfTokens + " amount of money is: " 
				+ this.amountOfMoney + " length of queue is " + this.getCarsWaiting());

		CarArrivalMessage message = new CarArrivalMessage(new Date(), carWrapper.getCarRepresenting().getTimeDeparts());

		try {
				this.manager.writeMessage(message);
				this.numberOfTokens--;
				this.numberOfCarsLetThrough++;
				this.amountOfMoney += this.moneyPerCarPassed; 
				
		} catch (IOException e) {
				//Do stuff
			}

	}

	/**
	 * Gets the cost for each token
	 * @return The cost of each token
	 */
	public int getCostPerToken() {
		return costPerToken;
	}


	/**
	 * This is just a utility class which easily wraps the car and the time that it should leave the queue.
	 */
	static class CarWrapper {
		Car carRepresenting;
		Date timeToLeaveQueue;

		public CarWrapper(Car carRepresenting, Date leavingTime)
		{
			this.carRepresenting = carRepresenting;
			this.timeToLeaveQueue = leavingTime;
		}

		public Car getCarRepresenting() {
			return carRepresenting;
		}

	}
}
