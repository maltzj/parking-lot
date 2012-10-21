package messaging;

import java.net.InetAddress;

public class TimeSubscribeMessage extends AbstractMessage{

	private InetAddress addressSubscribing;
	private int portSubscribingOn;
	
	
	public TimeSubscribeMessage(InetAddress address, int port) {
		super(AbstractMessage.TYPE_TIME_SUBSCRIBE);
		this.addressSubscribing = address;
		this.portSubscribingOn = port;
		
		
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
	
	public byte[] generateMessageData()
	{
		return null;
	}
}
