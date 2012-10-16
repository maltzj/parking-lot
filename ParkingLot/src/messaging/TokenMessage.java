package messaging;

public class TokenMessage extends AbstractMessage {
	
	int numberOfTokensSent;

	public TokenMessage(int numberOfTokensSent) {
		super(AbstractMessage.TYPE_TOKEN_MESSAGE);
		this.numberOfTokensSent = numberOfTokensSent;
	}

	public int getNumberOfTokensSent() {
		return numberOfTokensSent;
	}

	public void setNumberOfTokensSent(int numberOfTokensSent) {
		this.numberOfTokensSent = numberOfTokensSent;
	}
	
	

}
