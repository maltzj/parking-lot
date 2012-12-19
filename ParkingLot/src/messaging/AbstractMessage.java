package messaging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Stack;

import util.HostPort;

public abstract class AbstractMessage {

	public static final byte TYPE_CAR_ARRIVAL = 1;
	public static final byte TYPE_GATE_SUBSCRIBE = 2;
	public static final byte TYPE_TIME_SUBSCRIBE = 3;
	public static final byte TYPE_TIME_MESSAGE = 4;
	
	public static final byte TYPE_TOKEN_REQUEST_MESSAGE = 6;
	public static final byte TYPE_TOKEN_MESSAGE = 7;
	public static final byte TYPE_MONEY_MESSAGE = 8;
	public static final byte TYPE_TOKEN_REQUIRE_MESSAGE = 9;
	public static final byte TYPE_TOKEN_RESPONSE_MESSAGE = 100;
	
	public static final byte TYPE_GATE_DONE = 10;
	public static final byte TYPE_LOT_DONE = 11;
	public static final byte TYPE_DONE = 12;
	
	public static final byte TYPE_CLOSE_CONNECTION = 20;
	
	public static final byte TYPE_MONEY_QUERY_MESSAGE = 21;
	public static final byte TYPE_TOKEN_QUERY_MESSAGE = 22;
	
	public static final byte TYPE_MONEY_AMOUNT_MESSAGE = 23;
	public static final byte TYPE_TOKEN_AMOUNT_MESSAGE = 24;
	
	public static final byte TYPE_GATE = 25;
	
	public static final byte TYPE_MANAGER_AVAILABLE = 26;
	
	public static final byte TYPE_CONNECT = 50;
	
	protected int length;
	protected byte messageType;
	
	/**
	 * The one thing that every instance of AbstractMessage must do is initialize its type
	 * @param type
	 */
	public AbstractMessage(byte type)
	{
		this.messageType = type;
	}
	

	/**
	 * Decodes the message being sent on the given InputStream
	 * @param inputStream The input stream which is being listened on
	 * @return An AbstractMessage which represents the decoded message
	 * @throws IOException If an exception occurs
	 */					
	public static AbstractMessage decodeMessage(InputStream inputStream) throws IOException
	{
		
		synchronized(inputStream)
		{
			DataInputStream dataInput = new DataInputStream(inputStream);
			byte messageType = dataInput.readByte();
			switch (messageType)
			{
				case TYPE_CAR_ARRIVAL:
				{
					long startTime = dataInput.readLong();
					long endTime = dataInput.readLong();
					return new CarArrivalMessage(new Date(startTime), new Date(endTime));
				}
				case TYPE_GATE_SUBSCRIBE:
				{
					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
					return new GateSubscribeMessage(InetAddress.getByName(inetAddress), port);
				}
				case TYPE_TIME_SUBSCRIBE:
				{
					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
					return new TimeSubscribeMessage(InetAddress.getByName(inetAddress), port);
				}
				case TYPE_TIME_MESSAGE:
				{
					long time = dataInput.readLong();
					return new TimeMessage(new Date(time));
				}
				case TYPE_TOKEN_REQUEST_MESSAGE:
				{
					int length = dataInput.readInt();
					int tokens = dataInput.readInt(); //get length and TTL
					int ttl = dataInput.readInt();
					byte[] hostPorts = new byte[length - 8];
					dataInput.read(hostPorts);
					String formatted = new String(hostPorts, "ASCII"); //encode the bytes
					Stack<HostPort> stackOfHosts = AbstractMessage.convertStringToHostPort(formatted);
					return new TokenRequestMessage(tokens, stackOfHosts, ttl);
				}
				case TYPE_TOKEN_RESPONSE_MESSAGE:
				{
					int length = dataInput.readInt();
					int tokens = dataInput.readInt();
					byte[] stackAsBytes = new byte[length - 4];
					dataInput.read(stackAsBytes);
					String stackString = new String(stackAsBytes, "ASCII"); //encode the bytes
					Stack<HostPort> stackOfHosts = AbstractMessage.convertStringToHostPort(stackString);
					return new TokenResponseMessage(tokens, stackOfHosts);
				}
				case TYPE_TOKEN_REQUIRE_MESSAGE:
				{
					int tokens = dataInput.readInt();
					return new TokenRequireMessage(tokens);
				}
				case TYPE_TOKEN_MESSAGE:
				{
					int tokensSent = dataInput.readInt();
					return new TokenMessage(tokensSent);
				}	
                case TYPE_GATE_DONE:
				{

					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
                
					return new GateDoneMessage(InetAddress.getByName(inetAddress), port);
				}
				case TYPE_CLOSE_CONNECTION:
				{
					return new SimpleMessage(TYPE_CLOSE_CONNECTION);
				}
				case TYPE_CONNECT:
				{
					return new SimpleMessage(TYPE_CONNECT);
				}
				case TYPE_MONEY_QUERY_MESSAGE:
				{
					return new SimpleMessage(TYPE_MONEY_QUERY_MESSAGE);
				}
				case TYPE_TOKEN_QUERY_MESSAGE:
				{
					return new SimpleMessage(TYPE_TOKEN_QUERY_MESSAGE);
				}
				case TYPE_TOKEN_AMOUNT_MESSAGE:
				{
					int length = dataInput.readInt();
					int numberOfTokens = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddString = getIpAddress(dataInput, length - 8);
					return new TokenAmountMessage(numberOfTokens, InetAddress.getByName(inetAddString), port);
				}
				case TYPE_GATE:
				{
					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
					return new GateMessage(InetAddress.getByName(inetAddress), port);
				}
				case TYPE_MANAGER_AVAILABLE:
				{
					int length = dataInput.readInt();
					int gatePort = dataInput.readInt();
					int managerPort = dataInput.readInt();
					String inetAddr = getIpAddress(dataInput, length - 8);
					return new ManagerAvailableMessage(InetAddress.getByName(inetAddr), gatePort, managerPort);
				}
				case TYPE_DONE:
				{
					return new DoneMessage();
				}
				default:
					return null;
			}
		}
	}
	
