package messaging;

/**
 * A SimpleMessage which specifies that the simulation is done
 *
 */
public class DoneMessage extends SimpleMessage{

	/**
	 * Creates a DoneMessage
	 */
	public DoneMessage(){
		super(AbstractMessage.TYPE_DONE);
	}
	
}
