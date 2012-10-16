package messaging;

import java.net.InetAddress;

public class TokenSubscribeMessage extends AbstractMessage {
	
	private InetAddress addressSubscribing;
	private int portSubscribingOn;
	
	public TokenSubscribeMessage(InetAddress addressSubscribing,
			int portSubscribingOn) {
		super(AbstractMessage.TYPE_TOKEN_SUBSCRIBE_MESSAGE);
		this.addressSubscribing = addressSubscribing;
		this.portSubscribingOn = portSubscribingOn;
	}
	

	public InetAddress getAddressSubscribing() {
		return addressSubscribing;
	}

	public void setAddressSubscribing(InetAddress addressSubscribing) {
		this.addressSubscribing = addressSubscribing;
	}

	public int getPortSubscribingOn() {
		return portSubscribingOn;
	}

	public void setPortSubscribingOn(int portSubscribingOn) {
		this.portSubscribingOn = portSubscribingOn;
	}
	

}
