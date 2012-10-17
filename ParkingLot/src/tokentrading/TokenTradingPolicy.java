package tokentrading;

import gates.Gate;
import gates.GateImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import util.MessageReceiver;

import messaging.AbstractMessage;
import messaging.TokenMessage;
import messaging.TokenRequestMessage;
import messaging.TokenSubscribeMessage;

public abstract class TokenTradingPolicy implements TokenTrader, Runnable {

	ServerSocket serverListeningOn;
	List<MessageReceiverListener> gatesToCommunicateWith;
	Gate gateResponsibleFor;
	
	
	public TokenTradingPolicy(Gate gateResponsibleFor, int port) throws IOException {
		this.serverListeningOn = new ServerSocket(port);
		this.gatesToCommunicateWith = new ArrayList<TokenTradingPolicy.MessageReceiverListener>();
		this.gateResponsibleFor = gateResponsibleFor;
	}

	@Override
	public void onTokenReceived(int numberOfTokensReceived) {
		gateResponsibleFor.onTokensAdded(numberOfTokensReceived);
	}

	public abstract void onTokenRequest(TokenRequestMessage tokenRequest, Socket socketToRequestOn);

	@Override
	public void onTokenTraderRegister(TokenSubscribeMessage tokenSubscription) {
		try
		{
			MessageReceiver tokenReceiver = new MessageReceiver(tokenSubscription.getAddressSubscribing(), tokenSubscription.getPortSubscribingOn());
			gatesToCommunicateWith.add(new MessageReceiverListener(tokenReceiver,  this));
		}
		catch(IOException e)
		{
			//do something
		}	
	}
	
	public void onMessageReceived(AbstractMessage messageReceived, MessageReceiverListener listener)
	{	
		switch(messageReceived.getMessageType())
		{
			case AbstractMessage.TYPE_TOKEN_MESSAGE:
			{
				this.onTokenReceived(((TokenMessage) messageReceived).getNumberOfTokensSent());
			}
			case AbstractMessage.TYPE_TOKEN_REQUEST_MESSAGE:
			{
				this.onTokenRequest((TokenRequestMessage)messageReceived, listener.currentReceiver.getSocket());
			}
		}
	}
	
	@Override
	public abstract void requestTokens(int numberOfTokensToRequest);
	
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
	
	protected static class MessageReceiverListener extends Thread
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
					tradingPolicy.onMessageReceived(message, this);
				} catch (IOException e) {
					//do shit
				}

			}
		}
		
		public void sendMessage(AbstractMessage messageToSend) throws IOException
		{
			AbstractMessage.encodeMessage(currentReceiver.getSocket().getOutputStream(), messageToSend);
		}
	}
	
	

}
