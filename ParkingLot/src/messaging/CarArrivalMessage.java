package messaging;

import java.util.Date;

public class CarArrivalMessage {

	Date carSentTime;
	Date carReturnTime;
	
	public Date getCarSentTime() {
		return carSentTime;
	}
	public void setCarSentTime(Date carSentTime) {
		this.carSentTime = carSentTime;
	}
	public Date getCarReturnTime() {
		return carReturnTime;
	}
	public void setCarReturnTime(Date carReturnTime) {
		this.carReturnTime = carReturnTime;
	}
	
	
	
	
}
