package gates;
import java.net.*;
import util.MessageHandler;
import messaging.*;
import java.util.Date;


/**
 * TimeBombGate is an extension of Gate which disconnects from its manager after a given amount of time
 *
 */
public class TimeBombGate extends Gate implements MessageHandler
{
	
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
    public TimeBombGate(long timeToWait, int tokensToStartWith, int moneyToStartWith, InetAddress addr, 
    		int port, int moneyPerCarPassed, int tradingPolicy, int costPerToken) throws Exception
    {
        super(timeToWait, tokensToStartWith, moneyToStartWith, addr, port, moneyPerCarPassed, tradingPolicy, costPerToken);
    	System.out.println("THIS IS A TIME BOMB GATE");
    }

    public void onTimeUpdate(TimeMessage messageFromChronos)
    {
        Date time = messageFromChronos.getNewTime();
        Date tmptime = new Date(500 * 1000);

        if(time.getTime() > tmptime.getTime())
        {
            System.out.println("Exiting the time bomb "+this.portListeningOn);
            System.out.println("Mexico");
            try
            {
                this.manager.close();
                this.manager.die = true;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        super.onTimeUpdate(messageFromChronos);
    }
}
