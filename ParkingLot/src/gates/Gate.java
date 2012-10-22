package gates;

import messaging.CarArrivalMessage;
import messaging.TimeMessage;

public interface Gate {

	/**
	 * Specifies what the Gate should do when a car arrives to it.
	 * @param arrival, The ArrivalMessage which represents information about the car
	 */
	public void onCarArrived(CarArrivalMessage arrival);
	
	/**
	 * Specifies what to do when a car leaves the gate
	 */
	public void onCarLeave();
	
	/**
	 * Specifies what the Gate should do when it receives a new message about the time.
	 * @param newTime, The new canonical time for the system
	 */
	public void onTimeUpdate(TimeMessage newTime);
	
	/**
	 * Specifies what the gate should do when its tokens run low
	 */
	public void onTokensLow();
	
	/**
	 * Returns the number of token that the gate currently has
	 * @return The number of tokens that the Gate currently posesses
	 */
	public int getNumberTokens();
	
	/**
	 * Removes the given number of tokens from the gate.
	 * @param numberOfTokensToReceive, The number of tokens which will be removed from the Gate
	 * @return True if that many tokens could be removed.  False otherwise.
	 */
	public boolean removeTokens(int numberOfTokensToReceive);
	
	/**
	 * Specifies what a gate should do when it has extra tokens added.
	 * @param tokens, The number of additional tokens which are being added.
	 */
	public void onTokensAdded(int tokens);
	
	/**
	 * Gets the amount of money left that this Gate has.
	 * @return The amount of money still left for the Gate.
	 */
	public int getAmountOfMoneyLeft();
	
	/**
	 * Removes a given amount of money from the Gate's coffers
	 * @param amountOfMoneyToTake, The amount of money to take from the gate.
	 * @return True if the gate can have that much money removed, false otherwise.
	 */
	public boolean removeMoney(int amountOfMoneyToTake);
	
	/**
	 * Adds money to the Gate's coffers
	 * @param amountOfMoneyToAdd, The amount of money which is being added to the Gate.
	 */
	public void addMoney(int amountOfMoneyToAdd);
}
