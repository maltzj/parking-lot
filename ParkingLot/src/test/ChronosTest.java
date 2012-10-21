package test;
import gates.*;
import simulation.*;
import java.net.*;
import messaging.*;
public class ChronosTest extends SetupTest
{
    /** This function makes sure that you can subscribe to time updates from the Traffic Generator. */
    public void testSubscribe() throws Exception
    {
        Socket s = new Socket(trafficGenerator.ipAddress, trafficGenerator.port);

        TimeSubscribeMessage t = new TimeSubscribeMessage(InetAddress.getLocalHost(), 5000);
        AbstractMessage.encodeMessage(s.getOutputStream(), t);
        s.getOutputStream().flush();

        s.close();

        /*
        for(GateImpl g: gates)
        {
            g.subscribe(trafficGenerator.ipAddress, trafficGenerator.port);
        }
        
        System.out.println("trafficGenerator.size ="+trafficGenerator.subscribers.size());
        System.out.println("gates.size ="+gates.size());
        assert(trafficGenerator.subscribers.size() == gates.size());
        */

        System.out.println("We subscribed all the gates brah");
    }

    public static void main(String[] args) throws Exception
    {
        ChronosTest test = new ChronosTest();
        test.setup();
        test.testSubscribe();
    }
}
