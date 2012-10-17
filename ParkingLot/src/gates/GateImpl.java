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
	
	ConcurrentLinkedQueue<Car> waitingCars = new ConcurrentLinkedQueue<Car>();
	int numberOfTokens;
	long amountOfTimeToWait;
	SimulationMessageListener messageListener;
	TokenTrader tokenTrader;
	int amountOfMoney;
	
	
	@Override
	public void onCarArrived(CarArrivalMessage arrival) {
		Car carToQueue = new Car(arrival.getCarSentTime(), arrival.getCarReturnTime(), amountOfTimeToWait);
		if(numberOfTokens <= 0)
			waitingCars.add(carToQueue);
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
		for(Car currentCar: waitingCars)
		{
			Calendar carLeaveQueueTime = Calendar.getInstance();
			carLeaveQueueTime.setTime(currentCar.getTimeWaitingUntil());
			if(timeToCheckAgainst.after(carLeaveQueueTime))
				waitingCars.remove(currentCar);
		}
	}

	@Override
	public void onTokensLow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTokensAdded(int tokens) {
		this.numberOfTokens += tokens;
	}

	@Override
	public int getNumberTokens() {
		// TODO Auto-generated method stub
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

	
}
