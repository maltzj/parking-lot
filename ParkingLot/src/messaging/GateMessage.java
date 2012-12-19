package messaging;

import java.net.InetAddress;

import util.HostPort;

public class GateMessage extends AbstractMessage{

	InetAddress addr;
	int port;
	
	public GateMessage(InetAddress addr, int port) {
		super(AbstractMessage.TYPE_GATE);
		this.addr = addr;
		this.port = port;
	}
	
	public GateMessage(HostPort hostPort){
		super(AbstractMessage.TYPE_GATE);
		this.addr = hostPort.iaddr;
		this.port = hostPort.port;
	}

	public InetAddress getAddr() {
		return addr;
	}

	public void setAddr(InetAddress addr) {
		this.addr = addr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String toString(){
		return this.addr.getHostAddress() + " " + this.port;
	}
}
