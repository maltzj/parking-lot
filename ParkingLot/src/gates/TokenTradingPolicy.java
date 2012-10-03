package gates;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import util.MessageReceiver;

import messaging.AbstractMessage;
import messaging.TokenRequestMessage;
import messaging.TokenSubscribeMessage;

public class TokenTradingPolicy implements TokenTrader, Runnable {

	ServerSocket serverListeningOn;
	List<MessageReceiverListener> gatesToCommunicateWith;
	
	@Override
	public void onTokenReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTokenRequest(TokenRequestMessage tokenRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTokenTraderRegister(TokenSubscribeMessage tokenSubscription) {
		// TODO Auto-generated method stub
		
	}
	
	public void onMessageReceived(AbstractMessage messageReceived)
	{
		
	}
	
	@Override
	public void run() {
		while(GateImpl.stillRunning)
		{
			try {
				Socket acceptedSocket = serverListeningOn.accept();
				MessageReceiver receiver = new MessageReceiver(acceptedSocket);
				this.gatesToCommunicateWith.add(new MessageReceiverListener(receiver, this));
				
			} catch (IOException e) {
				//Do shit
			}
			
			
		}
	}
	
	private static class MessageReceiverListener extends Thread
	{
		MessageReceiver currentReceiver;
		TokenTradingPolicy tradingPolicy;
		
		public MessageReceiverListener(MessageReceiver receiver, TokenTradingPolicy policy)
		{
			currentReceiver = receiver;
		}
		
		public void run(){
			while (GateImpl.stillRunning)
			{
				AbstractMessage message;
				try {
					message = AbstractMessage.decodeMessage(currentReceiver.getSocket().getInputStream());
					tradingPolicy.onMessageReceived(message);
				} catch (IOException e) {
					//do shit
				}

			}
		}
	}

}
