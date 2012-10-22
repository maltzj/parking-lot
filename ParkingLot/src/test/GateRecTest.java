package test;
import simulation.TrafficGenerator;
import gates.GateImpl;
import messaging.*;
import java.util.*;
import util.Config;
import util.HostPort;
import util.Everything;
import java.net.*;
import java.io.*;
import util.MessageReceiver;
import messaging.*;
/** Tests to see if we get car arrival messages, when we gate subscribe. */

public class GateRecTest extends MessageReceiver
{
    private static final int SIMULATION_LENGTH = 100;
    private static final String MAGIC_POLY = "1,.1";
    private static final long TIME_TO_WAIT = 10;
    private static final int CASH_MONEY_TO_START = 100;

    protected TrafficGenerator trafficGenerator;
    protected ArrayList<GateImpl> gates;

    private boolean carArrived;

    public GateRecTest(InetAddress iaddr, int port) throws Exception
    {
        super(iaddr, port);
        this.carArrived = false;
    }

    public void setup() throws Exception
    {
        try {
            Config config = new Config();

            this.trafficGenerator = new TrafficGenerator(SIMULATION_LENGTH, MAGIC_POLY, config.trafficGenerator.iaddr, config.trafficGenerator.port);


            Thread trafficGenThread = new Thread(trafficGenerator);
            trafficGenThread.start();

            System.out.println("Created a traffic generator successfully");

            //subscribe to the Traffic Generator
            GateSubscribeMessage message = new GateSubscribeMessage(this.ipAddress, this.port);
            Everything.sendMessage(message, trafficGenerator.ipAddress, trafficGenerator.port);

            System.out.println("Subscribed to the Traffic Generator successfully");

            Thread.sleep(2000);

            if(this.carArrived)
            {
                System.out.println("Car Arrival was successful");
            }
            else
            {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(">>>>>>>>>>>>>>>CAR NEVER ARRIVED<<<<<<<<<<<<<<<<<");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

        }
        catch(Exception e) {
            System.out.println("Sadness occurred while trying to do the thing below:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        int RANDOM_PORT = 12345;
        GateRecTest test = new GateRecTest(InetAddress.getLocalHost(), RANDOM_PORT);
        test.setup();
    }

    public void onMessageArrived(AbstractMessage message) {
        switch(message.getMessageType())
        {
            case AbstractMessage.TYPE_CAR_ARRIVAL:
                this.carArrived = true;
                break;
            default:
                break;
        }
    }
}
