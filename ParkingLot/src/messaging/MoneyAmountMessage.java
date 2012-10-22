package messaging;

public class MoneyAmountMessage extends AbstractMessage{
	
	int amountOfMoney;
	
	public MoneyAmountMessage(int amountOfMoney)
	{
		super(AbstractMessage.TYPE_MONEY_AMOUNT_MESSAGE);
		this.amountOfMoney = amountOfMoney;
	}

}
