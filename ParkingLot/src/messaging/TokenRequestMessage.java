package messaging;

import java.util.Stack;

import util.HostPort;


public class TokenRequestMessage extends AbstractMessage {
	
	private int tokensRequested;
	private Stack<HostPort> receivers;
	private int ttl;

	public TokenRequestMessage(int tokensRequested, Stack<HostPort> receivers, int ttl) {
		super(AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE);
		this.tokensRequested = tokensRequested;
		this.ttl = ttl;
		this.receivers = receivers;
	}

	public int getTokensRequested() {
		return tokensRequested;
	}

	public void setTokensRequested(int tokensRequested) {
		this.tokensRequested = tokensRequested;
	}

	public Stack<HostPort> getReceivers() {
		return receivers;
	}

	public void setReceivers(Stack<HostPort> receivers) {
		this.receivers = receivers;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public String toString(){
		return this.receivers.toString() + " " + this.tokensRequested;
	}
	
}
