package messaging;

import java.net.InetAddress;

public class MoneyAmountMessage extends AbstractMessage{
	
	InetAddress ipAddress;
	int port;
	int amountOfMoney;
	
	public MoneyAmountMessage(int amountOfMoney, InetAddress ipAddress, int port)
	{
		super(AbstractMessage.TYPE_MONEY_AMOUNT_MESSAGE);
		this.ipAddress = ipAddress;
		this.port = port;
		this.amountOfMoney = amountOfMoney;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getAmountOfMoney() {
		return amountOfMoney;
	}

	public void setAmountOfMoney(int amountOfMoney) {
		this.amountOfMoney = amountOfMoney;
	}

	
	
}
