package gates;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import car.Car;

import messaging.CarArrivalMessage;
import messaging.TimeMessage;

public class GateImpl implements Gate{
	
	ConcurrentLinkedQueue<Car> waitingCars = new ConcurrentLinkedQueue<Car>();
	int numberOfTokens;
	long amountOfTimeToWait;
	
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
	public void onTokenReceived() {
		numberOfTokens++;
		if(waitingCars.size() > 0)
		{
			//let a car through the gate
		}	
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
	
	
}