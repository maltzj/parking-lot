package simulation;

import messaging.TimeSubscribeMessage;

public interface Chronos {
	
	public long getCurrentTime();
	public void onSubscribeReceived(TimeSubscribeMessage messageRecieved);

}
