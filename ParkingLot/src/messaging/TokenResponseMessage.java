package messaging;

import java.util.Stack;

import util.HostPort;

/**
 * 
 * The TokenResponseMessage is a response to a token request and used to send tokens between managers
 *
 */
public class TokenResponseMessage extends AbstractMessage{

	int numberOfTokens;
	Stack<HostPort> receivers;
	
	/**
	 * Initializes the response to have a given number of tokens and a given set of receivers
	 * @param numTokens The number of tokens that the message has
	 * @param receivers The list of receivers that the receivers should be routed through
	 */
	public TokenResponseMessage(int numTokens, Stack<HostPort> receivers) {
		super(AbstractMessage.TYPE_TOKEN_RESPONSE_MESSAGE);
		this.numberOfTokens = numTokens;
		this.receivers = receivers;
	}
	
	/**
	 * Get the number of tokens that this response contains
	 * @return The number of tokens the response contains
	 */
	public int getNumberOfTokens() {
		return numberOfTokens;
	}
	
	/**
	 * Sets the number of tokens that the response contains
	 * @param numberOfTokens The number of tokens the response contains
	 */
	public void setNumberOfTokens(int numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
	}
	
	/**
	 * Get the list of receivers who this message will pass through
	 * @return The list of receivers who this message will pass through
	 */
	public Stack<HostPort> getReceivers() {
		return receivers;
	}
	
	/**
	 * Set the list of receivers that this message will pass through
	 * @param receivers The actors that this message will pass through
	 */
	public void setReceivers(Stack<HostPort> receivers) {
		this.receivers = receivers;
	}
	
	public String toString(){
		return this.receivers.toString();
	}
	
}
