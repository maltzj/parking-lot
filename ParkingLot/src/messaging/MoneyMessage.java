package messaging;

public class MoneyMessage extends AbstractMessage {

	int amountOfMoney;
	
	public MoneyMessage(int amountOfMoney) {
		super(AbstractMessage.TYPE_MONEY_MESSAGE);
		this.amountOfMoney = amountOfMoney;
	}

	public int getAmountOfMoney() {
		return amountOfMoney;
	}

	public void setAmountOfMoney(int amountOfMoney) {
		this.amountOfMoney = amountOfMoney;
	}
	
	

}
