package simulation;

import gates.Gate;

public class ParkingLotMain {

	/**
	 * @param args
	 */
	public static void main(String [] args) {
		TrafficGenerator tg = new TrafficGenerator(40000, "2,0.000000000275,1,-0.0000099,0,0.1");
		tg.run();
		
		Gate[] gates = new Gate[TrafficGenerator.numGates];
		
		for(int i = 0; i < tg.numGates; i++) {
			//Make Gates and attach them
		}
	}

}
