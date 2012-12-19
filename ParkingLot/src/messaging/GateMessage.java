package messaging;

import java.net.InetAddress;

import util.HostPort;

public class GateMessage extends AbstractMessage{

	InetAddress addr;
	int port;
	
	/**
	 * Creates a GateMessage given a particular ip and port combination 
	 * @param addr The address of the gate
	 * @param port The port that the gate is listening on
	 */
	public GateMessage(InetAddress addr, int port) {
		super(AbstractMessage.TYPE_GATE);
		this.addr = addr;
		this.port = port;
	}
	
	/**
	 * Creates a Message just based on a HostPort which wraps a given ip and port
	 * @param hostPort The HostPort which contains the gate's information
	 */
	public GateMessage(HostPort hostPort){
		super(AbstractMessage.TYPE_GATE);
		this.addr = hostPort.iaddr;
		this.port = hostPort.port;
	}

	/**
	 * Get the ip address of the gate
	 * @return The ip address that the gate is listening on
	 */
	public InetAddress getAddr() {
		return addr;
	}

	/**
	 * Sets the ip address of the gate
	 * @param addr The ip address that the gate is listening on
	 */
	public void setAddr(InetAddress addr) {
		this.addr = addr;
	}

	/**
	 * Gets the port of the gate
	 * @return The port that the gate is listening on 
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port of the gate
	 * @param port The port that the gate is listening on
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public String toString(){
		return this.addr.getHostAddress() + " " + this.port;
	}
}
