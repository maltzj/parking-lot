package util;

import java.net.Socket;

import messaging.AbstractMessage;

/**
 * This interface must be implemented for a class to receive messages from a MessageListener
 *
 */
public interface MessageHandler {

	/**
	 * Handles the message which was just received by a MessageListener.
	 * @param message The message that was just received by a MessageListener
	 * @param socket The MessageListener that it was received on
	 */
	public void onMessageReceived(AbstractMessage message, MessageListener socket);
	
	/**
	 * Handles a socket that a MessageListener is using becomes closed
	 * @param socket The socket that was closed
	 */
	public void onSocketClosed(Socket socket);
}