	/**
	 * Encodes a given AbstractMessage on a given output stream
	 * @param outputStream The output stream on which the message is being written
	 * @param messageWriting The message being written onto the output stream
	 * @throws IOException If an exception occurs.
	 */
	public static void encodeMessage(OutputStream outputStream, AbstractMessage messageWriting) throws IOException
	{
		synchronized (outputStream)
		{
			DataOutputStream dataOutput = new DataOutputStream(outputStream);
			dataOutput.writeByte(messageWriting.getMessageType());
			switch(messageWriting.messageType)
			{
				case TYPE_CAR_ARRIVAL:
				{
					CarArrivalMessage arrivalMessage = (CarArrivalMessage) messageWriting;
					dataOutput.writeLong(arrivalMessage.getCarSentTime().getTime());
					dataOutput.writeLong(arrivalMessage.getCarReturnTime().getTime());
					
					break;
				}
				case TYPE_GATE_SUBSCRIBE:
				{
					GateSubscribeMessage subsribeMessage = (GateSubscribeMessage) messageWriting;
					String addressAsString = subsribeMessage.getAddressOfGate().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("ASCII");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(subsribeMessage.getPort());
					dataOutput.write(addressAsBytes);
					
					break;
				}
				case TYPE_TIME_SUBSCRIBE:
				{
					TimeSubscribeMessage subsribeMessage = (TimeSubscribeMessage) messageWriting;
					String addressAsString = subsribeMessage.getAddressSubscribing().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("ASCII");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(subsribeMessage.getPortSubscribingOn());
					dataOutput.write(addressAsBytes);
					
					break;
				}
				case TYPE_TIME_MESSAGE:
				{
					dataOutput.writeLong(((TimeMessage) messageWriting).getNewTime().getTime());
					
					break;
				}
				case TYPE_TOKEN_REQUEST_MESSAGE:
				{
					TokenRequestMessage requestMessage = (TokenRequestMessage) messageWriting;
					String parsedStack = AbstractMessage.convertHostPortsToStrings(requestMessage.getReceivers());
					dataOutput.writeInt(parsedStack.length()  + 8);
					dataOutput.writeInt(requestMessage.getTokensRequested());
					dataOutput.writeInt(requestMessage.getTtl());
					dataOutput.write(parsedStack.getBytes("ASCII"));
					
					break;
				}
				case TYPE_TOKEN_RESPONSE_MESSAGE:
				{
					TokenResponseMessage responseMessage = (TokenResponseMessage) messageWriting;
					String parsedStack = AbstractMessage.convertHostPortsToStrings(responseMessage.getReceivers());
					dataOutput.writeInt(parsedStack.length() + 4);
					dataOutput.writeInt(responseMessage.getNumberOfTokens());
					dataOutput.write(parsedStack.getBytes("ASCII"));
					
					break;
				}
				case TYPE_TOKEN_REQUIRE_MESSAGE:
				{
					TokenRequireMessage requireMessage = (TokenRequireMessage) messageWriting;
					dataOutput.writeInt(requireMessage.getTokensRequired());	
					break;
				}
				case TYPE_TOKEN_MESSAGE:
				{
					TokenMessage tokenMessage = (TokenMessage) messageWriting;
					dataOutput.writeInt(tokenMessage.getNumberOfTokensSent());
					break;
				}
				case TYPE_GATE_DONE:
				{
					GateDoneMessage gateMessage = (GateDoneMessage) messageWriting;
					String addressAsString = gateMessage.getAddressSubscribing().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("ASCII");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(gateMessage.getPortSubscribingOn());
					dataOutput.write(addressAsBytes);
					
					break;
				}
				case TYPE_CLOSE_CONNECTION:
				{
					
					break;
				}
				case TYPE_CONNECT:
				{
					
					break;
				}
				case TYPE_TOKEN_AMOUNT_MESSAGE:
				{
					TokenAmountMessage tokenAmount = (TokenAmountMessage) messageWriting;
					String addressAsString = tokenAmount.getIpAddress().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("ASCII");
					dataOutput.writeInt(addressAsBytes.length + 8);
					dataOutput.writeInt(tokenAmount.getNumberOfTokens());
					dataOutput.writeInt(tokenAmount.getPort());
					dataOutput.write(addressAsBytes);
					
					break;
				}
				case TYPE_GATE:
				{
					GateMessage gateMessage = (GateMessage) messageWriting;
					InetAddress addr = gateMessage.addr;
					byte[] addressAsBytes = addr.getHostAddress().getBytes("ASCII");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(gateMessage.getPort());
					dataOutput.write(addressAsBytes);
					break;
				}
				case TYPE_MANAGER_AVAILABLE:
				{
					ManagerAvailableMessage availableMessage = (ManagerAvailableMessage) messageWriting;
					InetAddress addr = availableMessage.getAddr();
					byte[] addrAsBytes = addr.getHostAddress().getBytes("ASCII");
					dataOutput.writeInt(addrAsBytes.length + 8);
					dataOutput.writeInt(availableMessage.getGatePort());
					dataOutput.writeInt(availableMessage.getManagerPort());
					dataOutput.write(addrAsBytes);
					
					break;
				}
				default:
				{
					break;
				}
				
			}
			dataOutput.flush();
			outputStream.flush();
		}
	}
	
