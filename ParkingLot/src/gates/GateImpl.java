package gates;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import tokentrading.TokenTrader;

import messaging.CarArrivalMessage;
import messaging.TimeMessage;
import car.Car;

public class GateImpl implements Gate{
	
	public static boolean stillRunning = true;
	
	ConcurrentLinkedQueue<CarWrapper> waitingCars = new ConcurrentLinkedQueue<CarWrapper>();
	long amountOfTimeToWait; //Seconds
	
	Thread messageListenerThread;
	SimulationMessageListener messageListener;
	
	int numberOfTokens;
	TokenTrader tokenTrader;
	
	int amountOfMoney;
	
	public GateImpl(long timeToWait, int moneyToStartWith, TokenTrader tokenPolicy, int port)
	{
		this.amountOfTimeToWait = timeToWait*1000; //dates deal with milliseconds, we want to expose all APIs as seconds
		this.amountOfMoney = moneyToStartWith;
		tokenTrader = tokenPolicy;
		
		messageListener = new SimulationMessageListener(port, this);
		messageListenerThread = new Thread(messageListener);
		messageListenerThread.setName("Simulation Message Listener Thread");
		messageListenerThread.start();
	}
	
	
	@Override
	public void onCarArrived(CarArrivalMessage arrival) {
		Car carToQueue = new Car(arrival.getCarSentTime(), arrival.getCarReturnTime());
		if(numberOfTokens <= 0)
		{
			long timeArrived = arrival.getCarSentTime().getTime();
			long leavingTime = timeArrived + amountOfTimeToWait;
			Date timeToLeave = new Date();
			timeToLeave.setTime(leavingTime);
			CarWrapper carWrapper = new CarWrapper(carToQueue, timeToLeave);
		}
		else
		{
			//do some logic that puts the car into place
		}
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
		for(CarWrapper currentCar: waitingCars)
		{
			Calendar carLeaveQueueTime = Calendar.getInstance();
			carLeaveQueueTime.setTime(currentCar.timeLeaving);
			if(timeToCheckAgainst.after(carLeaveQueueTime))
				waitingCars.remove(currentCar);
		}
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
