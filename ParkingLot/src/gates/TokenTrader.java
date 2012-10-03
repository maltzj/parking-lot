package gates;

import messaging.TokenRequestMessage;
import messaging.TokenSubscribeMessage;

public interface TokenTrader {

	public void onTokenReceived();
	public void onTokenRequest(TokenRequestMessage tokenRequest);
	public void onTokenTraderRegister(TokenSubscribeMessage tokenSubscription);
}
