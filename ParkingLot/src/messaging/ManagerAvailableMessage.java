package messaging;

import java.net.InetAddress;

/**
 * ManagerAvailableMessages specficy that a given manager has come online and is able to receive a paired gate
 *
 */
public class ManagerAvailableMessage extends AbstractMessage {

	private InetAddress addr;
	private int gatePort;
	private int managerPort;
	
	/**
	 * Initializes a ManagerAvailableMessage with a give ip, gatePort, and managerPort
	 * @param addr The ip that the manager is listening on
	 * @param gatePort The port where gates subscribe to this manager
	 * @param managerPort The port where managers subscribe to this manager
	 */
	public ManagerAvailableMessage(InetAddress addr, int gatePort, int managerPort) {
		super(AbstractMessage.TYPE_MANAGER_AVAILABLE);
		this.addr = addr;
		this.gatePort = gatePort;
		this.managerPort = managerPort;
	}

	/**
	 * Get the ip of this manager
	 * @return The ip of the manager
	 */
	public InetAddress getAddr() {
		return addr;
	}

	/**
	 * Sets the ip of this manager
	 * @param addr The ip of the manager
	 */
	public void setAddr(InetAddress addr) {
		this.addr = addr;
	}

	/**
	 * Gets the port that gates can use to subscribe to this manager
	 * @return The port that gates subscribe to the manager on
	 */
	public int getGatePort() {
		return gatePort;
	}

	/**
	 * Set The port that gates use to subscribe to this Manager
	 * @param gatePort The port that gates subscribe to the manager on
	 */
	public void setGatePort(int gatePort) {
		this.gatePort = gatePort;
	}

	/**
	 * Gets the port that managers can use to subscribe to this manager
	 * @return The port that managers subscribe to the manager on
	 */
	public int getManagerPort() {
		return managerPort;
	}

	/**
	 * Set The port that managers use to subscribe to this Manager
	 * @param managerPort The port that managers subscribe to the manager on
	 */
	public void setManagerPort(int managerPort) {
		this.managerPort = managerPort;
	}	

}
