package simulation;

import java.util.Date;

import messaging.TimeSubscribeMessage;

public interface Chronos 
{	
	/**
	 * Gets the current of the system.
	 * @return A Date which represents the current time of the system.
	 */
	public Date getCurrentTime();
	
	/**
	 * Specifies what to do whenan entity wants to subscribe to time updates
	 * @param messageReceived The TimeSubscribeMessage which contains info about the subscriber
	 */
	public void onTimeSubscribeReceived(TimeSubscribeMessage messageReceived);
	
	/**
	 * Publishes the time to all subscribed entities.
	 */
	public void publishTime();
}
