package gates;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import simulation.TrafficGenerator;
import util.Config;
import util.ConnectionHandler;

public class ConnectionListener extends Thread {
	
	private ConnectionHandler connectionHandler;
	private int port;
	private ServerSocket server;
	
	public ConnectionListener(ConnectionHandler connectionHandler, int port) throws IOException {
		super();
		this.connectionHandler = connectionHandler;
		this.port = port;
		
		this.server = new ServerSocket(port);
	}

	@Override
	public void run() {
		while(GateImpl.stillRunning && !server.isClosed()){ //keep running for as long as the application is
			try {
				Socket connection = server.accept();
				this.connectionHandler.onConnectionReceived(connection);
			} catch (IOException e) {
				this.connectionHandler.onServerError(this.server);
			}
		}
	}
	
	
	
	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public void setConnectionHandler(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

}
