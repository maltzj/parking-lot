package messaging;

import java.net.InetAddress;

/**
 * Message to announce the fact that a gate is done with its responsibilities, whatever those may be
 *
 */
public class GateDoneMessage extends AbstractMessage{

	private InetAddress addressSubscribing;
	private int portSubscribingOn;
	
	/**
	 * Creates a GateDoneMessage for a specified Gate
	 * @param address The ipAddress of the Gate
	 * @param port The port that the gate listens on
	 */
	public GateDoneMessage(InetAddress address, int port) {
		super(AbstractMessage.TYPE_GATE_DONE);
		this.addressSubscribing = address;
		this.portSubscribingOn = port;
	}
	
	/**
	 * Gets the address of the gate that is done with its responsibilities
	 * @return The ip Address that the Gate is listening on
	 */
	public InetAddress getAddressSubscribing() {
		return addressSubscribing;
	}
	
	/**
	 * Set the address of the Gate that is done with its responsibilities
	 * @param addressSubscribing The ip of the gate which is done
	 */
	public void setAddressSubscribing(InetAddress addressSubscribing) {
		this.addressSubscribing = addressSubscribing;
	}
	
	/**
	 * Gets the port of the gate that is done with its responsibilities
	 * @return The port that the gate is listening on
	 */
	public int getPortSubscribingOn() {
		return portSubscribingOn;
	}
	
	/**
	 * Get the port of the Gate that is done with its responsibilities
	 * @param portSubscribingOn The port of the gate which is done
	 */
	public void setPortSubscribingOn(int portSubscribingOn) {
		this.portSubscribingOn = portSubscribingOn;
	}
	
}
