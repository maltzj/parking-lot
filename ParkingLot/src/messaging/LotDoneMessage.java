package messaging;

import java.net.InetAddress;

public class LotDoneMessage extends AbstractMessage{

	private InetAddress addressSubscribing;
	private int portSubscribingOn;
	
	
	public LotDoneMessage(InetAddress address, int port) {
		super(AbstractMessage.TYPE_GATE_DONE);
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
