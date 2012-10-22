package test;

import gates.*;
import simulation.*;
import java.net.*;
import messaging.*;
import java.util.*;
import util.*;

public class GateTest extends SetupTest
{

    public void testGateSubscribe() throws Exception
    {
        for(GateImpl g: gates)
        {
            g.gateSubscribe(trafficGenerator.ipAddress, trafficGenerator.port);
        }

        Thread.sleep(1000);

        assert(trafficGenerator.gates.size() == gates.size());

        System.out.println("Gate Subscribed Everything");

    }

    public void sendCarArrival(int gate_index) throws Exception
    {
        GateImpl gate = gates.get(gate_index);

        int cars_before = gate.getCarsWaiting();

        CarArrivalMessage carToGateMessage = new CarArrivalMessage(new Date(), new Date());
        Everything.sendMessage(carToGateMessage, gate.ipAddress, gate.port);

        Thread.sleep(1000);

        int cars_after = gate.getCarsWaiting();

        assert( (cars_before + 1) == cars_after);

        System.out.println("Car Arrived at Gate " + gate.port);

    }

    public static void main(String[] args) throws Exception
    {
        GateTest test = new GateTest();
        test.setup();
        test.testGateSubscribe();
        test.sendCarArrival(0);
        test.sendCarArrival(0);
        test.sendCarArrival(2);
        test.sendCarArrival(5);

    }
}
