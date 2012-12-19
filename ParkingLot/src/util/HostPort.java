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

    /**
     * Checks if two HostPorts are equal.  Equality is defined as having the same InetAddress and port
     * Returns false if any object other than a HostPort is passed in
     */
    public boolean equals(Object other)
    {
        if(!(other instanceof HostPort))
        {
        	return false;
        }
        HostPort h = (HostPort) other;
        boolean iaddrEqual = this.iaddr.equals(h.iaddr);
        boolean portEqual = this.port == h.port;
        return iaddrEqual && portEqual;
       
    }
    
    public String toString() {
    	String ret = "";
    	ret += "Host: " + iaddr.toString() + " Port: " + port;
    	return ret;
    }
}
