package messaging;

import java.util.Date;

public class TimeMessage extends AbstractMessage {

	Date newTime;
	
	public TimeMessage(Date newTime)
	{
		super(AbstractMessage.TYPE_TIME_MESSAGE);
		this.newTime = newTime;
	}

	public Date getNewTime() {
		return newTime;
	}

	public void setNewTime(Date newTime) {
		this.newTime = newTime;
	}
	
	public byte[] generateMessageData()
	{
		return null;
	}
	
}
