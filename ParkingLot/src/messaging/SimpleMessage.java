package messaging;

/**
 * SimpleMessages are messages which simply announce a change in state, they carry no payload
 *
 */
public class SimpleMessage extends AbstractMessage {
	
	public SimpleMessage(byte typeOfMessage)
	{
		super(typeOfMessage);
	}

}
