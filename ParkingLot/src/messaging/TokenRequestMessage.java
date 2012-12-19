package messaging;

import java.util.Stack;

import util.HostPort;

/**
 * A TokenRequestMessage is a message sent between gates requesting tokens from their respective dates.
 * 
 */
public class TokenRequestMessage extends AbstractMessage {
	
	private int tokensRequested;
	private int ttl;
	
	//the list of people who this request has gone through
	private Stack<HostPort> receivers;
	

	/**
	 * Initializes a token request message to have a given number of tokens requested and with a particular history and time to live 
	 * @param tokensRequested The number of tokens which are being requested
	 * @param receivers A list of people that this TokenRequestMessage has gone through
	 * @param ttl The time to live of this message
	 */
	public TokenRequestMessage(int tokensRequested, Stack<HostPort> receivers, int ttl) {
		super(AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE);
		this.tokensRequested = tokensRequested;
		this.ttl = ttl;
		this.receivers = receivers;
	}
	
	/**
	 * Gets the number of tokens that are requested by this request
	 * @return The number of tokens requested by this request
	 */
	public int getTokensRequested() {
		return tokensRequested;
	}

	/**
	 * Sets the number of tokens requested by this request
	 * @param tokensRequested The number of tokens requested by the request
	 */
	public void setTokensRequested(int tokensRequested) {
		this.tokensRequested = tokensRequested;
	}

	/**
	 * Gets the list of receivers who have received this request
	 * @return The list of receivers who have received this request
	 */
	public Stack<HostPort> getReceivers() {
		return receivers;
	}

	/**
	 * Set the list of receivers who have received this request
	 * @param receivers The receivers who have received this request
	 */
	public void setReceivers(Stack<HostPort> receivers) {
		this.receivers = receivers;
	}

	/**
	 * Gets the time to live of this request
	 * @return The time to live of this request
	 */
	public int getTtl() {
		return ttl;
	}
	
	/**
	 * Sets the time to live of this request
	 * @param ttl Gets the time to live of this request
	 */
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public String toString(){
		return this.receivers.toString() + " " + this.tokensRequested;
	}
	
}
