package messaging;

/**
 * A TokenMessage is used to send tokens to a actor within the system
 *
 */
public class TokenMessage extends AbstractMessage {
	
	int numberOfTokensSent;

	/**
	 * Initializes the number of tokens to the input value
	 * @param numberOfTokensSent The number of tokens which are being sent
	 */
	public TokenMessage(int numberOfTokensSent) {
		super(AbstractMessage.TYPE_TOKEN_MESSAGE);
		this.numberOfTokensSent = numberOfTokensSent;
	}

	/**
	 * Gets the number of tokens sent with the message
	 * @return The number of tokens in the message
	 */
	public int getNumberOfTokensSent() {
		return numberOfTokensSent;
	}

	/**
	 * Sets the number of tokens sent with the message
	 * @param numberOfTokensSent The number of tokens in the message
	 */
	public void setNumberOfTokensSent(int numberOfTokensSent) {
		this.numberOfTokensSent = numberOfTokensSent;
	}
	
	

}
