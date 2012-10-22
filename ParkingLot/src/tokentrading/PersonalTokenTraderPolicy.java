package tokentrading;

import gates.Gate;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import messaging.AbstractMessage;
import messaging.TokenMessage;
import messaging.TokenRequestMessage;

public class PersonalTokenTraderPolicy extends TokenTradingPolicy{

	public PersonalTokenTraderPolicy(Gate gateResponsibleFor, int port)
			throws IOException {
		super(gateResponsibleFor, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void requestTokens(int numberOfTokensToRequest) {
		Random rand = new Random(System.currentTimeMillis());
		if(this.gatesToCommunicateWith.size() != 0)
		{
			int numberOfTokens = numberOfTokensToRequest / this.gatesToCommunicateWith.size() > 0 ? 1 : numberOfTokensToRequest/this.gatesToCommunicateWith.size();
			for(MessageReceiverListener listener : this.gatesToCommunicateWith)
			{
				if(rand.nextInt(2) == 1)
				{
					try {
						listener.sendMessage(new TokenRequestMessage(numberOfTokens));
					} catch (IOException e) {
						//do nothing??
					}
				}
			}
		}
	}

	public void onTokenRequest(TokenRequestMessage tokenRequest, Socket socketToSendTo) {
		int numberOfTokensRequested = tokenRequest.getTotalNumberOfTokensRequested();
		Random rand = new Random(System.currentTimeMillis());

		if(rand.nextInt(2) == 1)
		{
			if(this.gatesToCommunicateWith.size() > 0)
			{
				int numberOfTokensWhichWouldBeSent = numberOfTokensRequested / this.gatesToCommunicateWith.size();
				int numberOfTokensToSend = 0;
				if (numberOfTokensWhichWouldBeSent + 10 <= this.gateResponsibleFor.getNumberTokens())
					numberOfTokensToSend = numberOfTokensWhichWouldBeSent;
				else
				{
					numberOfTokensToSend = Math.max(0, this.gateResponsibleFor.getNumberTokens() - numberOfTokensWhichWouldBeSent);
				}
				
				
				TokenMessage messageToSend = new TokenMessage(numberOfTokensToSend);
				try {
					AbstractMessage.encodeMessage(socketToSendTo.getOutputStream(), messageToSend);
				} catch (IOException e) {
					// herpderp
				}
			}
		}
	}

}
