package tokentrading;

import gates.Gate;

import java.io.IOException;

import messaging.TokenRequestMessage;

public class ProfitTokenTradingPolicy extends TokenTradingPolicy {

	public ProfitTokenTradingPolicy(Gate gateResponsibleFor, int port)
			throws IOException {
		super(gateResponsibleFor, port);
	}

	@Override
	public void requestTokens(int numberOfTokensToRequest) {
		
	}
	
	@Override
	public void onTokenRequest(TokenRequestMessage tokenRequest) {
		
	}

}
