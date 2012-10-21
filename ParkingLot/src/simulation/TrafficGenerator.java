package simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import messaging.AbstractMessage;
import messaging.GateSubscribeMessage;
import messaging.TimeSubscribeMessage;
import util.MessageReceiver;
import car.Car;



public class TrafficGenerator extends MessageReceiver implements Simulation, Chronos
{
	
	//Make parking lot a composition, so Gates communicate with the
	//Traffic Generator only. Makes stuff easier to handle/test
	ParkingLot parkLot = new ParkingLot();
	
	//List<MessageReceiver> subscribedTimeElements = new ArrayList<MessageReceiver>();
	//List<CarReceiver> subscribedGates = new ArrayList<CarReceiver>();
	Date timeFromStart = new Date();
	
	private int currentTime;
	private int simulationLength;
	private Polynomial nextTimePolynomial;
	private Random rdm;
	public static int numGates = 6;
	
	public TrafficGenerator(int simLen, String nextTimePoly, InetAddress address, int port) throws Exception
	{
        super(address, port);
		currentTime = 0;
		simulationLength = simLen;
		nextTimePolynomial = new Polynomial(nextTimePoly);
		rdm = new Random();
		subscribers = new ArrayList<HostPort>();
	}

	public void run()
	{
		/**
			You may want to wait for a signal here, instead of start sending a car right away.
			And maybe you want to hard code all your six gates' IP and port number here. That depends your implementation.
		*/
		
		int nextTime;
		int stayTime;
		int nextGate;
		int leavingGate;
		int leavingTime;
		
		while(currentTime < simulationLength)
		{
			nextTime = (int)nextTime(nextTimePolynomial.evaluate(currentTime));
			stayTime = (int)(Math.abs(rdm.nextGaussian() * ( simulationLength - currentTime )/4 + (simulationLength - currentTime)/2));
			nextGate = (int)(rdm.nextDouble() * ( numGates + 2 ));
			leavingGate = (int)(rdm.nextDouble() * (numGates - 1) );

            //wait for things to be ready.

			
			currentTime = currentTime + nextTime;
			leavingTime = currentTime + stayTime;
			checkCarLeaving();
			if(currentTime < simulationLength)
			{
				System.out.println("Time: " + currentTime + "\tGate: " + nextGate + "\t\tstayTime: " + stayTime + "\t\tleavingGate: " + leavingGate + "\t\tleavingTime: " + leavingTime);
				/**
				Here you should send a {massage} (LOL MALTZ) to the gate and insert the car to parking lot array (you need to implement the array).
				Remember to handle the situation that car may get reject by the gate so that it won't be in the parking lot.
				*/
				
				
				
			}
			
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(timeFromStart);
			startCal.add(Calendar.SECOND, currentTime);
			
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(timeFromStart);
			endCal.add(Calendar.SECOND, leavingTime);
			
		}
	}

	public double nextTime(double expectedValue)
	{
		return -Math.log(1 - rdm.nextDouble()) / expectedValue;
	}

    public long getCurrentTime()
    {
		return this.currentTime;
    }

    private void checkCarLeaving()
    {
	/**Base on current time, check your parking lot array whether there is car should be leaving*/
    }
    
    private void notifySubscribers()
    {
	/**Iterate over the subscribers and send each of them the current time*/
    }
    
	@Override
	public void onMessageArrived(AbstractMessage message) {
		switch(message.getMessageType())
		{
			case AbstractMessage.TYPE_TIME_SUBSCRIBE:
			{
				this.onSubscribeReceived((TimeSubscribeMessage) message);
				break;
			}
		}
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCarGenerated(Car newestCar) {
	}

	@Override
	public void onGateSubscribe(GateSubscribeMessage gateSubscribing)
			throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSubscribeReceived(TimeSubscribeMessage messageReceived) {
		subscribers.add(new HostPort(messageReceived.getAddressSubscribing(), messageReceived.getPortSubscribingOn()));
	}

	/**You don't need to change the rest of code*/
	private class Polynomial
	{
		private ArrayList<Integer> exponent;
		private ArrayList<Double> coefficient; 		
		public Polynomial(String str)
		{
			createPolynomial(str);
		}

		private void createPolynomial(String poly)
		{
			int i;
			int exp;
			double coeff;
	
			i = poly.indexOf(',');
			exp = Integer.parseInt(poly.substring(0,i));
			poly = poly.substring(i+1);
			i = poly.indexOf(',');
			if(i == -1)
			{
				coeff = Double.parseDouble(poly);
				poly = "";
			}
			else
			{
				coeff = Double.parseDouble(poly.substring(0,i));
				poly = poly.substring(i+1);
			}

			exponent = new ArrayList<Integer>();
			coefficient = new ArrayList<Double>();
			exponent.add(exp);
			coefficient.add(coeff);

			while(!poly.equals(""))
			{
				i = poly.indexOf(',');
				exp = Integer.parseInt(poly.substring(0,i));
				poly = poly.substring(i+1);
				i = poly.indexOf(',');
				if(i == -1)
				{
					coeff = Double.parseDouble(poly);
					poly = "";
				}
				else
				{
					coeff = Double.parseDouble(poly.substring(0,i));
					poly = poly.substring(i+1);
				}
				exponent.add(exp);
				coefficient.add(coeff);
			}	
		}	
		
		public double evaluate(double x)
		{
			double sum = 0;
			for(int i = 0; i < exponent.size(); i++)
			{
				sum = sum + coefficient.get(i) * Math.pow(x, exponent.get(i));
			}
			return sum;
		}
	}

}
