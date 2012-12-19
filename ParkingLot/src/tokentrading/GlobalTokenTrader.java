package tokentrading;

import gates.Gate;

/**
 * This version of TokenTrader implements a global token trader strategy
 * The global policy that we chose to implement is to ensure that gates always have least 3 tokens. 
 * Thus, gates will never trade below three tokens, and will always request to have 3 more tokens.
 *
 */
public class GlobalTokenTrader extends TokenTrader {

	/**
	 * Creates a TokenTrader which is responsible for a given gate and implements our global policy
	 * @param tokenTrader
	 */
	public GlobalTokenTrader(Gate tokenTrader) {
		super(tokenTrader);
	}

	@Override
	public int onTokenRequestReceived() {
		int currTokens = this.tokenTrader.getNumberTokens();
		if(currTokens > 3){
			return Math.min(3, currTokens - 3);
		}
		else{
			return 0;
		}
	}

	@Override
	public int requestTokens() {
		int currTokens = this.tokenTrader.getNumberTokens();
		if(currTokens < 3){
			return 3 - currTokens;
		}
		
		return 0;
	}

}
