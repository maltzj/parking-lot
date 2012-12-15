package gates;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import messaging.AbstractMessage;
import messaging.CarArrivalMessage;
import messaging.GateDoneMessage;
import messaging.GateMessage;
import messaging.GateSubscribeMessage;
import messaging.MoneyMessage;
import messaging.SimpleMessage;
import messaging.TimeMessage;
import messaging.TokenMessage;
import messaging.TokenRequestMessage;
import tokentrading.GlobalTokenTrader;
import tokentrading.NoTokenTrader;
import tokentrading.ProfitTokenTrader;
import tokentrading.TokenTrader;
import util.Config;
import util.MessageHandler;
import util.MessageListener;
import car.Car;

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
            System.exit(0);
        }
        super.onTimeUpdate(messageFromChronos);
    }
}
