package src.util;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesReportDataUtils {
	private static final String URL = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
	private static final String USER = "sql9657484";
	private static final String PASSWORD = "e8X5f44Fl9";
	
	public static ArrayList<String> getRoutesAndTimes(Date date) {
		Connection connection = null;
	    ResultSet resultSet = null;
	    ArrayList<String> routeNames = new ArrayList<String>();
	    
	    String currentDay = date.toString();
	    date.setDate(date.getDate() + 1);
	    String nextDay = date.toString();
	    date.setDate(date.getDate() - 1);
	    
	    try {
	    	connection = DriverManager.getConnection(URL, USER, PASSWORD);
	    	String query = "SELECT route_name, depart_time FROM route NATURAL JOIN bus_route WHERE depart_time >= ? AND depart_time < ? order by route_name;";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	preparedStatement.setString(1, currentDay);
	    	preparedStatement.setString(2, nextDay);
	    	resultSet = preparedStatement.executeQuery();
	    	
	    	while(resultSet.next()) {
	    		routeNames.add(resultSet.getString("route_name") + "\t" + resultSet.getString("depart_time"));
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
	    return routeNames;
	}
	
	public static ArrayList<String> getPaymentMethods() {
		Connection connection = null;
	    ResultSet resultSet = null;
	    ArrayList<String> paymentMethods = new ArrayList<String>();
	    
	    try {
	    	connection = DriverManager.getConnection(URL, USER, PASSWORD);
	    	String query = "SELECT method_name FROM payment_method ORDER BY method_name;";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	resultSet = preparedStatement.executeQuery();
	    	
	    	while(resultSet.next()) {
	    		paymentMethods.add(resultSet.getString("method_name"));
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
	    return paymentMethods;
	}
	
	public static ArrayList<String> getDailyInfo(Date date, ArrayList<String> paymentMethods) {
	    Connection connection = null;
	    ResultSet resultSet = null;
	    ArrayList<String> lines = new ArrayList<String>();
	    
	    String currentDay = date.toString();
	    date.setDate(date.getDate() + 1);
	    String nextDay = date.toString();
	    date.setDate(date.getDate() - 1);

	    try {
	    	connection = DriverManager.getConnection(URL, USER, PASSWORD);
	    	
	    	// Retrieve all payment methods used on routes from the current day
	    	String query = "SELECT route_name, depart_time, method_name " +
	    			"FROM route NATURAL JOIN bus_route RIGHT JOIN passenger ON bus_route.bus_route_id = passenger.bus_route_id INNER JOIN payment_method ON passenger.payment_method = payment_method.method_id " +
	    			"WHERE depart_time >= ? AND depart_time < ? ORDER BY route_name, method_name";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	preparedStatement.setString(1, currentDay);
	    	preparedStatement.setString(2, nextDay);
	    	resultSet = preparedStatement.executeQuery();
	    	
			String currentSalesReportLine = "";
			String compareRouteName = "";
		
			String routeName = "";
			String departureTime = "";
			String paymentMethod = "";
			
			int routeTotal = 0;
			int[] routeTotals = null;
			int[] paymentMethodTotals = new int[paymentMethods.size()];
			
			while(resultSet.next()) {
				routeName = resultSet.getString("route_name");
				departureTime = resultSet.getString("depart_time");
				paymentMethod = resultSet.getString("method_name");
				
				// Starting a new line in the sales report
				if (!routeName.equals(compareRouteName)) {
					if (routeTotals != null) {
						for (int i = 0; i < routeTotals.length; i++ ) {
							currentSalesReportLine += routeTotals[i] + "\t\t\t\t";
						}
						currentSalesReportLine += routeTotal;
						lines.add(currentSalesReportLine);
					}
					
					routeTotal = 0;
					compareRouteName = routeName;
					routeTotals = new int[paymentMethods.size()];
					currentSalesReportLine = "";
					currentSalesReportLine += routeName;
					for (int i = 24 - routeName.length(); i > 0; i -= 4) {
						currentSalesReportLine += "\t";
					}
					currentSalesReportLine += departureTime + "\t\t";
				}
				
				// Check for which payment method was used 
				for (int i = 0; i < paymentMethods.size(); i++) {
					if (paymentMethod.equals(paymentMethods.get(i))) {
						routeTotal++;
						routeTotals[i] = routeTotals[i] + 1;
						paymentMethodTotals[i] = paymentMethodTotals[i] + 1;
					}
				}
			}
			if (routeTotals != null) {
				for (int i = 0; i < routeTotals.length; i++ ) {
					currentSalesReportLine += routeTotals[i] + "\t\t\t\t";
				}
			}
			currentSalesReportLine += routeTotal;
			lines.add(currentSalesReportLine);
			
			currentSalesReportLine = "\nTotal\t\t\t\t\t\t\t\t\t\t\t";
			int overallTotal = 0;
			for (int i = 0; i < paymentMethodTotals.length; i++) {
				overallTotal += paymentMethodTotals[i];
				currentSalesReportLine += paymentMethodTotals[i] + "\t\t\t\t";
			}
			currentSalesReportLine += overallTotal;
			lines.add(currentSalesReportLine);
					
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
	    return lines;
	}
	
	public static ArrayList<String> getPerRouteInfo(String routeName, String departTime, ArrayList<String> paymentMethods) {
		Connection connection = null;
	    ResultSet resultSet = null;
	    ArrayList<String> lines = new ArrayList<String>();
	
	    try {
	    	connection = DriverManager.getConnection(URL, USER, PASSWORD);
	    	
	    	// Retrieve all payment methods used on a specific bus route
	    	String query = "SELECT stop_location, method_name " +
	    			"FROM stop inner join passenger ON stop.stop_id = passenger.boarded_stop LEFT JOIN bus_route ON bus_route.bus_route_id = passenger.bus_route_id NATURAL JOIN route INNER JOIN payment_method on passenger.payment_method = payment_method.method_id " +
	    			"WHERE route_name = ? and depart_time = ?;";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	preparedStatement.setString(1, routeName);
	    	preparedStatement.setString(2, departTime);
	    	resultSet = preparedStatement.executeQuery();
	    	
			String currentSalesReportLine = "";
			String compareStopName = "";
		
			String stopName = "";
			String paymentMethod = "";
			
			int stopTotal = 0;
			int[] stopTotals = null;
			int[] paymentMethodTotals = new int[paymentMethods.size()];
			
			while(resultSet.next()) {
				stopName = resultSet.getString("stop_location");
				paymentMethod = resultSet.getString("method_name");
				
				if (!routeName.equals(compareStopName)) {
					if (stopTotals != null) {
						for (int i = 0; i < stopTotals.length; i++ ) {
							currentSalesReportLine += stopTotals[i] + "\t\t\t\t";
						}
						currentSalesReportLine += stopTotal;
						lines.add(currentSalesReportLine);
					}
					
					stopTotal = 0;
					compareStopName = stopName;
					stopTotals = new int[paymentMethods.size()];
					currentSalesReportLine = "";
					currentSalesReportLine += stopName;
					for (int i = 48 - stopName.length(); i > 0 ; i -= 4) {
						currentSalesReportLine += "\t";
					}
				}
				
				for (int i = 0; i < paymentMethods.size(); i++) {
					if (paymentMethod.equals(paymentMethods.get(i))) {
						stopTotal++;
						stopTotals[i] = stopTotals[i] + 1;
						paymentMethodTotals[i] = paymentMethodTotals[i] + 1;
					}
				}
			}
			if (stopTotals != null) {
				for (int i = 0; i < stopTotals.length; i++ ) {
					currentSalesReportLine += stopTotals[i] + "\t\t\t\t";
				}
			}
			currentSalesReportLine += stopTotal;
			lines.add(currentSalesReportLine);
			
			currentSalesReportLine = "\nTotal\t\t\t\t\t\t\t\t\t\t\t";
			int overallTotal = 0;
			for (int i = 0; i < paymentMethodTotals.length; i++) {
				overallTotal += paymentMethodTotals[i];
				currentSalesReportLine += paymentMethodTotals[i] + "\t\t\t\t";
			}
			currentSalesReportLine += overallTotal;
			lines.add(currentSalesReportLine);
					
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
	    return lines;
	}
}
