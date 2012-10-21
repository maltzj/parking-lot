package test;

import gates.*;
import simulation.*;
import java.net.*;
import messaging.*;
import java.util.*;

public class GateTest extends SetupTest
{

    public void testGateSubscribe() throws Exception
    {
        for(GateImpl g: gates)
        {
  //          g.gateSubscribe(trafficGenerator.ipAddress, trafficGenerator.port);
        }

        Thread.sleep(1000);
        
        System.out.println("trafficGenerator Gates size = "+trafficGenerator.gates.size());
        System.out.println("gates.size ="+gates.size());
        assert(trafficGenerator.subscribers.size() == gates.size());

        System.out.println("We subscribed all the gates brah");

    }

		public void sendCarArrival(int gate_index) {
			GateImpl gate = gates.get(gate_index);
			CarArrivalMessage carToGateMessage = new CarArrivalMessage(new Date(), new Date());
			sendMessage(carToGateMessage, gate.ipAddress, gate.port);
		}

    public static void main(String[] args) throws Exception
    {
        GateTest test = new GateTest();
        test.setup();
    //    test.testGateSubscribe();
				test.sendCarArrival(0);
			
    }
}
