package tokentrading;

import gates.Gate;

public abstract class TokenTrader {

	Gate tokenTrader;
	
	public abstract void onTokenRequestReceived();
	public abstract void requestTokens();
}
