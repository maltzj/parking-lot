package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.io.*;
import java.net.*;

import util.ConnectionListener;
import util.ConnectionHandler;

/**
 * This class encompases all of the TrafficGeneration capabilities within the program.  It is also somewhat responsible for the redistribution of tokens.
 * 
 */
public class TrafficGenerator implements ConnectionHandler
{
	public TrafficGenerator() throws Exception
	{
        System.out.println("I am a traffic generator");

        ConnectionListener listener = new ConnectionListener(this, 10000);
        listener.setDaemon(false);
        listener.start();
    }

    public void onConnectionReceived(Socket connection)
    {
        System.out.println("I got me a connection from "+connection.getPort());
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while(true)
            {
                String line = bf.readLine();
                if(line == null)
                    break;
                System.out.println(line);
            }
        } catch(IOException e) {
            System.out.println("fuck");
            return;
        }
    
    }

    public void onServerError(ServerSocket failedSocket)
    {
        System.out.println("There is sadness in the world");
    }
}
