package messaging;

import java.util.Date;

public class CarArrivalMessage extends AbstractMessage {


	Date carSentTime;
	Date carReturnTime;
	
	public CarArrivalMessage(Date carSentTime, Date carReturnTime, int length) {
		super(length, AbstractMessage.TYPE_CAR_ARRIVAL);
		this.carSentTime = carSentTime;
		this.carReturnTime = carReturnTime;
	}
	
	public Date getCarSentTime() {
		return carSentTime;
	}
	public void setCarSentTime(final Date carSentTime) {
		this.carSentTime = carSentTime;
	}
	public Date getCarReturnTime() {
		return carReturnTime;
	}
	public void setCarReturnTime(final Date carReturnTime) {
		this.carReturnTime = carReturnTime;
	}
	
	public byte[] generateMessageData()
	{
		return null;
	}
}
