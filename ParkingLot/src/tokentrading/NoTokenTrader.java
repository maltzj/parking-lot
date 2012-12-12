package tokentrading;

import gates.Gate;

public class NoTokenTrader extends TokenTrader {

	public NoTokenTrader(Gate tokenTrader) {
		super(tokenTrader);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int onTokenRequestReceived() {
		return 0;
	}

	@Override
	public int requestTokens() {
		return 0;
	}

}
