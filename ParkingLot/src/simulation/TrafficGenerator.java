package simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.*;
import test.*;

import messaging.*;
import util.HostPort;
import util.MessageReceiver;
import car.Car;



public class TrafficGenerator extends MessageReceiver implements Chronos
{
	
	public ArrayList<HostPort> timeSubscribers;
	public ArrayList<HostPort> gates;

    public ArrayList<Car> parkingLot;

	
	Date timeFromStart = new Date();
	
	private int currentTime;
	private int simulationLength;
	private Polynomial nextTimePolynomial;
	private Random rdm;
    private int numGatesDone;
	public static int numGates = 6;
    private int distributeType = 0;

    //Putting this here because we generate a car before advancing time.
    private int stayTime = 0;
	
	public static boolean tokenTradingStepComplete = false;
	
	Map<HostPort, Integer> hostPortToTokensMap = new HashMap<HostPort, Integer>();
	Map<HostPort, Integer> hostPortToMoneyMap = new HashMap<HostPort, Integer>();

	public TrafficGenerator(int simLen, String nextTimePoly, InetAddress address, int port) throws Exception
	{
        super(address, port);
        numGatesDone = 0;
		currentTime = 0;
		simulationLength = simLen;
		nextTimePolynomial = new Polynomial(nextTimePoly);
		rdm = new Random();
		timeSubscribers = new ArrayList<HostPort>();
		gates = new ArrayList<HostPort>();
        parkingLot = new ArrayList<Car>();

        //Create thread for listening on socket.
        Thread listeningThread = new Thread(this);
        listeningThread.start();
	}


	public void step() throws IOException
	{
		/**
			You may want to wait for a signal here, instead of start sending a car right away.
			And maybe you want to hard code all your six gates' IP and port number here. That depends your implementation.
		*/
		
		int nextTime;
		int leavingTime;

        try{
            Thread.sleep(10);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("FUCK JHAVA");
        }

        nextTime = (int)nextTime(nextTimePolynomial.evaluate(currentTime));
        stayTime = (int)(Math.abs(rdm.nextGaussian() * ( simulationLength - currentTime )/4 + (simulationLength - currentTime)/2));
        stayTime = 100;

        //wait for things to be ready.

        currentTime = currentTime + nextTime;

        //Make cars leave parking lot
        checkCarLeaving();
        tokenTradingStepComplete = false;
        askForTokens();
   }


