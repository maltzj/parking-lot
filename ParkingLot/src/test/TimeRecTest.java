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
/** Tests the set up of our traffic generator. */

public class TimeRecTest extends MessageReceiver
{
    private static final int SIMULATION_LENGTH = 100;
    private static final String MAGIC_POLY = "1,.1";
    private static final long TIME_TO_WAIT = 10;
    private static final int CASH_MONEY_TO_START = 100;

    protected TrafficGenerator trafficGenerator;
    protected ArrayList<GateImpl> gates;

    public TimeRecTest() throws Exception
    {
        super(InetAddress.getByName("localhost"), 6001);
    }
    public void setup() throws Exception
    {
        try {
            Config config = new Config();

            this.trafficGenerator = new TrafficGenerator(SIMULATION_LENGTH, MAGIC_POLY, config.trafficGenerator.iaddr, config.trafficGenerator.port);

            System.out.println("Created a traffic generator successfully");

            Thread trafficGenThread = new Thread(trafficGenerator);
            trafficGenThread.start();

            sendMessage(new TimeSubscribeMessage(this.ipAddress, this.port), config.trafficGenerator.iaddr, config.trafficGenerator.port);
        }
        catch(Exception e) {
            System.out.println("Sadness occurred while trying to do the thing below:");
            e.printStackTrace();
        }
    }
    
    public void onTimeUpdate(TimeMessage messageFromChronos){
		
		Date newTime = messageFromChronos.getNewTime();
        System.out.println(newTime.getTime());
	}

	@Override
	public void onMessageArrived(AbstractMessage message) {
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
    }

    public void sendMessage(AbstractMessage message, InetAddress ip, int port)
    {
        try {
            Socket s = new Socket(ip, port);
            
            OutputStream o = s.getOutputStream();

            AbstractMessage.encodeMessage(o, message);
            o.flush();
            s.close();
        } catch(Exception e) {
					e.printStackTrace();
        }
    }
}
