package src;

import java.util.ArrayList;

public class Route {
    private int id;
    private String name;
    private ArrayList<Stop> stops;
 
    public Route(int id, String name, ArrayList<Stop> stops) {
        this.id = id;
        this.name = name;
        this.stops = stops;
    }
 
    public int getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public ArrayList<Stop> getStops() {
        return stops;
    }
}
