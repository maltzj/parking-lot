package util;

import java.net.ServerSocket;
import java.net.Socket;

/** You need to implement this interface if you want to accept connections via ConnectionListener */

public interface ConnectionHandler {
	
	/**
	 * Handles the new connection that has been received on a ServerSocket
	 * @param newConnection The connection which was just received
	 * @param receivedOn The port it was received on
	 */
	public void onConnectionReceived(Socket newConnection, int receivedOn);
	
	/**
	 * Handles the ServerSocket which is accepting connections failing
	 * @param failedServer The ServerSocket which failed
	 */
	public void onServerError(ServerSocket failedServer);
}
