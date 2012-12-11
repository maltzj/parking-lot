package gates;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.GateDoneMessage;
import messaging.GateMessage;
import messaging.GateSubscribeMessage;
import messaging.MoneyMessage;
import messaging.SimpleMessage;
import messaging.TimeMessage;
import messaging.TimeSubscribeMessage;
import messaging.TokenMessage;
import util.Config;
import util.MessageHandler;
import util.MessageListener;
import car.Car;

/**
 * A concrete implementation of the Gate interface.  This is responsible for handling all the responsibilities of a Gate
 * This includes trading tokens, listening for messages, and allowing cars into the parking lot.
 * @author Jonathan
 *
 */
public class Gate implements MessageHandler{
	
    public static boolean stillRunning = true;

    ConcurrentLinkedQueue<CarWrapper> waitingCars = new ConcurrentLinkedQueue<CarWrapper>();
    long amountOfTimeToWait; //Seconds

    MessageListener simulationMessageListener;
    MessageListener manager;
    
    int numberOfTokens;

    int amountOfMoney;
    int moneyPerCarPassed;
    
    InetAddress addrListeningOn;
    int portListeningOn;
    private int realPort;
    
    int numberOfSadnessCars = 0;
    int totalCarWait = 0;
    int numberOfCarsLetThrough = 0;
    
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
    public Gate(long timeToWait, int tokensToStartWith, int moneyToStartWith, InetAddress addr, int port, int moneyPerCarPassed) throws Exception
    {
    	
    	Config c = Config.getSharedInstance();
    	
    	this.addrListeningOn = addr;
    	this.portListeningOn = port;
    	
        this.amountOfTimeToWait = timeToWait*1000; //dates deal with milliseconds, we want to expose all APIs as seconds
        this.amountOfMoney = moneyToStartWith;
        this.numberOfTokens = tokensToStartWith;

		this.moneyPerCarPassed = moneyPerCarPassed;
		
		/*Connect to the simulation*/
		Socket s = new Socket(c.trafficGenerator.gate.iaddr, c.trafficGenerator.gate.port);
		simulationMessageListener = new MessageListener(this, s);
		simulationMessageListener.setDaemon(true);
		simulationMessageListener.start();
		simulationMessageListener.writeMessage(new SimpleMessage(AbstractMessage.TYPE_CONNECT));
		
		realPort = s.getLocalPort();
    }
	
	
	public void onCarArrived(CarArrivalMessage arrival) {
		Car carToQueue = new Car(arrival.getCarSentTime(), arrival.getCarReturnTime());

		
		//Add Car to queue
		long timeArrived = arrival.getCarSentTime().getTime();
		long leavingTime = timeArrived + amountOfTimeToWait;

		Date timeToLeave = new Date();
		timeToLeave.setTime(leavingTime);
		CarWrapper carWrapper = new CarWrapper(carToQueue, timeToLeave);
		waitingCars.add(carWrapper);

		while(this.numberOfTokens > 0 && this.waitingCars.size() > 0){
			Car c = this.waitingCars.poll().carRepresenting;
			try {
				this.manager.writeMessage(new CarArrivalMessage(c.getTimeSent(), c.getTimeDeparts()));
			} catch (IOException e) {
				// TODO DEAL WITH THAT
			}
			this.numberOfTokens -= 1;
		}
		
		
	}

	public void onCarLeave() {
		numberOfTokens++;
		//do any additional logic re: broadcasting
	}

	public void onTimeUpdate(TimeMessage messageFromChronos){
		
		Date newTime = messageFromChronos.getNewTime();
		Calendar timeToCheckAgainst = Calendar.getInstance();
		timeToCheckAgainst.setTime(newTime);

        ArrayList<CarWrapper> toRemove = new ArrayList<CarWrapper>();
        

		for(CarWrapper currentCar: waitingCars)
		{
			Calendar carLeaveQueueTime = Calendar.getInstance();
			carLeaveQueueTime.setTime(currentCar.timeToLeaveQueue);
			
			//Car waited too long and left
			if(timeToCheckAgainst.after(carLeaveQueueTime)) {
                numberOfSadnessCars++;
                toRemove.add(currentCar);
                this.totalCarWait += currentCar.timeToLeaveQueue.getTime() - currentCar.getCarRepresenting().getTimeSent().getTime();
			} else {
                //we have enough tokens.
                if(this.numberOfTokens > 0) {
                    this.numberOfTokens--;
                    this.sendCarToParkingLot(currentCar);
					this.amountOfMoney += moneyPerCarPassed;

                     
                    this.numberOfCarsLetThrough++;

                    this.totalCarWait += newTime.getTime() - currentCar.getCarRepresenting().getTimeSent().getTime();

                    toRemove.add(currentCar);
                } 
            }
        }

        for(CarWrapper car: toRemove)
        {
            waitingCars.remove(car);
        }

        sendDone();
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
			this.simulationMessageListener.close();
		} catch (IOException e) {
			//Already closed
		}
    	
