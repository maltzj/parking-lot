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
	
	/**
	 * Gets the currently active config
	 * @return A singleton, immutable, config
	 */
	public static Config getSharedInstance() {
		if(instance == null) {
			instance = new Config();
		} 
		return instance;
	}

    /** Make two of our gates fast and loose */
    public static Config getByzantine()
    {
        Config c = getSharedInstance();
        for(int i = 0; i < c.gates.length; i++){
        	c.gates[i].makeFastAndLoose();
        }
        
        return c;
    }
    
    /**
     * Creates a config with token thief gates
     * @return A config that contains GateInfo for token thieves
     */
    public static Config getTokenThief()
    {
    	Config c = getSharedInstance();
    	c.gates[0].makeTokenThief();
    	c.gates[1].makeTokenThief();
    	c.gates[2].makeTokenThief();
    	
    	return c;
    }
    

    /** Make two of our gates time bombs. */
    public static Config getFailure()
    {
        Config c = getSharedInstance();
        c.gates[0].makeTimeBomb();
        c.gates[1].makeTimeBomb();
        
        return c;
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
	
	/**
	 * Contains all the information that a manager contains
	 * 	managerPort: The specification of where other managers should connect
	 * 	hostPort: The specification of where gates should connect
	 *  tokens: the number of tokens it starts with
	 *  money: the amount of money it starts with
	 * @author Jonathan
	 *
	 */
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
	
	/**
	 * Contains all the information about a gate
	 * 	hostPort: The specification of where the gate starts
	 * 	tokens: The number of tokens a gate starts with
	 * 	money: The amount of money a gate starts with
	 * @author Jonathan
	 *
	 */
	public class GateInfo {
		public HostPort hostport;
		public int tokens;
		public int money;

        public static final int NORMAL = 0;
        public static final int TIME_BOMB = 1;
        public static final int FAST_AND_LOOSE = 2;
        public static final int TOKEN_THIEF = 3;

        public int type = NORMAL;

		public String toString() {
			String ret = "Manager -- ";
			ret += hostport + " Number of Tokens: " + tokens + " Money: " + money;
			return ret;
		}

		/**
		 * Makes it so that this gate is specified as a FastAndLooseGate
		 */
        public void makeFastAndLoose()
        {
            this.type = FAST_AND_LOOSE;
        }

        /**
         * Specifies this gate as a TimeBombGate
         */
        public void makeTimeBomb()
        {
            this.type = TIME_BOMB;
        }
        
        /**
         * Specifies this as a TokenThiefGate
         */
        public void makeTokenThief(){
        	this.type = TOKEN_THIEF;
        }
	}
	
	/**
	 * Contains all the information for networking our traffic generator.  Includes
	 * 	manager: The specification for where managers should connect
	 * 	gate: The specification of where gates should connect
	 *
	 */
	public class TrafficGenInfo {
		public HostPort manager;
		public HostPort gate;
	}
}


