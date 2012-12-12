package tokentrading;

import gates.Gate;

public class GlobalTokenTrader extends TokenTrader {

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
