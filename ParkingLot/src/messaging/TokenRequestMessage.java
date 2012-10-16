package messaging;


public class TokenRequestMessage extends AbstractMessage {
	
	private int totalNumberOfTokensRequested;

	public TokenRequestMessage(int totalNumberOfTokensRequested) {
		super(AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE);
		this.totalNumberOfTokensRequested = totalNumberOfTokensRequested;
	}
	
	public int getTotalNumberOfTokensRequested() {
		return totalNumberOfTokensRequested;
	}

	public void setTotalNumberOfTokensRequested(int totalNumberOfTokensRequested) {
		this.totalNumberOfTokensRequested = totalNumberOfTokensRequested;
	}

	
	
	
}
