package src;

import java.sql.Timestamp;
import java.util.ArrayList;

public class BusRoute {

	private int id;
	private int currentStopIndex;
	private Bus bus;
	private Route route;
	private Timestamp departureTime;
	private ArrayList<Passenger> passengers;
	
	private final String LATE_STATUS = "Late";
	private final String EARLY_STATUS = "Early";
	private final String ON_TIME_STATUS = "On Time";
	private final int LATE_BUFFER = 3;
	private final int EARLY_BUFFER = -3;
	
	public BusRoute(int id, Bus bus, Route route) {
		this.id = id;
		this.bus = bus;
		this.route = route;
	}
	
	public int getId() {
		return id;
	}
	
	public Bus getBus() {
		return bus;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public Stop getCurrentStop() {
		return route.getStops().get(currentStopIndex);
	}
	
	public void setCurrentStopId(int stopIndex) throws IllegalArgumentException {
		if (id >= route.getStops().size()) {
			throw new IllegalArgumentException("Stop ID outside range of Stops on this Route");
		} else {
			currentStopIndex = stopIndex;
		}
	}
	
	public Timestamp getDepartureTime() {
		return departureTime;
	}
	
	public void setDepartureTime(Timestamp departureTime) {
		this.departureTime = departureTime;
	}
	
	public ArrayList<Passenger> getPassengers() {
		return passengers;
	}
	
	public void addPassenger(Passenger p) {
		passengers.add(p);
	}
	
	public String getOnTimeStatus() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		
		if(isLate(currentTime)) {
			return LATE_STATUS;
		} else if(isEarly(currentTime)) {
			return EARLY_STATUS;
		} else {
			return ON_TIME_STATUS;
		}
	}
	
	private boolean isLate(Timestamp currentTime) {
		if (currentStopIndex + 1 < route.getStops().size()) {
			Timestamp lateTime = new Timestamp(departureTime.getTime());
			Stop nextStop = route.getStops().get(currentStopIndex + 1);
			lateTime.setMinutes(lateTime.getMinutes() + nextStop.getExpectedArrivalTime() + LATE_BUFFER);
			return currentTime.compareTo(lateTime) > 0;
		} else {
			return false;
		}
	}
	
	private boolean isEarly(Timestamp currentTime) {
		Timestamp earlyTime = new Timestamp(departureTime.getTime());
		earlyTime.setMinutes(departureTime.getMinutes() + getCurrentStop().getExpectedArrivalTime() + EARLY_BUFFER);
		return currentTime.compareTo(earlyTime) < 0;
	}
}
