package util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import util.ConnectionHandler;

/** You need to create an instance of this, to get a ServerSocket listening for messages */
public class ConnectionListener extends Thread {
	
	private ConnectionHandler connectionHandler;
	private int port;
	private ServerSocket server;
	
	/**
	 * Initializes a ConnectionListener to report back to a given ConnectionHandler
	 * @param connectionHandler Where new connections will be passed
	 * @param port The port where the ServerSocket will listen
	 * @throws IOException
	 */
	public ConnectionListener(ConnectionHandler connectionHandler, int port) throws IOException {
		super();
		this.connectionHandler = connectionHandler;
		this.port = port;
		
		this.server = new ServerSocket(port);
	}

	@Override
	public void run() {
		while(!server.isClosed()){ //keep running for as long as the application is
			try {
				Socket connection = server.accept();
				this.connectionHandler.onConnectionReceived(connection, this.port);
			} catch (IOException e) {
				this.connectionHandler.onServerError(this.server);
			}
		}
	}
	
	/**
	 * Returns the ConnectionHandler which handles connections for this ConnectionListener
	 * @return This instance's ConnectionHandler
	 */
	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	/**
	 * Sets the ConnectionHandler which handles connections for this ConnectionListener
	 * @param connectionHandler The new ConnectionHandler for this instance
	 */
	public void setConnectionHandler(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}

	/**
	 * Gets the port that this ConnectionHandler is listening on
	 * @return The port that is being listened on
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the ServerSocket that this ConnectionListener is using to listen for connections
	 * @return The ServerSocket which is being used to listen for connections
	 */
	public ServerSocket getServer() {
		return server;
	}

}
