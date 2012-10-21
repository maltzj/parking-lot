package test;
import gates.*;
import simulation.*;
import java.net.*;
public class ChronosTest extends SetupTest
{
    /** This function makes sure that you can subscribe to time updates from the Traffic Generator. */
    public void testSubscribe()
    {
        for(GateImpl g: gates)
        {
            g.subscribe(trafficGenerator.ipAddress, trafficGenerator.port);
        }
        
        assert(trafficGenerator.subscribers.size() == gates.size());

        System.out.println("We subscribed all the gates brah");
    }

    public static void main(String[] args)
    {
        ChronosTest test = new ChronosTest();
        test.setup();
        test.testSubscribe();
    }
}
