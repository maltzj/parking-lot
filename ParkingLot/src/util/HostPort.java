package util;

import java.net.InetAddress;

/**
 * 
 * An wrapper class around an external host which specifies an IP and a Port for that host
 *
 */
public class HostPort
{
	public InetAddress iaddr;
	public int port;
	
	/**
	 * Creates a new HostPort with an IP address and port number.
	 * @param i The InetAddress of the external 
	 * @param p
	 */
	public HostPort(InetAddress i, int p)
	{
		iaddr = i;
		port = p;
	}
}
