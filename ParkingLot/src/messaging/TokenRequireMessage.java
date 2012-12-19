package messaging;

/**
 * 
 * The TokenRequireMessage is used by Gates to communicate that they need tokens
 *
 */
public class TokenRequireMessage extends AbstractMessage {

	private int tokensRequired;
	
	/**
	 * Initializes a TokenRequireMessage with a given number of required tokens
	 * @param tokensRequired The number of tokens required
	 */
	public TokenRequireMessage(int tokensRequired){
		super(AbstractMessage.TYPE_TOKEN_REQUIRE_MESSAGE);
		this.tokensRequired = tokensRequired;
	}

	/**
	 * Gets the number of tokens required by the gate
	 * @return The number of tokens required by the gate
	 */
	public int getTokensRequired() {
		return tokensRequired;
	}

	/**
	 * Sets the number of tokens required by the gate
	 * @param tokensRequired Sets the number of tokens required by the gate
	 */
	public void setTokensRequired(int tokensRequired) {
		this.tokensRequired = tokensRequired;
	}
	
	
}
