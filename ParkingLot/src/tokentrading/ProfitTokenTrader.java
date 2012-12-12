package tokentrading;

import gates.Gate;

public class ProfitTokenTrader extends TokenTrader {

	public ProfitTokenTrader(Gate tokenTrader) {
		super(tokenTrader);
	}

	@Override
	public int onTokenRequestReceived() {  //when we receive a request, be willing to trade all but one token
		if(this.tokenTrader.getNumberTokens() > 1){
			return this.tokenTrader.getNumberTokens() - 1;
		}
		return 0;
	}

	@Override
	public int requestTokens() {
		return this.tokenTrader.getNumberTokens() == 0 ? 1 : 0;  //ensure that we have one token
	}

}
