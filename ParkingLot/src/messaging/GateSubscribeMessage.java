package messaging;

import java.net.InetAddress;

/**
 * 
 * A GateSubscribeMessage is a message which is used to announce to the Traffic Generator that a given actor would like to receive cars
 * The message contains information about the receiver's ip and port
 *
 */

public class GateSubscribeMessage extends AbstractMessage{
	
	private InetAddress addressOfGate;
	private int port;
	
	/**
	 * Creates a message based off the given ip address and port
	 * @param addressOfGate The ip of the gate subscribing
	 * @param port The port of the gate subscribing
	 */
	public GateSubscribeMessage(InetAddress addressOfGate, int port) {
		super(AbstractMessage.TYPE_GATE_SUBSCRIBE);
		this.addressOfGate = addressOfGate;
		this.port = port;
	}
	
	/**
	 * Get the ip of the gate subscribing to traffic
	 * @return The ip of the subscribing gate
	 */
	public InetAddress getAddressOfGate() {
		return addressOfGate;
	}
	
	/**
	 * Set the address of the gate subscribing to traffic 
	 * @param addressOfGate The ip of the subscribing gate
	 */
	public void setAddressOfGate(InetAddress addressOfGate) {
		this.addressOfGate = addressOfGate;
	}
	
	/**
	 * Get the port of the gate subscribing to traffic
	 * @return The port of the subscribing gate
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port of the gate subscribing to traffic
	 * @param port The port of the gate subscribing to traffic
	 */
	public void setPort(int port) {
		this.port = port;
	}
	

}
