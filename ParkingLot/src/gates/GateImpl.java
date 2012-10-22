package gates;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.*;
import tokentrading.TokenTrader;
import util.MessageReceiver;
import util.Config;
import car.Car;
import java.net.*;
import java.io.*;

public class GateImpl extends MessageReceiver implements Gate {
	
	public static boolean stillRunning = true;
	
	ConcurrentLinkedQueue<CarWrapper> waitingCars = new ConcurrentLinkedQueue<CarWrapper>();
	long amountOfTimeToWait; //Seconds
	
	Thread messageListenerThread;
	
	int numberOfTokens;
	TokenTrader tokenTrader;
	
	int amountOfMoney;
	
	public GateImpl(long timeToWait, int moneyToStartWith, TokenTrader tokenPolicy, InetAddress addr, int port) throws Exception
	{
        super(addr, port);

		this.amountOfTimeToWait = timeToWait*1000; //dates deal with milliseconds, we want to expose all APIs as seconds
		this.amountOfMoney = moneyToStartWith;
		tokenTrader = tokenPolicy;
        
        //Subscribe to car and time updates.
        Config config = new Config();
        timeSubscribe(config.trafficGenerator.iaddr, config.trafficGenerator.port);
        gateSubscribe(config.trafficGenerator.iaddr, config.trafficGenerator.port);
        sendDone(config.trafficGenerator.iaddr, config.trafficGenerator.port);

        //Create thread for listening to time and gate subscriptions
        Thread listeningThread = new Thread(this);
        listeningThread.start();
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
        System.out.println("currentTime: " + (newTime.getTime() / 1000));
		Calendar timeToCheckAgainst = Calendar.getInstance();
		timeToCheckAgainst.setTime(newTime);
		for(CarWrapper currentCar: waitingCars)
		{
			Calendar carLeaveQueueTime = Calendar.getInstance();
			carLeaveQueueTime.setTime(currentCar.timeLeaving);
			
			//Car waited too long and left
			if(timeToCheckAgainst.after(carLeaveQueueTime)) {
				waitingCars.remove(currentCar);
			}
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
    public void timeSubscribe(InetAddress ip, int port)
    {
		TimeSubscribeMessage message = new TimeSubscribeMessage(this.ipAddress, this.port);
		try 
		{
            Socket s = new Socket(ip, port);
            OutputStream o = s.getOutputStream();
            AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
		} 
		catch(Exception e) {
			System.out.println("Sadddnesss");
		}	
	}
	public void gateSubscribe(InetAddress ip, int port)
    {
		GateSubscribeMessage message = new GateSubscribeMessage(this.ipAddress, this.port);
		try 
		{
            Socket s = new Socket(ip, port);
            OutputStream o = s.getOutputStream();
            AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
		} 
		catch(Exception e) {
			System.out.println("Sadddnesss");
		}	
	}
    public void sendDone(InetAddress ip, int port)
    {
		GateDoneMessage message = new GateDoneMessage(this.ipAddress, this.port);
		try 
		{
            Socket s = new Socket(ip, port);
            OutputStream o = s.getOutputStream();
            AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
		} 
		catch(Exception e) {
			System.out.println("Sadddnesss");
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
