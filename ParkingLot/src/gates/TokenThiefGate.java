package gates;

import java.net.InetAddress;

import messaging.TokenResponseMessage;

public class TokenThiefGate extends Gate {

	public TokenThiefGate(long timeToWait, int tokensToStartWith,
			int moneyToStartWith, InetAddress addr, int port,
			int moneyPerCarPassed, int tradingPolicy, int costPerToken) throws Exception {
		super(timeToWait, tokensToStartWith, moneyToStartWith, addr, port,
				moneyPerCarPassed, tradingPolicy, costPerToken);
	}
	
	protected void onTokenResponseReceived(TokenResponseMessage message){
		
		if(message.getNumberOfTokens() > 0){ //only do the super method if we got a positive number of tokens
			super.onTokenResponseReceived(message);
		}
		
	}

}
