package car;

import java.util.Calendar;
import java.util.Date;

public class Car {
	
	Date timeSent;
	Date timeDeparts;
	Date timeWaitingUntil;
	
	public Car(Date timeSent, Date timeDeparts, long timeToWait) {
		super();
		this.timeSent = timeSent;
		this.timeDeparts = timeDeparts;
		
		Calendar timeLeavingCal = Calendar.getInstance();
		Date dateToLeave = new Date(timeSent.getTime() + timeToWait);
		timeLeavingCal.setTime(dateToLeave);
		timeWaitingUntil = timeLeavingCal.getTime();
	}
	
	public Date getTimeSent() {
		return timeSent;
	}
	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}
	public Date getTimeDeparts() {
		return timeDeparts;
	}
	public void setTimeDeparts(Date timeDeparts) {
		this.timeDeparts = timeDeparts;
	}
	public Date getTimeWaitingUntil() {
		return timeWaitingUntil;
	}
	public void setTimeWaitingUntil(Date timeWaitingUntil) {
		this.timeWaitingUntil = timeWaitingUntil;
	}
	
	
}
