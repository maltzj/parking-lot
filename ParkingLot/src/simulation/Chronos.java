package simulation;

import java.util.Date;

import messaging.TimeSubscribeMessage;
import java.util.Date;

public interface Chronos 
{	
	public Date getCurrentTime();
	public void onTimeSubscribeReceived(TimeSubscribeMessage messageReceived);
	public void publishTime();
}
