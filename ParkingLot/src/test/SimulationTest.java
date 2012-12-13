package test;

import gates.Gate;

import java.util.ArrayList;

import manager.Manager;
import simulation.TrafficGenerator;
import util.Config;

public class SimulationTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception{
		Config c = Config.getSharedInstance();
		TrafficGenerator generator = new TrafficGenerator(c.managers, 1000, "2,0.000000000275,1,-0.0000099,0,0.1");
		
		ArrayList<Gate> gates = new ArrayList<Gate>();
		ArrayList<Manager> managers = new ArrayList<Manager>();
		
		//Startup Gates
		for(int i = 0; i < c.gates.length; i++) {
			gates.add(new Gate(10, c.gates[i].tokens, c.gates[i].money, c.gates[i].hostport.iaddr,
					c.gates[i].hostport.port, 10, Gate.GLOBAL_TRADING_POLICY));
		}
		
		for(int i = 0; i < c.managers.length; i++) {
			managers.add(new Manager(-1, c.managers[i].money, c.managers[i].hostport.port, c.managers[i].managerPort.port));
		}
		
		
	}
	
}
