package util;

import java.net.InetAddress;

/**
 * 
 * A configuration for the application which specifies the IP addresses and ports of the gates and traffic generator
 *
 */
public class Config
{

	private static Config instance = null;
	
	public TrafficGenInfo trafficGenerator;
	public GateInfo[] gates;
	public ManagerInfo[] managers;
	
	public static Config getSharedInstance() {
		if(instance == null) {
			instance = new Config();
		} 
		return instance;
	}
	
	/**
	 * Starts up the config class and initializes the TrafficGenerator and Gates.
	 * The ipAddress defaults to localhost and the ports default to:
	 * Traffic Generator for managers 7549
	 * Traffic Generator at 	7500.
	 * Gates start at 			7501 and up.
	 * Managers start at 		8050 and up.
	 */
	private Config()
	{
		try
		{
			//Set up Traffic Generator
			trafficGenerator = new TrafficGenInfo();
			trafficGenerator.gate = new HostPort(InetAddress.getByName("localhost"), 7500);
			trafficGenerator.manager = new HostPort(InetAddress.getByName("localhost"), 7549);
			
			//Set up Gates
			gates = new GateInfo[6];
			for(int i = 0; i < gates.length; i++) {
				GateInfo g = new GateInfo();
				g.hostport = new HostPort(InetAddress.getByName("localhost"), 7501 + i);
				g.money = 100;
				g.tokens = 10;
				gates[i] = g;
			}
			
			//Set up Managers
			managers = new ManagerInfo[6];
			for(int i = 0; i < managers.length ; i++) {
				ManagerInfo m = new ManagerInfo();
				m.hostport = new HostPort(InetAddress.getByName("localhost"), 8050 + i);
				m.managerPort = new HostPort(InetAddress.getByName("localhost"), 8050 + i + managers.length); //give it a separate manager and gate port
				m.money = 100;
				m.tokens = 10;
				managers[i] = m; //make sure we initialize all of the spots in the array
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void printConfig()
	{
		System.out.println(trafficGenerator.gate.iaddr + " " + trafficGenerator.gate.port);
		
		for(int i = 0; i < gates.length; i++) {
			System.out.println(gates[i]);
		}	
		
		for(int i = 0; i < managers.length; i++) {
			System.out.println(managers[i]);
		}	
	}
	
	public class ManagerInfo {
		public HostPort hostport;
		public HostPort managerPort;
		public int tokens;
		public int money;
		
		public String toString() {
			String ret = "Gate -- ";
			ret += hostport + " Number of Tokens: " + tokens + " Money: " + money;
			return ret;
		}
	}
	
	public class GateInfo {
		public HostPort hostport;
		public int tokens;
		public int money;
		
		public String toString() {
			String ret = "Manager -- ";
			ret += hostport + " Number of Tokens: " + tokens + " Money: " + money;
			return ret;
		}
	}
	
	public class TrafficGenInfo {
		public HostPort manager;
		public HostPort gate;
	}
}


