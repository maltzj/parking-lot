package util;

import java.net.ServerSocket;
import java.net.Socket;

public interface ConnectionHandler {
	public void onConnectionReceived(Socket newConnection);
	public void onServerError(ServerSocket failedServer);
}
