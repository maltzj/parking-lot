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
		TrafficGenerator generator = new TrafficGenerator();
		
		ArrayList<Gate> gates = new ArrayList<Gate>();
		ArrayList<Manager> managers = new ArrayList<Manager>();
		
		//Startup Gates
		for(int i = 0; i < c.gates.length; i++) {
			gates.add(new Gate(10, c.gates[i].tokens, c.gates[i].money, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10));
		}
		
		for(int i = 0; i < c.managers.length; i++) {
			managers.add(new Manager(c.managers[i].tokens, c.managers[i].money, c.managers[i].hostport.port));
		}
		
		
	}
	
}
