package messaging;

import java.net.InetAddress;

public class ManagerAvailableMessage extends AbstractMessage {

	private InetAddress addr;
	private int gatePort;
	private int managerPort;
	
	public ManagerAvailableMessage(InetAddress addr, int gatePort, int managerPort) {
		super(AbstractMessage.TYPE_MANAGER_AVAILABLE);
		this.addr = addr;
		this.gatePort = gatePort;
		this.managerPort = managerPort;
	}

	public InetAddress getAddr() {
		return addr;
	}

	public void setAddr(InetAddress addr) {
		this.addr = addr;
	}

	public int getGatePort() {
		return gatePort;
	}

	public void setGatePort(int gatePort) {
		this.gatePort = gatePort;
	}

	public int getManagerPort() {
		return managerPort;
	}

	public void setManagerPort(int managerPort) {
		this.managerPort = managerPort;
	}	

}
