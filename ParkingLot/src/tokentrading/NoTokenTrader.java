package tokentrading;

import gates.Gate;

/**
 * A version of TokenTrader which doesn't trade with any other gates.
 * No matter what the case, it will always respond that the gate it is responsible for does not need tokens.
 * Similarly, on a token request, it will say a gate is able to service none of those requests
 */
public class NoTokenTrader extends TokenTrader {

	/**
	 * Creates a TokenTrader which is responsible for a given gate and never trades
	 * @param tokenTrader The gate that is being traded for
	 */
	public NoTokenTrader(Gate tokenTrader) {
		super(tokenTrader);
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
