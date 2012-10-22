package test;
import simulation.TrafficGenerator;
import gates.GateImpl;
import messaging.*;
import util.*;
import java.util.*;
import util.Config;
import util.HostPort;
import java.net.*;
import java.io.*;
import util.*;
/** Tests the set up of our traffic generator. */

public class TimeRecTest extends MessageReceiver
{
    private static final int SIMULATION_LENGTH = 40000;
    private static final String MAGIC_POLY = "2,.000000000275,1,-.0000099,0,.1";
    private static final long TIME_TO_WAIT = 10;
    private static final int CASH_MONEY_TO_START = 100;

    protected TrafficGenerator trafficGenerator;
    protected ArrayList<GateImpl> gates;

    private boolean timeReceived = false;

    public TimeRecTest() throws Exception
    {
        super(InetAddress.getByName("localhost"), 6101);
    }
    public void setup() throws Exception
    {
        try {
            Config config = new Config();

            this.trafficGenerator = new TrafficGenerator(SIMULATION_LENGTH, MAGIC_POLY, config.trafficGenerator.iaddr, config.trafficGenerator.port);

            System.out.println("Created a traffic generator successfully");

            Thread trafficGenThread = new Thread(trafficGenerator);
            trafficGenThread.start();

            Thread listeningThread = new Thread(this);
            listeningThread.start();

            Everything.sendMessage(new TimeSubscribeMessage(this.ipAddress, this.port), config.trafficGenerator.iaddr, config.trafficGenerator.port);
            Everything.sendMessage(new GateDoneMessage(this.ipAddress, this.port), config.trafficGenerator.iaddr, config.trafficGenerator.port);

            Thread.sleep(1000);

            if(this.timeReceived)
            {
                System.out.println("Time Update was received successfully");
            }
            else
            {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(">>>>>>>>>>>TIME UPDATE WAS NEVER RECEIVED <<<<<<<<<<<");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        catch(Exception e) {
            System.out.println("Sadness occurred while trying to do the thing below:");
            e.printStackTrace();
        }
    }
    
    public void onTimeUpdate(TimeMessage messageFromChronos){
        this.timeReceived = true;	
		Date newTime = messageFromChronos.getNewTime();
        System.out.println(newTime.getTime() / 1000);
	}

	@Override
	public void onMessageArrived(AbstractMessage message) {
        System.out.println(message.getMessageType() + "adslfkldfaj ");
		switch(message.getMessageType())
		{
			/*case AbstractMessage.TYPE_CAR_ARRIVAL:
			{
				this.onCarArrived((CarArrivalMessage) message);
				break;
			}*/
			case AbstractMessage.TYPE_TIME_MESSAGE:
			{
				this.onTimeUpdate((TimeMessage) message);
				break;
			}
			default:
			{
				//Do something
			}
		}
	}
    public static void main(String[] args) throws Exception
    {
        TimeRecTest test = new TimeRecTest();
        test.setup();
        System.exit(0);
    }
}
