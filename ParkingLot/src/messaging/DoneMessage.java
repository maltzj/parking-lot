package messaging;

public class DoneMessage extends SimpleMessage{

	public DoneMessage(){
		super(AbstractMessage.TYPE_DONE);
	}
	
}
