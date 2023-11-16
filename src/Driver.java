package src;

public class Driver {
	private int id;
    private String name;
    private double salary;
 
    public Driver(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
 
    public int getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public double getSalary() {
        return salary;
    }
 
    public void setSalary(double newSalary) {
        this.salary = newSalary;
    }
}
