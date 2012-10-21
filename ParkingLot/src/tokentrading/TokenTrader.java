package tokentrading;

import java.net.Socket;

import messaging.TokenRequestMessage;
import messaging.TokenSubscribeMessage;

public interface TokenTrader {

	public void onTokenReceived(int tokensReceived);
	public void onTokenRequest(TokenRequestMessage tokenRequest, Socket socketToSendTo);
	public void onTokenTraderRegister(TokenSubscribeMessage tokenSubscription);
	public void requestTokens(int numberOfTokensToRequest);
}
