package util;

import java.net.InetAddress;

/**
 * 
 * A configuration for the application which specifies the IP addresses and ports of the gates and traffic generator
 *
 */
public class Config
{
	public HostPort trafficGenerator;
	public HostPort [] gates;
	
	/**
	 * Starts up the config class and initializes the TrafficGenerator and Gates.
	 * The ipAddress defaults to localhost and the ports default to 7500 (for the Generator)
	 * and the gates are on ipAddress 7501 up.
	 */
	public Config()
	{
		try
		{
			trafficGenerator = new HostPort(InetAddress.getByName("localhost"),7500);
			gates = new HostPort [6];
			for(int i = 0; i < 6; i++)
			{
				gates[i] = new HostPort(InetAddress.getByName("localhost"), 7501 + i);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void printConfig()
	{
		System.out.println(trafficGenerator.iaddr + " " + trafficGenerator.port);
		for(int i = 0; i < 6; i++)
		{
			System.out.println(gates[i].iaddr + " " + gates[i].port);
		}	
	}
	/*public static void main(String [] args)
	{
		Config c = new Config();
		c.printConfig();
	}*/
}
