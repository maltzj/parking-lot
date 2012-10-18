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
	
	protected int length;
	protected byte messageType;
	
	public AbstractMessage(byte type)
	{
		this.messageType = type;
	}
	
	
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
				default:
					return null;
			}
		}
	}
	
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
