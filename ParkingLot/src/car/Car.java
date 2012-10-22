package car;

import java.util.Date;


/**
 * An abstraction of a Car as it is used by all parts of the simulation.
 * Note that the only information that is necessary about a car is when it is sent from the simulator
 * and when it departs from the parkingLot.
 * @author Jonathan
 *
 */
public class Car {
	
	Date timeSent;
	Date timeDeparts;
	
	/**
	 * Creates a new Car object with the two important parameters
	 * @param timeSent The time the car is sent.
	 * @param timeDeparts The time the car departs the parking lot.
	 */
	public Car(Date timeSent, Date timeDeparts) {
		super();
		this.timeSent = timeSent;
		this.timeDeparts = timeDeparts;
		
	}
	
	/**
	 * Gets the time this car was sent
	 * @return The time the car was sent.
	 */
	public Date getTimeSent() {
		return timeSent;
	}
	/**
	 * Sets time time the car was sent
	 * @param timeSent, The new specification of when the car was sent
	 */
	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}
	
	/**
	 * Get the time that the car would depart from the parking lot.
	 * @return The time that the car will depart from the parking lot (if it is admitted)
	 */
	public Date getTimeDeparts() {
		return timeDeparts;
	}
	
	/**
	 * Set the time the a car departs from the parking lot.
	 * @param timeDeparts, The new time that the given car departs from the parking lot
	 */
	public void setTimeDeparts(Date timeDeparts) {
		this.timeDeparts = timeDeparts;
	}

	
}
