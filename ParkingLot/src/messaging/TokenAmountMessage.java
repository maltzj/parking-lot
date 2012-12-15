package messaging;

import java.net.InetAddress;

public class TokenAmountMessage extends AbstractMessage {
	
	int numberOfTokens;
	InetAddress ipAddress;
	int port;
	
	public TokenAmountMessage(int numberOfTokens, InetAddress ipAddress, int port)
	{
		super(AbstractMessage.TYPE_TOKEN_AMOUNT_MESSAGE);
		this.ipAddress = ipAddress;
		this.numberOfTokens = numberOfTokens;
		this.port = port;
	}

	public int getNumberOfTokens() {
		return numberOfTokens;
	}

	public void setNumberOfTokens(int numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
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

	
	
}
