package messaging;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractMessage {

	public static final byte TYPE_CAR_ARRIVAL = 1;
	public static final byte TYPE_GATE_SUBSCRIBE = 2;
	public static final byte TYPE_TIME_SUBSCRIBE = 3;
	public static final byte TYPE_TIME_MESSAGE = 4;
	
	protected int length;
	protected byte messageType;
	
	public AbstractMessage(int length, byte type)
	{
		this.length = length;
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
					//handle that accordingly
				case TYPE_GATE_SUBSCRIBE:
					//handle that accordingly
				case TYPE_TIME_SUBSCRIBE:
					//handle that accordingly
				case TYPE_TIME_MESSAGE:
					//handle that accordingly
				default:
					return null;
			}
		}
	}
	
	public static void encodeMessage(OutputStream outputStream, AbstractMessage messageWriting)
	{
		synchronized (outputStream)
		{
			DataOutputStream dataOutput = new DataOutputStream(outputStream);
		}
	}
	
	public abstract byte[] generateMessageData();
}
