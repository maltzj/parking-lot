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
		if(this.tokenTrader.getAmountOfMoneyLeft() > this.tokenTrader.getCostPerToken()){
			return this.tokenTrader.getNumberTokens() == 0 ? 1 : 0;  //ensure that we have one token
		}
		else{ //otherwise we cant afford this, return nothing
			return 0;
		}
	}

}
