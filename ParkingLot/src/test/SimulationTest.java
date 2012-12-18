package test;

import gates.Gate;
import gates.FastAndLooseGate;
import gates.TimeBombGate;
import gates.TokenThiefGate;

import java.util.ArrayList;

import manager.Manager;
import simulation.TrafficGenerator;
import util.Config;

public class SimulationTest {

	public static final int COST_PER_TOKEN = 2;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception{
        Config c = null; 
        if(args.length > 0)
        {
            if(args[0].toLowerCase().startsWith("byz"))
            {
                c = Config.getByzantine();
            }
            else if(args[0].toLowerCase().startsWith("fail"))
            {
                c = Config.getFailure();
            }
            else if(args[0].toLowerCase().startsWith("steal"))
            {
            	c = Config.getTokenThief();
            }
        }
        else{
            c = Config.getSharedInstance();
        }
		TrafficGenerator generator = new TrafficGenerator(c.managers, 1000, "2,0.000000000275,1,-0.0000099,0,0.1");
		
		ArrayList<Gate> gates = new ArrayList<Gate>();
		ArrayList<Manager> managers = new ArrayList<Manager>();
		
		for(int i = 0; i < c.managers.length; i++) {
			managers.add(new Manager(-1, c.managers[i].money, c.managers[i].hostport.port, c.managers[i].managerPort.port));
		}
	
		
		//Startup Gates
		for(int i = 0; i < c.gates.length; i++) {
            switch(c.gates[i].type)
            {
                case Config.GateInfo.NORMAL:
                    gates.add(new Gate(10, c.gates[i].tokens, c.gates[i].money, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.GLOBAL_TRADING_POLICY, COST_PER_TOKEN));
                    break;
                case Config.GateInfo.TIME_BOMB:
                    gates.add(new TimeBombGate(10, c.gates[i].tokens, c.gates[i].money, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.GLOBAL_TRADING_POLICY, COST_PER_TOKEN));
                    break;
                case Config.GateInfo.FAST_AND_LOOSE:
                    gates.add(new FastAndLooseGate(10, c.gates[i].tokens, c.gates[i].money, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.NO_TRADING_POLICY, COST_PER_TOKEN));
                    break;
                case Config.GateInfo.TOKEN_THIEF:
                	gates.add(new TokenThiefGate(10, c.gates[i].tokens, c.gates[i].money, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.GLOBAL_TRADING_POLICY, COST_PER_TOKEN));
                	break;
            }
		}
	
	}
	
}
