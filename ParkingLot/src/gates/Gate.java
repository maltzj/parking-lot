package gates;

import messaging.CarArrivalMessage;
import messaging.TimeMessage;

public interface Gate {

	
	public void onCarArrived(CarArrivalMessage arrival);
	public void onCarLeave();
	public void onTimeUpdate(TimeMessage newTime);
	public void onTokensLow();
	
	public int getNumberTokens();
	public boolean removeTokens(int numberOfTokensToReceive);
	public void onTokensAdded(int tokens);
	
	public int getAmountOfMoneyLeft();
	public boolean removeMoney(int amountOfMoneyToTake);
	public void addMoney(int amountOfMoneyToAdd);
}
