package simulation;

import java.util.Date;

import messaging.TimeSubscribeMessage;

public interface Chronos 
{	
	public Date getCurrentTime();
	public void onSubscribeReceived(TimeSubscribeMessage messageReceived);
	public void publish();
}
