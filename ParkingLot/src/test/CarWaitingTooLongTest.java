package test;

import gates.GateImpl;

import java.util.ArrayList;

import simulation.TrafficGenerator;
import util.Config;
import util.HostPort;

public class CarWaitingTooLongTest {
	
	 private static final int SIMULATION_LENGTH = 40000;
	    private static final String MAGIC_POLY = "0,.5";
	    //private static final String MAGIC_POLY = "2,.000000000275,1,-.0000099,0,.1";
	    /** This is the time that a car waits in the gate's queue before it leaves. */
	    private static final long TIME_TO_WAIT = 1000;
	    private static final int CASH_MONEY_TO_START = 100;
	    private static final int CASH_MONEY_PER_CAR = 100;
	    private static final int TOKENS_TO_START = 0;

	    protected TrafficGenerator trafficGenerator;
	    protected ArrayList<GateImpl> gates;

	    public void setup() throws Exception
	    {
	        try {
	            Config config = new Config();

	            this.trafficGenerator = new TrafficGenerator(SIMULATION_LENGTH, MAGIC_POLY, config.trafficGenerator.iaddr, config.trafficGenerator.port);

	            System.out.println("Created a traffic generator successfully");

	            this.gates = new ArrayList<GateImpl>();

	            for(HostPort h: config.gates)
	            {
	                GateImpl g = new GateImpl(TIME_TO_WAIT, CarWaitingTooLongTest.TOKENS_TO_START, CASH_MONEY_TO_START, h.iaddr, h.port, CASH_MONEY_PER_CAR);
	                this.gates.add(g);
	            }

	            System.out.println("Created "+config.gates.length+" gates successfully");
	        }
	        catch(Exception e) {
	            System.out.println("Sadness occurred while trying to do the thing below:");
	            e.printStackTrace();
	        }
	    }

	    public static void main(String[] args) throws Exception
	    {
	        CarWaitingTooLongTest test = new CarWaitingTooLongTest();
	        test.setup();
	    }

}
