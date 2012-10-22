package tokentrading;

import java.net.Socket;

import messaging.TokenRequestMessage;
import messaging.TokenSubscribeMessage;

public interface TokenTrader {

	/**
	 * Specifies the what to do when a TokenTrader receives a number of tokens from another Gate
	 * @param tokensReceived, The number of tokens received.
	 */
	public void onTokenReceived(int tokensReceived);
	/**
	 * What specifies what the TokenTraderPolicy should do when a token request is received from another Gate.
	 * @param tokenRequest, The message which specifies information about the token request.
	 * @param socketToSendTo, The socket which the message is received from.
	 */
	public void onTokenRequest(TokenRequestMessage tokenRequest, Socket socketToSendTo);
	
	/**
	 * What to do when another TokenTrader registers with this TokenTrader
	 * @param tokenSubscription, The message which contains information about the tokenTrader doing the subscribing.
	 */
	public void onTokenTraderRegister(TokenSubscribeMessage tokenSubscription);
	
	/**
	 * Called extenerally by the gate to request tokens
	 * @param numberOfTokensToRequest, The number of tokens to request in total.
	 */
	public void requestTokens(int numberOfTokensToRequest);
}
