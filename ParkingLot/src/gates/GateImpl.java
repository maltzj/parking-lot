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
import messaging.GateSubscribeMessage;
import messaging.MoneyMessage;
import messaging.TimeMessage;
import messaging.TimeSubscribeMessage;
import messaging.TokenMessage;
import util.Config;
import util.MessageHandler;
import car.Car;

/**
 * A concrete implementation of the Gate interface.  This is responsible for handling all the responsibilities of a Gate
 * This includes trading tokens, listening for messages, and allowing cars into the parking lot.
 * @author Jonathan
 *
 */
public class GateImpl implements Gate, MessageHandler{
	
    public static boolean stillRunning = true;

    ConcurrentLinkedQueue<CarWrapper> waitingCars = new ConcurrentLinkedQueue<CarWrapper>();
    long amountOfTimeToWait; //Seconds

    SimulationMessageListener messageListener;
    Thread listenerThread;

    int numberOfTokens;

    private int realPort = 0;

    int numberOfSadnessCars = 0;
    int totalCarWait = 0;
    int numberOfCarsLetThrough = 0;
    
    int amountOfMoney;
    int moneyPerCarPassed;
    
    InetAddress addrListeningOn;
    int portListeningOn;

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
    public GateImpl(long timeToWait, int tokensToStartWith, int moneyToStartWith, InetAddress addr, int port, int moneyPerCarPassed) throws Exception
    {
    	this.addrListeningOn = addr;
    	this.portListeningOn = port;
    	
        this.amountOfTimeToWait = timeToWait*1000; //dates deal with milliseconds, we want to expose all APIs as seconds
        this.amountOfMoney = moneyToStartWith;
        this.numberOfTokens = tokensToStartWith;

		this.moneyPerCarPassed = moneyPerCarPassed;
		
		/*Connect to the simulation*/
		Config c = new Config();
		Socket s = new Socket(c.trafficGenerator.iaddr, c.trafficGenerator.port);
		messageListener = new SimulationMessageListener(this, s);
		listenerThread = new Thread(messageListener);
		listenerThread.start();
		
		messageListener.writeMessage(new TimeSubscribeMessage(this.addrListeningOn, this.portListeningOn));
		messageListener.writeMessage(new GateSubscribeMessage(this.addrListeningOn, this.portListeningOn));
		
        realPort = s.getLocalPort();

	}
	
	
	@Override
	public void onCarArrived(CarArrivalMessage arrival) {
		Car carToQueue = new Car(arrival.getCarSentTime(), arrival.getCarReturnTime());

		//Add Car to queue
		long timeArrived = arrival.getCarSentTime().getTime();
		long leavingTime = timeArrived + amountOfTimeToWait;

		Date timeToLeave = new Date();
		timeToLeave.setTime(leavingTime);
		CarWrapper carWrapper = new CarWrapper(carToQueue, timeToLeave);
		waitingCars.add(carWrapper);
		
	}

	@Override
	public void onCarLeave() {
		numberOfTokens++;
		//do any additional logic re: broadcasting
	}

	@Override
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
        System.out.println("Kill myself needs to be implemented god damnit");
    }

	/**
	 * Specifies the different actions to take with given messages
	 * @param message The message which is being acted upon
	 */

       public void onMessageReceived(AbstractMessage message, Socket receivedFrom) {
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
                default:
                    {
                        System.out.println("What are you doing Message Type = "+message.getMessageType());
                        System.exit(1);
                        //Do something
                    }
            }
        }
       

   	@Override
   	public void onSocketClosed(Socket socket) {
   		
   		
   	}

       /**
        * Returns the number of cars currently waiting to enter
        * @return The number of cars in the queue
        */
    public int getCarsWaiting() {
        return waitingCars.size();
    }

    @Override
        public void onTokensLow() {
            //
        }

    @Override
        public void onTokensAdded(int tokens) {
            this.numberOfTokens += tokens;
        }

    @Override
        public int getNumberTokens() {
            return this.numberOfTokens;
        }

    @Override
        public boolean removeTokens(int numberOfTokensToReceive) {
            if(this.numberOfTokens - numberOfTokensToReceive > 0)
            {
                this.numberOfTokens -= numberOfTokensToReceive;
                return true;
            }
            return false;
        }

    @Override
        public int getAmountOfMoneyLeft() {
            return this.amountOfMoney;
        }

    @Override
        public boolean removeMoney(int amountOfMoneyToTake) {
            if(this.amountOfMoney < amountOfMoneyToTake)
                return false;
            else
            {
                this.amountOfMoney -= amountOfMoneyToTake;
                return true;
            }
        }

    @Override
        public void addMoney(int amountOfMoneyToAdd) {
            this.amountOfMoney += amountOfMoneyToAdd;
        }

    public void timeSubscribe()
    {
		TimeSubscribeMessage message = new TimeSubscribeMessage(this.addrListeningOn, this.portListeningOn);
		try {
			this.messageListener.writeMessage(message);
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
			this.messageListener.writeMessage(message);
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
			this.messageListener.writeMessage(message);
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
			this.messageListener.writeMessage(message);
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

        public void setCarRepresenting(Car carRepresenting) {
            this.carRepresenting = carRepresenting;
        }

        public Date getTimeLeavingQueue() {
            return timeToLeaveQueue;
        }

        public void setTimeLeavingQueue(Date timeLeaving) {
            this.timeToLeaveQueue = timeLeaving;
        }
    }

}