    	System.out.println("Gate #" + this.realPort + " ended with $" + this.amountOfMoney + " from " + this.numberOfCarsLetThrough +
    			" let through and " + this.numberOfSadnessCars + " cars which had to be kicked out and " + this.numberOfTokens +
    			" leftover");
    }

	/**
	 * Specifies the different actions to take with given messages
	 * @param message The message which is being acted upon
	 */

       public void onMessageReceived(AbstractMessage message, Socket receivedFrom) {
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
            	case AbstractMessage.TYPE_MONEY_MESSAGE:
            	{
            		MoneyMessage money = (MoneyMessage) message;
            		this.amountOfMoney += money.getAmountOfMoney();
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
						this.manager.writeMessage(new TokenMessage(this.numberOfTokens));
						this.simulationMessageListener.getSocketListeningOn().close();
					} catch (IOException e) {
						System.out.println("ERROR WHEN CONNECTING TO THE MANAGER");
						return;
					}
            		
            		break;
            	}
            	default:
            	{
            		System.out.println("What are you doing Message Type = "+message.getMessageType());
            		System.exit(1);
            		//Do something
            	}
            	}
            }
        }
       

   	@Override
   	public void onSocketClosed(Socket socket) {
   		//TODO implement this!
   		
   		System.out.println("ON SOCKET CLOSED GOT CALLED, THAT SHIT NEEDS TO BE IMPLEMENTED!!!");
   		//check if it is the simulation
   			//if yes, do something
   		//if it is not, then find out which gate we're connected to
   			//disconnect the socket
   			//remove the gate from the list
   	}

       /**
        * Returns the number of cars currently waiting to enter
        * @return The number of cars in the queue
        */
    public int getCarsWaiting() {
        return waitingCars.size();
    }

    public void onTokensLow() {
    	//
    }

    public void onTokensAdded(int tokens) {
    	this.numberOfTokens += tokens;
    }

    public int getNumberTokens() {
    	return this.numberOfTokens;
    }

    public boolean removeTokens(int numberOfTokensToReceive) {
    	if(this.numberOfTokens - numberOfTokensToReceive > 0)
    	{
    		this.numberOfTokens -= numberOfTokensToReceive;
    		return true;
    	}
    	return false;
    }

    public int getAmountOfMoneyLeft() {
    	return this.amountOfMoney;
    }

    public boolean removeMoney(int amountOfMoneyToTake) {
    	if(this.amountOfMoney < amountOfMoneyToTake)
    		return false;
    	else
    	{
    		this.amountOfMoney -= amountOfMoneyToTake;
    		return true;
    	}
    }

    public void addMoney(int amountOfMoneyToAdd) {
    	this.amountOfMoney += amountOfMoneyToAdd;
    }

    public void timeSubscribe()
    {
		TimeSubscribeMessage message = new TimeSubscribeMessage(this.addrListeningOn, this.portListeningOn);
		try {
			this.simulationMessageListener.writeMessage(message);
		} catch (IOException e) {
			//do stuff
		}
	}
    
    /**
     * Subscribes to another Gate so that it can trade tokens with that Gate.
     */
    public void gateSubscribe()
    {
		GateSubscribeMessage message = new GateSubscribeMessage(this.addrListeningOn, this.portListeningOn);
		try {
			this.simulationMessageListener.writeMessage(message);
		} catch (IOException e) {
			//do stuff
		}
	}
	
	/**
	 * Sends a message to the TrafficSimulator that this gate has completed its responsibilities.
	 */
    public void sendDone()
    {
		GateDoneMessage message = new GateDoneMessage(this.addrListeningOn, this.portListeningOn);
		try {
			this.simulationMessageListener.writeMessage(message);
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
        System.out.println("Gate #"+realPort +": Sending a car to the parking lot. It will leave at "+carWrapper.timeToLeaveQueue+" Tokens: "+this.numberOfTokens + " amount of money is: " + this.amountOfMoney + " length of queue is " + this.getCarsWaiting());

        CarArrivalMessage message = new CarArrivalMessage(new Date(), carWrapper.getCarRepresenting().getTimeDeparts());
		try {
			this.simulationMessageListener.writeMessage(message);
		} catch (IOException e) {
			//Do stuff
		}
		
    }

    /**
     * This is just a utility class which easily wraps the car and the time that it should leave the queue.
     */
    private static class CarWrapper {
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
