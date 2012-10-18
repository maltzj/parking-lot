package tokentrading;

import gates.Gate;

import java.io.IOException;
import java.net.Socket;

import messaging.AbstractMessage;
import messaging.TokenMessage;
import messaging.TokenRequestMessage;

public class ProfitTokenTradingPolicy extends TokenTradingPolicy {

	int costPerToken;
	
	public ProfitTokenTradingPolicy(Gate gateResponsibleFor, int port, int tokenCost)
			throws IOException {
		super(gateResponsibleFor, port);
		this.costPerToken = tokenCost;
	}

	@Override
	public void requestTokens(int numberOfTokensToRequest) {
		synchronized(gateResponsibleFor)
		{
			int amountOfMoney = this.gateResponsibleFor.getAmountOfMoneyLeft();
			int amountOfTokensWhichCanBeRequested = amountOfMoney/costPerToken;
			int numberOfTokensWhichWillBeRequested = Math.min(numberOfTokensToRequest, amountOfTokensWhichCanBeRequested);
		
			this.gateResponsibleFor.removeMoney(amountOfTokensWhichCanBeRequested * costPerToken);
		
			int numberOfTokensPerGate = numberOfTokensWhichWillBeRequested / this.gatesToCommunicateWith.size();
			int numberOfTokensLeftOver = (numberOfTokensWhichWillBeRequested - numberOfTokensPerGate) % this.gatesToCommunicateWith.size();
		
			for(MessageReceiverListener listener : this.gatesToCommunicateWith)
			{
				int totalTokensToRequest = numberOfTokensLeftOver > 0 ? numberOfTokensPerGate + 1 : numberOfTokensPerGate;
				numberOfTokensLeftOver--;
				try {
					AbstractMessage.encodeMessage(listener.currentReceiver.getSocket().getOutputStream(),
												new TokenRequestMessage(totalTokensToRequest));
				} catch (IOException e) {
					//herpderp
				}
			}
		}
		
	}
	
	@Override
	public void onTokenRequest(TokenRequestMessage tokenRequest, Socket socketToSendTo) {
		int numberOfTokensDesired = tokenRequest.getTotalNumberOfTokensRequested();
		int numberOfTokensFree = Math.max(0, this.gateResponsibleFor.getNumberTokens() - 2);
		int numberOfTokensSent = Math.min(numberOfTokensDesired, numberOfTokensFree);
		TokenMessage messageToSend = new TokenMessage(numberOfTokensSent);
		this.gateResponsibleFor.removeTokens(numberOfTokensSent);
		try {
			AbstractMessage.encodeMessage(socketToSendTo.getOutputStream(), messageToSend);
		} catch (IOException e) {
			//deal with the error
		}
		
		
	}

}
