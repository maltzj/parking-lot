package simulation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import util.MessageReceiver;
import car.Car;

public class ParkingLot {
	
	List<Car> parkingLot;
	List<MessageReceiver> carReceivers;
	
	public void onTimeUpdate(Date currentTime)
	{
		Calendar leavingCal = Calendar.getInstance();
		leavingCal.setTime(currentTime);
		for(Car car: parkingLot)
		{
			Calendar timeCarLeaves = Calendar.getInstance();
			timeCarLeaves.setTime(car.getTimeDeparts());
			if(timeCarLeaves.after(leavingCal))
			{
				//have the car leave
			}
		}
	}

}
