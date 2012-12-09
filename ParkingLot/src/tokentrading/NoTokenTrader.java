package tokentrading;

import gates.Gate;

public class NoTokenTrader extends TokenTrader {

	NoTokenTrader(Gate tokenTrader) {
		super(tokenTrader);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onTokenRequestReceived() {
		//do nothing
	}

	@Override
	public void requestTokens() {
		//do nothing
	}

}