	private static String getIpAddress(DataInputStream dataInput, int size) throws IOException
	{
		byte[] addressSize = new byte[size];
		dataInput.read(addressSize);
		return new String(addressSize, "ASCII");
	}
	
	public byte getMessageType()
	{
		return this.messageType;
	}
	
	private static String convertHostPortsToStrings(Stack<HostPort> hosts){
		StringBuilder encoded = new StringBuilder("");
		while(!hosts.isEmpty()){ //for all the items in the stack
			HostPort top = hosts.pop();
			InetAddress ipAddr = top.iaddr;
			int port = top.port;
			encoded.append(ipAddr.getHostAddress()); //get the ip address
			encoded.append(":");
			encoded.append(port); //add the port
			encoded.append(";"); //delimit it accordingly
		}
		return encoded.toString();
	}
	
	private static Stack<HostPort> convertStringToHostPort(String hostPorts){
		Stack<HostPort> receivers = new Stack<HostPort>();
		String[] hosts = hostPorts.split(";"); //split the string on semicolons
		hostPorts = hostPorts.trim();
		
		for(String host: hosts){

			if(host.length() == 0){//account for edge cases
				break;
			}
			
			String[] information = host.split(":"); //split each string on semicolon
			try {
				InetAddress addr = InetAddress.getByName(information[0]); //format the string for bytes
				int port = Integer.parseInt(information[1]);
				HostPort toAdd = new HostPort(addr, port);
				receivers.push(toAdd);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
			} 
		}
		
		return receivers;
	}
}
