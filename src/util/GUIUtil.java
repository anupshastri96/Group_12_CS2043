package src.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import src.classes.Bus;
import src.classes.BusRoute;
import src.classes.Driver;

public class GUIUtil {
	
	private static final String URL = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
    private static final String USER = "sql9657484";
    private static final String PASSWORD = "e8X5f44Fl9";
    
	private ArrayList<BusRoute> busRoutes = new ArrayList<BusRoute>();
	
	public boolean updates(ArrayList<BusRoute> newBusRoutes) {
		if (busRoutes.size() != newBusRoutes.size()) {
			return true;
		}
		for (int i = 0; i < busRoutes.size(); i++) {
			if (!busRoutes.get(i).getCurrentStop().equals(newBusRoutes.get(i).getCurrentStop())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<BusRoute> getBusRoutes(Date date) {
		Connection connection = null;
	    ResultSet resultSet = null;
	    ArrayList<BusRoute> busRoutes = new ArrayList<BusRoute>();

	    String currentDay = date.toString();
	    date.setDate(date.getDate() + 1);
	    String nextDay = date.toString();
	    date.setDate(date.getDate() - 1);
	    
	    try {
	    	connection = DriverManager.getConnection(URL, USER, PASSWORD);

	    	String query = "SELECT bus_route_id, bus_id, driver_id " +
	    			"FROM bus_route natural join bus " +
	    			"WHERE depart_time >= ? AND depart_time < ?";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	preparedStatement.setString(1, currentDay);
	    	preparedStatement.setString(2, nextDay);
	    	resultSet = preparedStatement.executeQuery();
	    	
			while(resultSet.next()) {
				Driver driver = new Driver(resultSet.getInt("driver_id"), connection);
				Bus bus = new Bus(resultSet.getInt("bus_id"), driver);
				BusRoute busRoute = new BusRoute(resultSet.getInt("bus_route_id"), connection);
				busRoute.setBus(bus);
				busRoutes.add(busRoute);
			}

	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } finally {
	    	try {
	    		if (connection != null && !connection.isClosed()) {
	    			connection.close();
	    		}
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	    return busRoutes;
	}
}
