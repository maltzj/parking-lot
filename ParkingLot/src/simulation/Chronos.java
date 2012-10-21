package simulation;

import messaging.TimeSubscribeMessage;
import java.util.ArrayList;

public interface Chronos 
{	
	private ArrayList<HostPort> subscribers;
	public long getCurrentTime();
	public void onSubscribeReceived(TimeSubscribeMessage messageReceived);
	public void notifyAll();
}
