package tokentrading;

import gates.Gate;

import java.io.IOException;

import messaging.TokenRequestMessage;

public class NoTokenTraderPolicy extends TokenTradingPolicy{

	public NoTokenTraderPolicy(Gate gateResponsibleFor, int port)
			throws IOException {
		super(gateResponsibleFor, port);
	}

	@Override
	public void requestTokens(int numberOfTokensToRequest) {
		//do nothing
	}
	
	@Override
	public void onTokenRequest(TokenRequestMessage tokenRequest) {
		
	}

}
