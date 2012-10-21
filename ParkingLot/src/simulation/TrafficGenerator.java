package simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.GateSubscribeMessage;
import messaging.TimeMessage;
import messaging.TimeSubscribeMessage;
import util.HostPort;
import util.MessageReceiver;
import car.Car;



public class TrafficGenerator extends MessageReceiver implements Simulation, Chronos
{
	
	public ArrayList<HostPort> subscribers;
	
	//Make parking lot a composition, so Gates communicate with the
	//Traffic Generator only. Makes stuff easier to handle/test
	ParkingLot parkLot = new ParkingLot();
	
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
			if (nextGate >= numGates){
				nextGate = numGates-1;
			}

            //wait for things to be ready.
			
			currentTime = currentTime + nextTime;
			leavingTime = currentTime + stayTime;
			
			//Make cars leave parking lot
			checkCarLeaving();
			
			//Send time to everyone
			publish();
			
			if(currentTime < simulationLength)
			{
				System.out.println("Time: " + currentTime + "\tGate: " + nextGate + "\t\tstayTime: " + stayTime + "\t\tleavingGate: " + leavingGate + "\t\tleavingTime: " + leavingTime);
				/**
				Here you should send a {massage} (MASSAGES FOR ALL) to the gate and insert the car to parking lot array (you need to implement the array).
				Remember to handle the situation that car may get reject by the gate so that it won't be in the parking lot.
				*/
				
				Date carSendDate = getCurrentTime();
				Date carLeaveDate = new Date(leavingTime*1000);
				
				/* Make a car arrival message and send it to the gate */
				CarArrivalMessage carToGateMessage = new CarArrivalMessage(carSendDate, carLeaveDate);
				
				try {
					//TODO:HardCoded
					InetAddress gateIP = InetAddress.getLocalHost();
					int gatePort = 6001;

					Socket sock = new Socket(gateIP, gatePort);
					
					OutputStream outStream = sock.getOutputStream();
					AbstractMessage.encodeMessage(outStream, carToGateMessage);
					sock.close();
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					System.err.println("Unknown Host");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/* End send car to gate message */
				
			}
			
		}
	}

	public double nextTime(double expectedValue)
	{
		return -Math.log(1 - rdm.nextDouble()) / expectedValue;
	}

    public Date getCurrentTime()
    {
		Date d = new Date();
		d.setTime(this.currentTime * 1000);
		return d;
    }

	/**Base on current time, check your parking lot array whether there is car should be leaving*/
    private void checkCarLeaving()
    {
    	
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
	public void publish()
	{
		Date d = getCurrentTime();
		TimeMessage message = new TimeMessage(d);
		for(HostPort hp : subscribers)
		{
			try 
			{
				Socket s = new Socket(hp.iaddr, hp.port);
				OutputStream o = s.getOutputStream();
				AbstractMessage.encodeMessage(o, message);
                o.close();
                s.close();
       		}
			catch(Exception e) {
        	    System.out.println("Sadddnesss");
        	}
		}
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
