package test;

import gates.Gate;
import gates.GateImpl;
import simulation.TrafficGenerator;
import util.Config;

public class SimulationTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception{
		Config c = new Config();
		TrafficGenerator generator = new TrafficGenerator(1000,"2,.000000000275,1,-.0000099,0,.1", c.trafficGenerator.iaddr, c.trafficGenerator.port);
		Gate gate = new GateImpl(10, 10, 100, c.gates[0].iaddr, c.gates[0].port, 10);
		Gate gateTwo = new GateImpl(10, 10, 100, c.gates[1].iaddr, c.gates[1].port, 10);
		
		//start up managers
	}
	
	
}
