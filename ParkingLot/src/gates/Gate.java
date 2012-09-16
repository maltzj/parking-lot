package gates;

import messaging.CarArrivalMessage;

public interface Gate {

	
	public void onCarArrived(CarArrivalMessage arrival);
	public void onCarLeave();
	
}
