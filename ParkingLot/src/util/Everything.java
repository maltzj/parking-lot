package util;
import messaging.*;
import java.util.*;
import util.Config;
import util.HostPort;
import java.net.*;
import java.io.*;
/** This contains all the utilities you may ever need. */

public class Everything
{
    public static void sendMessage(AbstractMessage message, InetAddress ip, int port) throws IOException
    {
    	Socket s = null;
        try {
            s = new Socket(ip, port);
            
            OutputStream o = s.getOutputStream();

            AbstractMessage.encodeMessage(o, message);
            o.flush();
        } catch(Exception e) {
					e.printStackTrace();
        }
        finally{
        	s.close();
        }
    }
}
