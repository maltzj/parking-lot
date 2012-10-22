package messaging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Date;

public abstract class AbstractMessage {

	public static final byte TYPE_CAR_ARRIVAL = 1;
	public static final byte TYPE_GATE_SUBSCRIBE = 2;
	public static final byte TYPE_TIME_SUBSCRIBE = 3;
	public static final byte TYPE_TIME_MESSAGE = 4;
	public static final byte TYPE_TOKEN_SUBSCRIBE_MESSAGE = 5;
	public static final byte TYPE_TOKEN_REQUEST_MESSAGE = 6;
	public static final byte TYPE_TOKEN_MESSAGE = 7;
	public static final byte TYPE_MONEY_MESSAGE = 8;
	public static final byte TYPE_CAR_LEAVING = 9;
	
	public static final byte TYPE_GATE_DONE = 10;
	public static final byte TYPE_LOT_DONE = 11;
	public static final byte TYPE_CLOSE_CONNECTION = 20;
	
	public static final byte TYPE_MONEY_QUERY_MESSAGE = 21;
	public static final byte TYPE_TOKEN_QUERY_MESSAGE = 22;
	
	public static final byte TYPE_MONEY_AMOUNT_MESSAGE = 23;
	public static final byte TYPE_TOKEN_AMOUNT_MESSAGE = 24;
	
	protected int length;
	protected byte messageType;
	
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
				case TYPE_TOKEN_SUBSCRIBE_MESSAGE:
				{
					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
					return new TokenSubscribeMessage(InetAddress.getByName(inetAddress), port);
				}
				case TYPE_TOKEN_REQUEST_MESSAGE:
				{
					int numberOfTokens = dataInput.readInt();
					return new TokenRequestMessage(numberOfTokens);
				}
				case TYPE_TOKEN_MESSAGE:
				{
					int tokensSent = dataInput.readInt();
					return new TokenMessage(tokensSent);
				}
				case TYPE_MONEY_MESSAGE:
				{
					int amountOfMoney = dataInput.readInt();
					return new MoneyMessage(amountOfMoney);
				}
				case TYPE_CAR_LEAVING:
				{
					return new CarLeavingMessage();
				}				
                case TYPE_GATE_DONE:
				{

					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
                
					return new GateDoneMessage(InetAddress.getByName(inetAddress), port);
				}
                case TYPE_LOT_DONE:
				{
					int length = dataInput.readInt();
					int port = dataInput.readInt();
					String inetAddress = getIpAddress(dataInput, length - 4);
                
					return new LotDoneMessage(InetAddress.getByName(inetAddress), port);
				}
				case TYPE_CLOSE_CONNECTION:
				{
					return new SimpleMessage(TYPE_CLOSE_CONNECTION);
				}
				case TYPE_MONEY_QUERY_MESSAGE:
				{
					return new SimpleMessage(TYPE_MONEY_QUERY_MESSAGE);
				}
				case TYPE_TOKEN_QUERY_MESSAGE:
				{
					return new SimpleMessage(TYPE_TOKEN_QUERY_MESSAGE);
				}
				case TYPE_MONEY_AMOUNT_MESSAGE:
				{
					int amountOfMoney = dataInput.readInt();
					return new MoneyAmountMessage(amountOfMoney);
				}
				case TYPE_TOKEN_AMOUNT_MESSAGE:
				{
					int numberOfTokens = dataInput.readInt();
					return new TokenAmountMessage(numberOfTokens);
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
					dataOutput.flush();
					break;
				}
				case TYPE_GATE_SUBSCRIBE:
				{
					GateSubscribeMessage subsribeMessage = (GateSubscribeMessage) messageWriting;
					String addressAsString = subsribeMessage.getAddressOfGate().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("UTF-8");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(subsribeMessage.getPort());
					dataOutput.write(addressAsBytes);
					dataOutput.flush();
					break;
				}
				case TYPE_TIME_SUBSCRIBE:
				{
					TimeSubscribeMessage subsribeMessage = (TimeSubscribeMessage) messageWriting;
					String addressAsString = subsribeMessage.getAddressSubscribing().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("UTF-8");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(subsribeMessage.getPortSubscribingOn());
					dataOutput.write(addressAsBytes);
					dataOutput.flush();
					break;
				}
				case TYPE_TIME_MESSAGE:
				{
					dataOutput.writeLong(((TimeMessage) messageWriting).getNewTime().getTime());
					dataOutput.flush();
					break;
				}
				case TYPE_TOKEN_SUBSCRIBE_MESSAGE:
				{
					TokenSubscribeMessage subsribeMessage = (TokenSubscribeMessage) messageWriting;
					String addressAsString = subsribeMessage.getAddressSubscribing().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("UTF-8");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(subsribeMessage.getPortSubscribingOn());
					dataOutput.write(addressAsBytes);
					dataOutput.flush();
					break;
				}
				case TYPE_TOKEN_REQUEST_MESSAGE:
				{
					TokenRequestMessage requestMessage = (TokenRequestMessage) messageWriting;
					dataOutput.writeInt(requestMessage.getTotalNumberOfTokensRequested());
					dataOutput.flush();
					break;
				}
				case TYPE_TOKEN_MESSAGE:
				{
					TokenMessage tokenMessage = (TokenMessage) messageWriting;
					dataOutput.writeInt(tokenMessage.getNumberOfTokensSent());
					dataOutput.flush();
					break;
				}
				case TYPE_MONEY_MESSAGE:
				{
					MoneyMessage moneyMessage = (MoneyMessage) messageWriting;
					dataOutput.writeInt(moneyMessage.amountOfMoney);
					dataOutput.flush();
					break;
				}
				case TYPE_CAR_LEAVING:
				{
					dataOutput.flush();
					break;
				}
				case TYPE_GATE_DONE:
				{
					GateDoneMessage gateMessage = (GateDoneMessage) messageWriting;
					String addressAsString = gateMessage.getAddressSubscribing().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("UTF-8");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(gateMessage.getPortSubscribingOn());
					dataOutput.write(addressAsBytes);
					dataOutput.flush();
					break;
				}
				case TYPE_LOT_DONE:
				{
					LotDoneMessage lotMessage = (LotDoneMessage) messageWriting;
					String addressAsString = lotMessage.getAddressSubscribing().getHostAddress();
					byte[] addressAsBytes = addressAsString.getBytes("UTF-8");
					dataOutput.writeInt(addressAsBytes.length + 4);
					dataOutput.writeInt(lotMessage.getPortSubscribingOn());
					dataOutput.write(addressAsBytes);
					dataOutput.flush();
					break;
				}
				case TYPE_CLOSE_CONNECTION:
				{
					dataOutput.flush();
					break;
				}
				case TYPE_MONEY_AMOUNT_MESSAGE:
				{
					MoneyAmountMessage message = (MoneyAmountMessage) messageWriting;
					dataOutput.writeInt(message.amountOfMoney);
					dataOutput.flush();
					break;
				}
				case TYPE_TOKEN_AMOUNT_MESSAGE:
				{
					TokenAmountMessage tokenAmount = (TokenAmountMessage) messageWriting;
					dataOutput.writeInt(tokenAmount.numberOfTokens);
					dataOutput.flush();
					break;
				}
				default:
					return;
			}
		}
	}
	
	private static String getIpAddress(DataInputStream dataInput, int size) throws IOException
	{
		byte[] addressSize = new byte[size];
		dataInput.read(addressSize);
		return new String(addressSize, "UTF-8");
	}
	
	public byte getMessageType()
	{
		return this.messageType;
	}
}
