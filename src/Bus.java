package src;

public class Bus {
    private int id;
    private Driver driver;
 
    public Bus(int id, Driver driver) {
        this.id = id;
        this.driver = driver;
    }
 
    public int getId() {
        return id;
    }
 
    public Driver getDriver() {
        return driver;
    }
}
