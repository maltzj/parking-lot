package tokentrading;

import gates.Gate;

/**
* The TokenTrader class is used to specify particular policies that individual gates will use to trade tokens
* Instances contain information such as which gate they are trading tokens for, what to do when a token request is received, 
* and what to do when tokens are low
 */
public abstract class TokenTrader {

	Gate tokenTrader;
	
	/**
	 * Instantiates a TokenTrader as being responsible for a particular Gate
	 * @param tokenTrader The gate which this TokenTrader will use to trade
	 */
	public TokenTrader(Gate tokenTrader){
		this.tokenTrader = tokenTrader;
	}
	
	/**
	 * Gets how many tokens to send when a token request is received.  
	 * Uses the current number of tokens that the gate has as a baseline along with the particular policy
	 * @return The number of tokens that the gate should send to whoever sent the token request
	 */
	public abstract int onTokenRequestReceived();
	
	/**
	 * Gets the number of tokens that should be requested by a given gate
	 * This number is decided based on the number of tokens a gate currently has and the particular polciy
	 * @return The number of tokens that this gate needs to request
	 */
	public abstract int requestTokens();
}
