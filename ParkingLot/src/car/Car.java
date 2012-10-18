package car;

import java.util.Calendar;
import java.util.Date;

public class Car {
	
	Date timeSent;
	Date timeDeparts;
	
	public Car(Date timeSent, Date timeDeparts) {
		super();
		this.timeSent = timeSent;
		this.timeDeparts = timeDeparts;
		
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

	
}
