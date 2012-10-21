import java.net.InetAddress;

public class Config
{
	public HostPort trafficGenerator;
	public HostPort [] gates;
	public Config()
	{
		try
		{
			trafficGenerator = new HostPort();
			trafficGenerator.iaddr = InetAddress.getByName("localhost");
			trafficGenerator.port = 6000;
			gates = new HostPort [6];
			for(int i = 0; i < 6; i++)
			{
				gates[i] = new HostPort();
				gates[i].iaddr = InetAddress.getByName("localhost");
				gates[i].port = 6001 + i;
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
