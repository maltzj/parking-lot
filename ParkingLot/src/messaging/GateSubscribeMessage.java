package messaging;

import java.net.InetAddress;

public class GateSubscribeMessage extends AbstractMessage{
	
	private InetAddress addressOfGate;
	private int port;
	
	public GateSubscribeMessage(InetAddress addressOfGate, int port) {
		super(AbstractMessage.TYPE_GATE_SUBSCRIBE);
		this.addressOfGate = addressOfGate;
		this.port = port;
	}
	public InetAddress getAddressOfGate() {
		return addressOfGate;
	}
	public void setAddressOfGate(InetAddress addressOfGate) {
		this.addressOfGate = addressOfGate;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	public byte[] generateMessageData()
	{
		return null;
	}

}
