package simulation;

import messaging.TimeSubscribeMessage;

public interface Chronos 
{	
	private HostPort [] subscribers;
	public long getCurrentTime();
	public void onSubscribeReceived(TimeSubscribeMessage messageRecieved);
	public void notifyAll();
}
