package tokentrading;

import gates.Gate;

public abstract class TokenTrader {

	Gate tokenTrader;
	
	public TokenTrader(Gate tokenTrader){
		this.tokenTrader = tokenTrader;
	}
	
	public abstract int onTokenRequestReceived();
	public abstract int requestTokens();
}
