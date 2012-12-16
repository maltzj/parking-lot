package messaging;

public class TokenRequireMessage extends AbstractMessage {

	private int tokensRequired;
	
	public TokenRequireMessage(int tokensRequired){
		super(AbstractMessage.TYPE_TOKEN_REQUIRE_MESSAGE);
		this.tokensRequired = tokensRequired;
	}

	public int getTokensRequired() {
		return tokensRequired;
	}

	public void setTokensRequired(int tokensRequired) {
		this.tokensRequired = tokensRequired;
	}
	
	
}
