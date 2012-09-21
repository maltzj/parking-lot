import java.util.Random;

public class TrafficGenerator
{
	private int currentTime;
	private int simulationLength;
	private double [] nextTimePolynomial;
	private double [] waitTimePolynomial;
	private Random rdm;
	private static int numGates = 6;
	//nextTimePoly is a function of time that returns expected value for generating the next time, waitTimePoly is similar but for car wait time
	public TrafficGenerator(int simLen, String nextTimePoly, String waitTimePoly)
	{
		currentTime = 0;
		simulationLength = simLen;
		nextTimePolynomial = createPolynomial(nextTimePoly);
		waitTimePolynomial = createPolynomial(waitTimePoly);
		rdm = new Random();
	}
	public void run()
	{
		int nextTime;
		int waitTime;
		int nextGate;
		while(currentTime < simulationLength)
		{
			nextTime = (int)nextTime(evaluatePolynomial(nextTimePolynomial, currentTime));
			waitTime = (int)nextTime(evaluatePolynomial(waitTimePolynomial, currentTime));
			nextGate = (int)(rdm.nextDouble() * numGates);
			
			currentTime = currentTime + nextTime;
			if(currentTime < simulationLength)
			{
				if(currentTime < 10)
				{
					System.out.println("Time: " + currentTime + "\t\tGate: " + nextGate + "\t\tWaitTime: " + waitTime);
				}
				else
				{
					System.out.println("Time: " + currentTime + "\tGate: " + nextGate + "\t\tWaitTime: " + waitTime);
				}
			}
		}
	}
	//create polynomial must start with the highest exponent first and must be in format specific 
	//format: Exponent,Coefficient,Exponent,Coefficient "x^2 + .5x + 5" = 2,1,1,.5,0,5  
	public double [] createPolynomial(String poly)
	{
		double [] p;
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

		p = new double [exp+1];
		for(int j = 0; j < p.length; j++)
		{
			p[j] = 0;
		}
		p[exp] = coeff;

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
			p[exp] = coeff;	
		}	
		return p;
	}
	public double evaluatePolynomial(double [] poly, double x)
	{
		double sum = 0;
		for(int i = 0; i < poly.length; i++)
		{
			if(poly[i] != 0)
			{
				sum = sum + poly[i] * Math.pow(x, i);
			}
		}
		return sum;
	}
	public double nextTime(double expectedValue)
	{
		return -Math.log(1 - rdm.nextDouble()) / expectedValue;
	}
	public static void main(String [] args)
	{
		if(args.length != 3)
		{
			System.out.println("usage: java TrafficGenerator <simulation lenght (seconds)> <P(t) = y where y is expected value for the next time> <P(t) = y where y is the expected value for wait time>");
			System.out.println("Example: P(t) = t^2 + 3t + 5 would be 2,1,1,3,0,5 such that the sequence is Exponent, Coeefficient with the largest exponent first");
			System.exit(0);
		}
		//TrafficGenerator gt = new TrafficGenerator(200, "0,.1", "2,.0000099,1,-.00198,0,.1");
		TrafficGenerator gt = new TrafficGenerator(Integer.parseInt(args[0]),args[1],args[2]);
		gt.run();
	}
}