    public void generateCar()
    {

        int nextGate, leavingGate;

        nextGate = (int)(rdm.nextDouble() * ( numGates + 2 ));
        leavingGate = (int)(rdm.nextDouble() * numGates );
        if (nextGate >= numGates){
            nextGate = numGates-1;
        }

        int leavingTime = stayTime + currentTime;

        if(currentTime < simulationLength)
        {
            System.out.println("Generated a car: Time: " + currentTime + "\tGate: " + nextGate + "\t\tstayTime: " + stayTime + "\t\tleavingGate: " + leavingGate + "\t\tleavingTime: " + leavingTime);
            /**
              Here you should send a {massage} (MASSAGES FOR ALL) to the gate and insert the car to parking lot array (you need to implement the array).
              Remember to handle the situation that car may get reject by the gate so that it won't be in the parking lot.
              */

            //publishCar

            Date carSendDate = getCurrentTime();
            Date carLeaveDate = new Date(leavingTime*1000);

            /* Make a car arrival message and send it to the gate */
            CarArrivalMessage carToGateMessage = new CarArrivalMessage(carSendDate, carLeaveDate);

            Socket sock = null;
            try {
                HostPort gateHP = gates.get(nextGate);
                InetAddress gateIP = gateHP.iaddr;
                int gatePort = gateHP.port;
                System.out.println("Trying to send a car to port "+gatePort);
                sock = new Socket();
                sock.setReuseAddress(true);
                
                sock.connect(new InetSocketAddress(gateIP, gatePort));

                OutputStream outStream = sock.getOutputStream();
                AbstractMessage.encodeMessage(outStream, carToGateMessage);
                outStream.close();
                sock.close();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                System.err.println("Unknown Host");
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* End send car to gate message */

            publishTime();

        } else {
            //TODO: Send shutdown messages to everyone.
            killAllDashNine();
            
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

        ArrayList<Car> toRemove = new ArrayList<Car>();

        Date currentTime = getCurrentTime();
        for(Car c: parkingLot)
        {
            if (currentTime.compareTo(c.getTimeDeparts())  >= 0)
            {
                //generate random gate
                int gate = (int) (this.rdm.nextDouble()*numGates);

                HostPort h = gates.get(gate);

                sendTokenMessage(h.iaddr, h.port);
                
                toRemove.add(c);
            }
        }

        for(Car c: toRemove)
        {
            parkingLot.remove(c);
        }
    }
    
    private void notifySubscribers()
    {
	/**Iterate over the timeSubscribers and send each of them the current time*/
    }

    private void sendTokenMessage(InetAddress ip, int port)
    {
        //TODO: Perhaps, we need to send money here.
        TokenMessage message = new TokenMessage(1);
        try 
        {
            Socket s = new Socket();
            s.setReuseAddress(true);
            s.connect(new InetSocketAddress(ip, port));

            OutputStream o = s.getOutputStream();
            AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    
	@Override
	public void onMessageArrived(AbstractMessage message) throws IOException {
		switch(message.getMessageType())
		{
			case AbstractMessage.TYPE_TIME_SUBSCRIBE:
			{
				this.onTimeSubscribeReceived((TimeSubscribeMessage) message);
				break;
			}
			case AbstractMessage.TYPE_GATE_SUBSCRIBE:
			{
				this.onGateSubscribe((GateSubscribeMessage) message);
				break;
			}
            case AbstractMessage.TYPE_GATE_DONE:
			{
				this.onGateDone((GateDoneMessage) message);
				break;
			}
            case AbstractMessage.TYPE_CAR_ARRIVAL:
			{
				this.onCarArrived((CarArrivalMessage) message);
				break;
			}
            case AbstractMessage.TYPE_TOKEN_AMOUNT_MESSAGE:
            {
            	this.onTokenAmountArrived((TokenAmountMessage) message);
            	break;
            }
            
            case AbstractMessage.TYPE_MONEY_AMOUNT_MESSAGE:
            {
            	this.onMoneyAmountArrived((MoneyAmountMessage) message);
            }

		}
		
	}

	private void askForMoney(){
		SimpleMessage message = new SimpleMessage(AbstractMessage.TYPE_MONEY_QUERY_MESSAGE);
		for(HostPort hp : timeSubscribers)
		{
			try 
			{
				Socket s = new Socket();
                s.setReuseAddress(true);
                s.connect(new InetSocketAddress(hp.iaddr, hp.port));

				OutputStream o = s.getOutputStream();
				AbstractMessage.encodeMessage(o, message);
                o.close();
                s.close();
       		}
			catch(Exception e) {
                e.printStackTrace();
        	}
		}
        //once you've received all the tokens and money.
	}
	
    private void onMoneyAmountArrived(MoneyAmountMessage message) {
		HostPort hostPort = new HostPort(message.getIpAddress(), message.getPort());
		this.hostPortToMoneyMap.put(hostPort, new Integer(message.getAmountOfMoney()));
		
		if(this.hostPortToMoneyMap.keySet().size() == this.numGates)
		{
			//create the redistribution method
            switch(distributeType)
            {
                case 0:
                {
                    doNotDistribute();
                    break;
                }
                case 1:
                {
                    distributeEqually();
                    break;
                }
                case 2:
                {
                    scaleProfit();
                    break;
                }
            }
            hostPortToMoneyMap.clear();
            hostPortToTokensMap.clear(); 
            generateCar();
		}	
	}

   public void doNotDistribute()
   {
        int tokens = 0;
        int money = 0;
        for(HostPort hp : gates)
        {
            tokens = hostPortToTokensMap.get(hp);            
            money = hostPortToMoneyMap.get(hp);            
            sendMoney(hp, money);
            sendTokens(hp, tokens);
            System.out.println(hp.port+" has $"+money);
        }

   }

   public void distributeEqually()
   { 
       int totalTokens = 0;
       int numLeft = numGates;
       int money = 0;

       for(HostPort hp: gates)
       {
            totalTokens += hostPortToTokensMap.get(hp);            
       }


       for(HostPort hp: gates)
       {
           money = hostPortToMoneyMap.get(hp);            
           //send the money as is.
           sendMoney(hp, money);

           //send the tokens equally distributed.
           int sendingTokens = totalTokens/numLeft--;
           sendTokens(hp, sendingTokens);
           totalTokens -= sendingTokens;
       }
   }

   /** Ensure that all gates have atleast one token.
    * This will ensure that a car can always pass through a gate unless we reach the case where each gate has one token.
    */
   public void scaleProfit()
   {
       List<HostPort> buyers = new ArrayList<HostPort>();
    
       for(HostPort hp: gates)
       {
           int tokens = hostPortToTokensMap.get(hp);
           if(tokens == 0)
           {
               buyers.add(hp);
           }
       }

       for(HostPort buyer: buyers)
       {
           int buyerCashMoney = hostPortToMoneyMap.get(buyer);

           //if the buyer has enough money to buy a token, buy one token.
           if(buyerCashMoney > SetupTest.CASH_MONEY_PER_TOKEN)
           {
               for(HostPort seller: gates)
               {
                   int sellerTokens = hostPortToTokensMap.get(seller);
                   int sellerCashMoney = hostPortToMoneyMap.get(seller);

                   if(sellerTokens > 1)
                   {
                       hostPortToTokensMap.put(seller, sellerTokens - 1);
                       hostPortToMoneyMap.put(seller, sellerCashMoney + SetupTest.CASH_MONEY_PER_TOKEN);

                       hostPortToTokensMap.put(buyer, 1);
                       hostPortToMoneyMap.put(buyer, buyerCashMoney - SetupTest.CASH_MONEY_PER_TOKEN);

                       break;
                   }
               }
           }
       }


       doNotDistribute();
   }

   public void sendMoney(HostPort hp, int money)
   {
        MoneyMessage message = new MoneyMessage(money);
    	try 
		{
            Socket s = new Socket();
            s.setReuseAddress(true);
            s.connect(new InetSocketAddress(hp.iaddr, hp.port));

			OutputStream o = s.getOutputStream();
			AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
       	}
		catch(Exception e) {
            e.printStackTrace();
        }
   }
   public void sendTokens(HostPort hp, int tokens)
   {
        TokenMessage message = new TokenMessage(tokens);
    	try 
		{
            Socket s = new Socket();
            s.setReuseAddress(true);
            s.connect(new InetSocketAddress(hp.iaddr, hp.port));

			OutputStream o = s.getOutputStream();
			AbstractMessage.encodeMessage(o, message);
            o.close();
            s.close();
       	}
		catch(Exception e) {
            e.printStackTrace();
        }
   }
    
	private void askForTokens() {
		SimpleMessage message = new SimpleMessage(AbstractMessage.TYPE_TOKEN_QUERY_MESSAGE);
		for(HostPort hp : timeSubscribers)
		{
			try 
			{
                Socket s = new Socket();
                s.setReuseAddress(true);
                s.connect(new InetSocketAddress(hp.iaddr, hp.port));

				OutputStream o = s.getOutputStream();
				AbstractMessage.encodeMessage(o, message);
                o.close();
                s.close();
       		}
			catch(Exception e) {
                e.printStackTrace();
        	}
		}
	}
	
	private void onTokenAmountArrived(TokenAmountMessage message) {
    	HostPort hostPort = new HostPort(message.getIpAddress(), message.getPort());
		this.hostPortToTokensMap.put(hostPort, new Integer(message.getNumberOfTokens()));
		
		if(this.hostPortToTokensMap.keySet().size() == this.numGates)
		{
			askForMoney();
		}
	}
    
    
	/** This gets called when a car is sent to the parking lot.
     * It will add stuff to our arraylist, and then check to see if cars need to leave every timestep.
     */
    public void onCarArrived(CarArrivalMessage message)
    {
        System.out.println("TrafficGenerator got a car leaving at "+message.getCarReturnTime());

        parkingLot.add(new Car(message.getCarSentTime(), message.getCarReturnTime()));
    }

	
    public void onGateDone(GateDoneMessage message) throws IOException
    {
        numGatesDone++;
        if(numGatesDone == numGates)
        {
            numGatesDone = 0;
            step();
        }
    }
	public void onGateSubscribe(GateSubscribeMessage gateSubscribing) {
		gates.add(new HostPort(gateSubscribing.getAddressOfGate(),gateSubscribing.getPort()));
	}
	
	public void onTimeSubscribeReceived(TimeSubscribeMessage messageReceived) {
        System.out.println("Received a subscribe from "+messageReceived.getPortSubscribingOn());
		timeSubscribers.add(new HostPort(messageReceived.getAddressSubscribing(), messageReceived.getPortSubscribingOn()));
	}

	public void publishTime()
	{
		Date d = getCurrentTime();
        System.out.println("TrafficGenerator: Publishing Time "+d);
		TimeMessage message = new TimeMessage(d);
		for(HostPort hp : timeSubscribers)
		{
			try 
			{
                Socket s = new Socket();
                s.setReuseAddress(true);
                s.connect(new InetSocketAddress(hp.iaddr, hp.port));

				OutputStream o = s.getOutputStream();
				AbstractMessage.encodeMessage(o, message);
                o.close();
                s.close();
       		}
			catch(Exception e) {
                e.printStackTrace();
        	}
		}
	}

    public void killAllDashNine()
    {
		Date d = getCurrentTime();
        SimpleMessage message = new SimpleMessage(AbstractMessage.TYPE_CLOSE_CONNECTION);
		for(HostPort hp : timeSubscribers)
		{
			try 
			{
                System.out.println("Killing "+hp.iaddr+":"+hp.port);
                Socket s = new Socket();
                s.setReuseAddress(true);
                s.connect(new InetSocketAddress(hp.iaddr, hp.port));

				OutputStream o = s.getOutputStream();
				AbstractMessage.encodeMessage(o, message);
                o.close();
                s.close();
       		}
			catch(Exception e) {
                e.printStackTrace();
        	}
		}
        System.out.println("ParkingLot has "+parkingLot.size()+" cars.");
        this.die = true;
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
