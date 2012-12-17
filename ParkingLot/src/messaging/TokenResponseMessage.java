package messaging;

import java.util.Stack;

import util.HostPort;

public class TokenResponseMessage extends AbstractMessage{


	int numberOfTokens;
	Stack<HostPort> receivers;
	
	public TokenResponseMessage(int numTokens, Stack<HostPort> receivers) {
		super(AbstractMessage.TYPE_TOKEN_RESPONSE_MESSAGE);
		this.numberOfTokens = numTokens;
		this.receivers = receivers;
	}
	
	public int getNumberOfTokens() {
		return numberOfTokens;
	}
	public void setNumberOfTokens(int numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
	}
	public Stack<HostPort> getReceivers() {
		return receivers;
	}
	public void setReceivers(Stack<HostPort> receivers) {
		this.receivers = receivers;
	}
	
	public String toString(){
		return this.receivers.toString();
	}
	
}
