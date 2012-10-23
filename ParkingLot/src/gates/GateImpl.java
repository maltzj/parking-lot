package gates;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.GateDoneMessage;
import messaging.GateSubscribeMessage;
import messaging.MoneyAmountMessage;
import messaging.TimeMessage;
import messaging.TimeSubscribeMessage;
import messaging.TokenAmountMessage;
import messaging.TokenMessage;
import util.Config;
import util.MessageReceiver;
import car.Car;

/**
 * A concrete implementation of the Gate interface.  This is responsible for handling all the responsibilities of a Gate
 * This includes trading tokens, listening for messages, and allowing cars into the parking lot.
 * @author Jonathan
 *
 */
public class GateImpl extends MessageReceiver implements Gate {
	public static boolean stillRunning = true;

	ConcurrentLinkedQueue<CarWrapper> waitingCars = new ConcurrentLinkedQueue<CarWrapper>();
	long amountOfTimeToWait; //Seconds
	
	Thread messageListenerThread;
	
	int numberOfTokens;
	
	int amountOfMoney;
	int moneyPerCarPassed;
	
	SimulationMessageListener messageListener;
	Thread listenerThread;
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
	public GateImpl(long timeToWait, int tokensToStartWith, int moneyToStartWith,InetAddress addr, int port, int moneyPerCarPassed) throws Exception
	{
        super(addr, port);

        this.amountOfTimeToWait = timeToWait*1000; //dates deal with milliseconds, we want to expose all APIs as seconds
        this.amountOfMoney = moneyToStartWith;
        this.numberOfTokens = tokensToStartWith;

		this.moneyPerCarPassed = moneyPerCarPassed;
		
		Config c = new Config();
		messageListener = new SimulationMessageListener(this, new Socket(c.trafficGenerator.iaddr, c.trafficGenerator.port));
		listenerThread = new Thread(messageListener);
		listenerThread.start();
		messageListener.writeMessage(new TimeSubscribeMessage(this.ipAddress, this.port));
		messageListener.writeMessage(new GateSubscribeMessage(this.ipAddress, this.port));
		messageListener.writeMessage( new GateDoneMessage(this.ipAddress, this.port));

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
			carLeaveQueueTime.setTime(currentCar.timeLeaving);
			
			//Car waited too long and left
			if(timeToCheckAgainst.after(carLeaveQueueTime)) {
                toRemove.add(currentCar);
			} else {
                //we have enough tokens.
                if(this.numberOfTokens > 0) {
                    this.numberOfTokens--;
                    this.sendCarToParkingLot(currentCar);
					this.amountOfMoney += moneyPerCarPassed;
                    toRemove.add(currentCar);
                } else {
                    //we have enough tokens.
                    if(this.numberOfTokens > 0) {
                        this.numberOfTokens--;
                        this.sendCarToParkingLot(currentCar);
                        this.amountOfMoney += moneyPerCarPassed;
                        toRemove.add(currentCar);
                    }
                }
            }
        }

        for(CarWrapper car: toRemove)
        {
            waitingCars.remove(car);
        }

        sendDone();
    }

    public void onTokenAmountQuery(){
        Config c = new Config();
        TokenAmountMessage message = new TokenAmountMessage(this.numberOfTokens, this.ipAddress, this.port);
        System.out.println(port+": Gave up "+numberOfTokens);
        this.numberOfTokens = 0;
        try 
        {
            Socket s = new Socket();
            s.setReuseAddress(true);
            s.connect(new InetSocketAddress(c.trafficGenerator.iaddr, c.trafficGenerator.port));


            OutputStream o = s.getOutputStream();
            AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
        } 
        catch(Exception e) {
            e.printStackTrace();
        }	
    }

    public void onMoneyAmountQuery(){
        MoneyAmountMessage message = new MoneyAmountMessage(this.amountOfMoney, this.ipAddress, this.port);
        this.amountOfMoney = 0;
        Config c = new Config();
        try 
        {
            Socket s = new Socket();
            s.setReuseAddress(true);
            s.connect(new InetSocketAddress(c.trafficGenerator.iaddr, c.trafficGenerator.port));

            OutputStream o = s.getOutputStream();
            AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
        } 
        catch(Exception e) {
            e.printStackTrace();
        }	

    }

    @Override
        public void onMessageArrived(AbstractMessage message) {
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
                        System.out.println(port+": I have "+numberOfTokens+" tokens");
                        this.die = true;
                        break;
                    }
                case AbstractMessage.TYPE_TOKEN_MESSAGE:
                    {
                        TokenMessage tokenMessage = (TokenMessage) message;
                        this.numberOfTokens += tokenMessage.getNumberOfTokensSent();

                        System.out.println(port+": Now I got a total of "+numberOfTokens);
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
                default:
                    {
                        //Do something
                    }
            }
        }

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

    /** Makes dude subscribe to Traffic generator.
     * @param ip - IP Address of Traffic Generator
     * @param port - Port of Traffic Generator
     */
    public void timeSubscribe()
    {
		TimeSubscribeMessage message = new TimeSubscribeMessage(this.ipAddress, this.port);
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
		GateSubscribeMessage message = new GateSubscribeMessage(this.ipAddress, this.port);
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
        Config c = new Config();
		GateDoneMessage message = new GateDoneMessage(this.ipAddress, this.port);
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
        System.out.println(port +": Sending a car to the parking lot. It will leave at "+carWrapper.timeLeaving+" Tokens: "+this.numberOfTokens + " amount of money is: " + this.amountOfMoney + " length of queue is " + this.getCarsWaiting());

        Config c = new Config();

        CarArrivalMessage message = new CarArrivalMessage(new Date(), carWrapper.timeLeaving);
		try {
			this.messageListener.writeMessage(message);
		} catch (IOException e) {
			//Do stuff
		}
		
    }

    private static class CarWrapper {
        Car carRepresenting;
        Date timeLeaving;

        public CarWrapper(Car carRepresenting, Date leavingTime)
        {
            this.carRepresenting = carRepresenting;
            this.timeLeaving = leavingTime;
        }

        public Car getCarRepresenting() {
            return carRepresenting;
        }

        public void setCarRepresenting(Car carRepresenting) {
            this.carRepresenting = carRepresenting;
        }

        public Date getTimeLeaving() {
            return timeLeaving;
        }

        public void setTimeLeaving(Date timeLeaving) {
            this.timeLeaving = timeLeaving;
        }
    }
}
