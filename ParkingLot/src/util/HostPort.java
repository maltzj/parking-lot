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

    public int hashCode()
    {
        return (""+this.iaddr.hashCode() + ""+ port).hashCode();
    }

    public boolean equals(Object other)
    {
        if(other instanceof HostPort)
        {
            HostPort h = (HostPort) other;
            return this.iaddr.equals(h.iaddr) && this.port == h.port;
        }
        return false;
    }
}
