package messaging;

public class TokenAmountMessage extends AbstractMessage {
	
	int numberOfTokens;
	
	public TokenAmountMessage(int numberOfTokens)
	{
		super(AbstractMessage.TYPE_TOKEN_AMOUNT_MESSAGE);
		this.numberOfTokens = numberOfTokens;
	}

}
