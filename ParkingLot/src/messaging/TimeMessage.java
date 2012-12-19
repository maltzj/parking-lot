package messaging;

import java.util.Date;

/**
 * A TimeMessage announces a new canonical time in the system.
 *
 */
public class TimeMessage extends AbstractMessage {

	Date newTime;
	
	/**
	 * Creates a new TimeMessage with the given date as its time
	 * @param newTime
	 */
	public TimeMessage(Date newTime)
	{
		super(AbstractMessage.TYPE_TIME_MESSAGE);
		this.newTime = newTime;
	}

	/**
	 * Get the new time of the system
	 * @return The new time of the system
	 */
	public Date getNewTime() {
		return newTime;
	}

	/**
	 * Set the new time of the system
	 * @param newTime, The new time of the system
	 */
	public void setNewTime(Date newTime) {
		this.newTime = newTime;
	}
		
}
