package tokentrading;

import gates.Gate;

import java.io.IOException;

import messaging.TokenRequestMessage;

public class PersonalTokenTraderPolicy extends TokenTradingPolicy{

	public PersonalTokenTraderPolicy(Gate gateResponsibleFor, int port)
			throws IOException {
		super(gateResponsibleFor, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void requestTokens(int numberOfTokensToRequest) {
		// TODO Auto-generated method stub
		
	}
	
	public void onTokenRequest(TokenRequestMessage tokenRequest) {
		
	}

}
