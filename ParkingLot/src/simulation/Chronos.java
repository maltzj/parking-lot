package simulation;

import messaging.TimeSubscribeMessage;

public interface Chronos 
{	
	public long getCurrentTime();
	public void onSubscribeReceived(TimeSubscribeMessage messageReceived);
	public void publish();
}
