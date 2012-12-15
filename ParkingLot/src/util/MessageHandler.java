package util;

import java.net.Socket;

import messaging.AbstractMessage;

public interface MessageHandler {

	
	public void onMessageReceived(AbstractMessage message, MessageListener socket);
	public void onSocketClosed(Socket socket);
}
