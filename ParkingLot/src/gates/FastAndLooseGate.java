package gates;
import java.net.*;
import messaging.*;
import car.Car;
import java.util.Date;

/** This gate will send tokens to the parking lot regardless of how many tokens it has. */

 public class FastAndLooseGate extends Gate {
     public FastAndLooseGate(long timeToWait, int tokensToStartWith, int moneyToStartWith, InetAddress addr, int port, int moneyPerCarPassed, int tradingPolicy) throws Exception
     {
         super(timeToWait, tokensToStartWith, moneyToStartWith, addr, port, moneyPerCarPassed, tradingPolicy);
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
