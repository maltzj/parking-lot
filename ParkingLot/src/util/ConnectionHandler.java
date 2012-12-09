package util;

import java.net.ServerSocket;
import java.net.Socket;

/** You need to implement this interface if you want to accept connections via ConnectionListener */

public interface ConnectionHandler {
	public void onConnectionReceived(Socket newConnection);
	public void onServerError(ServerSocket failedServer);
}
