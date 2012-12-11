package test;
import gates.Gate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import simulation.TrafficGenerator;
import util.Config;
import util.HostPort;
/** Tests the set up of our traffic generator. */

public class RunSimulation
{
    private static int SIMULATION_LENGTH = 4000;
    private  static String MAGIC_POLY = "2,.000000000275,1,-.0000099,0,.1";
    /** This is the time that a car waits in the gate's queue before it leaves. */
    private static long TIME_TO_WAIT = 100;
    private static int CASH_MONEY_TO_START = 100;
    public static int CASH_MONEY_PER_CAR = 5;
    public static int CASH_MONEY_PER_TOKEN = 3;
    private  static int TOKENS_TO_START = 10;


    protected TrafficGenerator trafficGenerator;
    protected ArrayList<Gate> gates;

    public void setup() throws Exception
    {
        try {
            Config config = Config.getSharedInstance();

            //this.trafficGenerator = new TrafficGenerator(SIMULATION_LENGTH, MAGIC_POLY, config.trafficGenerator.gate.iaddr, config.trafficGenerator.gate.port);

            System.out.println("Created a traffic generator successfully");

            this.gates = new ArrayList<Gate>();

            /*for(HostPort h: config.gates)
            {
                Gate g = new Gate(TIME_TO_WAIT, TOKENS_TO_START, CASH_MONEY_TO_START,  h.iaddr, h.port, CASH_MONEY_PER_CAR);
                this.gates.add(g);
            }*/

            System.out.println("Created "+config.gates.length+" gates successfully");
        }
        catch(Exception e) {
            System.out.println("Sadness occurred while trying to do the thing below:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        RunSimulation test = new RunSimulation();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
    	
    	while(true)
    		{
    			System.out.print("Please input the length of the simulation in seconds (default 4000): ");
    			String simulationLength = reader.readLine();
    	
    			if(!simulationLength.equals(""))
    			{
    				try
    				{
    					test.setSIMULATION_LENGTH(Integer.parseInt(simulationLength));
    					break;
    				}
    				catch(NumberFormatException e)
    				{
    					System.out.println("Sorry, please enter a number ");
    				}
    			}
    			else
    				break;
    		}
    	
    	while(true)
    	{
    		System.out.print("Please input the time of the cars to wait in seconds(defaults to 100): ");
    		String timeToWait = reader.readLine();
    		
    		if(!timeToWait.equals(""))
    		{
    			try
    			{
    				test.setTIME_TO_WAIT(Integer.parseInt(timeToWait));
    				break;
    			}
    			catch(NumberFormatException e)
    			{
    				System.out.println("Sorry, please enter a number ");
    			}
    		}
    		else
    			break;
    	}
    	
    	
    	while(true)
    	{
    		System.out.print("Please input the amount of money a car gets from passing a car through (defaults to 5): ");
    		String moneyPerCar = reader.readLine();
    		if(!moneyPerCar.equals(""))
    		{
    			try
    			{
    				test.setCASH_MONEY_PER_CAR(Integer.parseInt(moneyPerCar));
    				break;
    			}
    			catch(NumberFormatException e)
    			{
    				System.out.println("Sorry, please enter a number or the empty string ");
    			}
    		}
    		else
    			break;
    	}
    	
    	while(true)
    	{
    		System.out.print("Please input the amount of money a token costs (defaults to 3): ");
    		String costPerToken = reader.readLine();
    		if(!costPerToken.equals(""))
    		{
    			try
    			{
    				test.setCASH_MONEY_PER_TOKEN(Integer.parseInt(costPerToken));
    				break;
    			}
    			catch(NumberFormatException e)
    			{
    				System.out.println("Sorry, please enter a number or the empty string");
    			}
    		}
    		else
    			break;
    	}
    	
    	while(true)
    	{
    		System.out.print("Please input the total number of tokens per gate (defaults to 22): ");
    		String numberOfTokens = reader.readLine();
    		if(!numberOfTokens.equals(""))
    		{
    			try
    			{
    				test.setTOKENS_TO_START(Integer.parseInt(numberOfTokens));
    				break;
    			}
    			catch(NumberFormatException e)
    			{
    				System.out.println("Sorry, please enter a number or the empty string");
    			}
    		}
    		else
    			break;
    	}
    	
    	System.out.print("Please enter the polynomial which will represent the distribution function (Defaults to " + test.getMAGIC_POLY() + ")");
    	String poly = reader.readLine();
    	if(!poly.equals(""))
    		test.setMAGIC_POLY(poly);
    	
    	
        test.setup();
    }

	public  int getSIMULATION_LENGTH() {
		return SIMULATION_LENGTH;
	}

	public  void setSIMULATION_LENGTH(int sIMULATION_LENGTH) {
		SIMULATION_LENGTH = sIMULATION_LENGTH;
	}

	public  String getMAGIC_POLY() {
		return MAGIC_POLY;
	}

	public  void setMAGIC_POLY(String mAGIC_POLY) {
		MAGIC_POLY = mAGIC_POLY;
	}

	public  long getTIME_TO_WAIT() {
		return TIME_TO_WAIT;
	}

	public  void setTIME_TO_WAIT(long tIME_TO_WAIT) {
		TIME_TO_WAIT = tIME_TO_WAIT;
	}

	public  int getCASH_MONEY_TO_START() {
		return CASH_MONEY_TO_START;
	}

	public  void setCASH_MONEY_TO_START(int cASH_MONEY_TO_START) {
		CASH_MONEY_TO_START = cASH_MONEY_TO_START;
	}

	public  int getCASH_MONEY_PER_CAR() {
		return CASH_MONEY_PER_CAR;
	}

	public  void setCASH_MONEY_PER_CAR(int cASH_MONEY_PER_CAR) {
		CASH_MONEY_PER_CAR = cASH_MONEY_PER_CAR;
	}

	public  int getCASH_MONEY_PER_TOKEN() {
		return CASH_MONEY_PER_TOKEN;
	}

	public  void setCASH_MONEY_PER_TOKEN(int cASH_MONEY_PER_TOKEN) {
		CASH_MONEY_PER_TOKEN = cASH_MONEY_PER_TOKEN;
	}

	public  int getTOKENS_TO_START() {
		return TOKENS_TO_START;
	}

	public  void setTOKENS_TO_START(int tOKENS_TO_START) {
		TOKENS_TO_START = tOKENS_TO_START;
	}

	public TrafficGenerator getTrafficGenerator() {
		return trafficGenerator;
	}

	public void setTrafficGenerator(TrafficGenerator trafficGenerator) {
		this.trafficGenerator = trafficGenerator;
	}

	public ArrayList<Gate> getGates() {
		return gates;
	}

	public void setGates(ArrayList<Gate> gates) {
		this.gates = gates;
	}
    
    
}
