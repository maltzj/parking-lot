package messaging;

import java.util.Date;

/**
 * Specifies information about a Car arrival.  
 * This includes the time that a car was sent from the traffic generator and when it will leav
 *
 */
public class CarArrivalMessage extends AbstractMessage {


	Date carSentTime;
	Date carReturnTime;
	
	/**
	 * Creates a Message with type car arrival and a given sent and return time
	 * @param carSentTime The time the car left the traffic
	 * @param carReturnTime The time it returned
	 */
	public CarArrivalMessage(Date carSentTime, Date carReturnTime) {
		super(AbstractMessage.TYPE_CAR_ARRIVAL);
		this.carSentTime = carSentTime;
		this.carReturnTime = carReturnTime;
	}
	
	/**
	 * Get the time that the car was sent at
	 * @return The time the car was sent
	 */
	public Date getCarSentTime() {
		return carSentTime;
	}
	
	/**
	 * Set the time the car was sent
	 * @param carSentTime The time the car was sent
	 */
	public void setCarSentTime(final Date carSentTime) {
		this.carSentTime = carSentTime;
	}
	
	/**
	 * Get the time the car will return
	 * @return The time the car will return
	 */
	public Date getCarReturnTime() {
		return carReturnTime;
	}
	
	/**
	 * Set the time the car will be returned
	 * @param carReturnTime The new time the car will be returned
	 */
	public void setCarReturnTime(final Date carReturnTime) {
		this.carReturnTime = carReturnTime;
	}
	
}
