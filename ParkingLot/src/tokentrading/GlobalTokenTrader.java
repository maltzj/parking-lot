package tokentrading;

import gates.Gate;

public class GlobalTokenTrader extends TokenTrader {

	public GlobalTokenTrader(Gate tokenTrader) {
		super(tokenTrader);
	}

	@Override
	public void onTokenRequestReceived() {
		//get the number of tokens of the gate has
		
			//if(numTokens >= 3)
				//take the min of 3 tokens and numTokens - 3
			//else
				//do nothing
	}

	@Override
	public void requestTokens() {
		//pick two random gates that are neighbors
		//request two tokens from each of them
	}

}
