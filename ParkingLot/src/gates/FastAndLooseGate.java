package gates;
import java.net.*;
import messaging.*;
import car.Car;
import java.util.Date;

/** This gate will send tokens to the parking lot regardless of how many tokens it has. */

 public class FastAndLooseGate extends Gate {
    
	 /**
     * Creates a gate in the exact same way as its super class 
     * @param timeToWait The time cars should wait before leaving
     * @param tokensToStartWith The number of tokens the gate starts with
     * @param moneyToStartWith The amount of money the gate starts with
     * @param addr The address the gate is listening on
     * @param port The port the gate is listening on
     * @param moneyPerCarPassed The amount of money the gate gains per car passed
     * @param tradingPolicy The type of trading policy the gate will used
     * @param costPerToken The cost of a token to trade for
     * @throws Exception
     */
	 public FastAndLooseGate(long timeToWait, int tokensToStartWith, int moneyToStartWith, 
    		 InetAddress addr, int port, int moneyPerCarPassed, int tradingPolicy, int costPerToken) throws Exception
     {
         super(timeToWait, tokensToStartWith, moneyToStartWith, addr, port, moneyPerCarPassed, tradingPolicy, costPerToken);
     }

     //override
     public void onCarArrived(CarArrivalMessage arrival)
     {
		Car carToQueue = new Car(arrival.getCarSentTime(), arrival.getCarReturnTime());
		
		//Add Car to queue
		long timeArrived = arrival.getCarSentTime().getTime();
		long leavingTime = timeArrived + amountOfTimeToWait;

		Date timeToLeave = new Date();
		timeToLeave.setTime(leavingTime);
		CarWrapper carWrapper = new CarWrapper(carToQueue, timeToLeave);
		waitingCars.add(carWrapper);

        //Let em all in
		while(this.waitingCars.size() > 0){
			CarWrapper c = this.waitingCars.poll();
			this.sendCarToParkingLot(c);
		}
		
		this.checkTokenStatus();
     }
 }
