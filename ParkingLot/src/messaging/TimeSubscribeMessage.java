package messaging;

import java.net.InetAddress;


/**
 * A TimeSubscribeMessage is sent to tell the Traffic Generator that a given actor would like to receive time updates
 *
 */
public class TimeSubscribeMessage extends AbstractMessage{

	private InetAddress addressSubscribing;
	private int portSubscribingOn;
	
	
	/**
	 * Initializes the TimeSubscribeMessage to have a particular ip and port combination
	 *  
	 * @param address The ip address of the actor that is subscribing
	 * @param port The port of the subscribing actor
	 */
	public TimeSubscribeMessage(InetAddress address, int port) {
		super(AbstractMessage.TYPE_TIME_SUBSCRIBE);
		this.addressSubscribing = address;
		this.portSubscribingOn = port;
		
		
	}
	
	/**
	 * Get the ip of the actor subscribing
	 * @return The ip of the subscribing actor
	 */
	public InetAddress getAddressSubscribing() {
		return addressSubscribing;
	}
	
	/**
	 * Get the port of the actor subscribing
	 * @return The port of the subscribing actor
	 */
	public int getPortSubscribingOn() {
		return portSubscribingOn;
	}
}
