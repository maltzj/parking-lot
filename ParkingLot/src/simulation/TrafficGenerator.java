package simulation;

import java.net.ServerSocket;
import java.net.Socket;

import messaging.AbstractMessage;
import util.Config;
import util.ConnectionHandler;
import util.ConnectionListener;
import util.MessageHandler;
import util.MessageListener;

/**
 * This class encompases all of the TrafficGeneration capabilities within the program.  It is also somewhat responsible for the redistribution of tokens.
 * 
 */
public class TrafficGenerator implements ConnectionHandler, MessageHandler
{
	public TrafficGenerator() throws Exception
	{
        System.out.println("I am a traffic generator");
        
        Config config = Config.getSharedInstance();

        ConnectionListener listener = new ConnectionListener(this, config.trafficGenerator.port);
        listener.setDaemon(false);
        listener.start();
    }

    public void onConnectionReceived(Socket connection)
    {
        System.out.println("I got me a connection from "+connection.getPort());
        
        MessageListener msgListener = new MessageListener(this, connection);
        msgListener.setDaemon(false);
        msgListener.start();
    
    }

    public void onServerError(ServerSocket failedSocket)
    {
        System.out.println("There is sadness in the world");
    }

	@Override
	public void onMessageReceived(AbstractMessage message, Socket socket) {
		System.out.println("Received Message");
		
		
	}

	@Override
	public void onSocketClosed(Socket socket) {
		// TODO Auto-generated method stub
		
	}
}
