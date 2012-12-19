package test;

import gates.Gate;
import gates.FastAndLooseGate;
import gates.TimeBombGate;
import gates.TokenThiefGate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import manager.Manager;
import simulation.TrafficGenerator;
import util.Config;

public class SimulationTest {

	public static final int COST_PER_TOKEN = 2;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
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
		
        int simLength = 1000;
        System.out.println("Input the length of time for the simulation defaults to 1000");
        boolean isValid = false;
        
        while(!isValid){
        	String line = reader.readLine();
        	if(line.equals("")){
        		isValid = true;
        		break;
        	}
        	else{
        		try{
        			simLength = Integer.parseInt(line);
        			isValid = true;
        			break;
        		}
        		catch(NumberFormatException e){
        			continue;
        		}
        	}
        }
        
        TrafficGenerator generator = new TrafficGenerator(c.managers, simLength, "2,0.000000000275,1,-0.0000099,0,0.1");
		
		ArrayList<Gate> gates = new ArrayList<Gate>();
		ArrayList<Manager> managers = new ArrayList<Manager>();
		
		for(int i = 0; i < c.managers.length; i++) {
			managers.add(new Manager(-1, 0, c.managers[i].hostport.port, c.managers[i].managerPort.port));
		}
	
		int amountOfMoney = readInt(reader, "How much money should gates start with? Defaults to " + c.gates[0].money, 
				c.gates[0].money);
		int tokens = readInt(reader, "How many tokens should gates start with? Defaults to " + c.gates[0].tokens, 
				c.gates[0].tokens);
		
		
		
		
		//Startup Gates
		for(int i = 0; i < c.gates.length; i++) {
            switch(c.gates[i].type)
            {
                case Config.GateInfo.NORMAL:
                    gates.add(new Gate(10, tokens, amountOfMoney, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.NO_TRADING_POLICY, COST_PER_TOKEN));
                    break;
                case Config.GateInfo.TIME_BOMB:
                    gates.add(new TimeBombGate(10,tokens, amountOfMoney, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.GLOBAL_TRADING_POLICY, COST_PER_TOKEN));
                    break;
                case Config.GateInfo.FAST_AND_LOOSE:
                    gates.add(new FastAndLooseGate(10,tokens, amountOfMoney, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.NO_TRADING_POLICY, COST_PER_TOKEN));
                    break;
                case Config.GateInfo.TOKEN_THIEF:
                	gates.add(new TokenThiefGate(10, tokens, amountOfMoney, c.gates[i].hostport.iaddr, c.gates[i].hostport.port, 10, Gate.GLOBAL_TRADING_POLICY, COST_PER_TOKEN));
                	break;
            }
		}
	
	}
	
	private static int readInt(BufferedReader reader, String prompt, int defaultVal) throws IOException{
		boolean isValid = false;
		while(!isValid){
			System.out.println(prompt);
			String line = reader.readLine();
			
			if(line.equals("")){
				return defaultVal;
			}
			
			try{
				int newVal = Integer.parseInt(line);
				return newVal;
			}
			catch(NumberFormatException e){
				continue;
			}
		}
		
		return -1;
	}
	
}
