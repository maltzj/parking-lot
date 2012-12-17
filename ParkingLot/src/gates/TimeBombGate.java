package gates;
import java.net.*;
import util.MessageHandler;
import messaging.*;
import java.util.Date;

public class TimeBombGate extends Gate implements MessageHandler
{
    public TimeBombGate(long timeToWait, int tokensToStartWith, int moneyToStartWith, InetAddress addr, int port, int moneyPerCarPassed, int tradingPolicy) throws Exception
    {
        super(timeToWait, tokensToStartWith, moneyToStartWith, addr, port, moneyPerCarPassed, tradingPolicy);
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
                this.simulationMessageListener.die = true;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        super.onTimeUpdate(messageFromChronos);
    }
}
