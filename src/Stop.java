package src;

public class Stop {
    private int id;
    private String location;
    private int expectedArrivalTime;
 
    public Stop(int id, String location, int expectedArrivalTime) {
        this.id = id;
        this.location = location;
        this.expectedArrivalTime = expectedArrivalTime;
    }
 
    public int getId() {
        return id;
    }
 
    public String getLocation() {
        return location;
    }
 
    public int getExpectedArrivalTime() {
        return expectedArrivalTime;
    }
 
    public void setExpectedArrivalTime(int newExpectedArrivalTime) {
        this.expectedArrivalTime = newExpectedArrivalTime;
    }
}
