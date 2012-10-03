package gates;

import messaging.TokenRequestMessage;

public interface TokenTrader {

	public void onTokenReceived();
	public void onTokenRequest(TokenRequestMessage tokenRequest);
}
