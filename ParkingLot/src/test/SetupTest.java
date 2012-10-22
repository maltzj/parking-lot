package test;
import simulation.TrafficGenerator;
import gates.GateImpl;
import messaging.*;
import java.util.*;
import util.Config;
import util.HostPort;
import java.net.*;
import java.io.*;
import util.*;
/** Tests the set up of our traffic generator. */

public class SetupTest
{
    private static final int SIMULATION_LENGTH = 100;
    private static final String MAGIC_POLY = "1,.1";
    private static final long TIME_TO_WAIT = 10;
    private static final int CASH_MONEY_TO_START = 100;

    protected TrafficGenerator trafficGenerator;
    protected ArrayList<GateImpl> gates;

    public void setup() throws Exception
    {
        try {
            Config config = new Config();

            this.trafficGenerator = new TrafficGenerator(SIMULATION_LENGTH, MAGIC_POLY, config.trafficGenerator.iaddr, config.trafficGenerator.port);

            System.out.println("Created a traffic generator successfully");

            Thread trafficGenThread = new Thread(trafficGenerator);
            trafficGenThread.start();

            Thread[] gateThreads = new Thread[config.gates.length];

            this.gates = new ArrayList<GateImpl>();

            int i=0;
            for(HostPort h: config.gates)
            {
                GateImpl g = new GateImpl(TIME_TO_WAIT, CASH_MONEY_TO_START, null, h.iaddr, h.port);
                gateThreads[i] = new Thread(g);
                gates.add(g);
                gateThreads[i].start();
                i++;
            }

            System.out.println("Created "+config.gates.length+" gates successfully");

            //TODO: Get rid of me.
            Everything.sendMessage(new GateDoneMessage(config.trafficGenerator.iaddr, config.trafficGenerator.port), config.trafficGenerator.iaddr, config.trafficGenerator.port);

        }
        catch(Exception e) {
            System.out.println("Sadness occurred while trying to do the thing below:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        SetupTest test = new SetupTest();
        test.setup();
    }
}
