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
	    	
	    	String query = "SELECT route_name, depart_time, method_name FROM route NATURAL JOIN bus_route RIGHT JOIN passenger ON bus_route.bus_route_id = passenger.bus_route_id INNER JOIN payment_method ON passenger.payment_method = payment_method.method_id WHERE depart_time >= ? AND depart_time < ? ORDER BY route_name, method_name";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	preparedStatement.setString(1, currentDay);
	    	preparedStatement.setString(2, nextDay);
	    	resultSet = preparedStatement.executeQuery();
	    	
			String currentSalesReportLine = "";
			String compareRouteName = "";
			int comparePaymentMethodIndex = 0;
			String comparePaymentMethod = "";
		
			String routeName = "";
			String departureTime = "";
			String paymentMethod = "";
			
			int countPaymentMethod = 0;
			int routeTotal = 0;
			int[] paymentMethodTotals = new int[paymentMethods.size()];
			
			while(resultSet.next()) {
				routeName = resultSet.getString("route_name");
				departureTime = resultSet.getString("depart_time");
				paymentMethod = resultSet.getString("method_name");
				
				if (currentSalesReportLine.isEmpty()) {
					currentSalesReportLine += routeName + "\t\t";
					if (routeName.length() < 16) {	
						currentSalesReportLine += "\t";
					}
					currentSalesReportLine += departureTime + "\t\t";
				}

				comparePaymentMethod = paymentMethods.get(comparePaymentMethodIndex);
				if (comparePaymentMethod != null && !comparePaymentMethod.equals(paymentMethod)) {
					currentSalesReportLine += countPaymentMethod + "\t\t\t\t";
					paymentMethodTotals[comparePaymentMethodIndex] = paymentMethodTotals[comparePaymentMethodIndex] + countPaymentMethod;
					countPaymentMethod = 0;
					comparePaymentMethodIndex++;
					if (comparePaymentMethodIndex >= paymentMethods.size()) {
						comparePaymentMethodIndex = 0;
					}
				}
				if (comparePaymentMethod != null && comparePaymentMethod.equals(paymentMethod)) {
					countPaymentMethod ++;
					routeTotal++;
				}
				
				if (!routeName.equals(compareRouteName)) {
					if (comparePaymentMethod.equals(paymentMethod)) {
						currentSalesReportLine += countPaymentMethod + "\t\t\t\t";
						paymentMethodTotals[comparePaymentMethodIndex] = paymentMethodTotals[comparePaymentMethodIndex] + countPaymentMethod;
						countPaymentMethod = 0;
						comparePaymentMethodIndex++;
					}
					while(comparePaymentMethodIndex < paymentMethods.size()) {
						comparePaymentMethod = paymentMethods.get(comparePaymentMethodIndex);
						if (comparePaymentMethod.equals(paymentMethod)) {
							countPaymentMethod++;
							routeTotal++;
						}
						currentSalesReportLine += countPaymentMethod + "\t\t\t\t";
						paymentMethodTotals[comparePaymentMethodIndex] = paymentMethodTotals[comparePaymentMethodIndex] + countPaymentMethod;
						countPaymentMethod = 0;
						comparePaymentMethodIndex++;
					}
					currentSalesReportLine += routeTotal;
					routeTotal = 0;
					lines.add(currentSalesReportLine);
					currentSalesReportLine = "";
					comparePaymentMethodIndex = 0;
				} 
			}
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
	    			System.out.println("Connection closed.");
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
	    	
	    	String query = "SELECT stop_location, method_name FROM stop inner join passenger ON stop.stop_id = passenger.boarded_stop LEFT JOIN bus_route ON bus_route.bus_route_id = passenger.bus_route_id NATURAL JOIN route INNER JOIN payment_method on passenger.payment_method = payment_method.method_id where route_name = ? and depart_time = ?;";
	    	PreparedStatement preparedStatement = connection.prepareStatement(query);
	    	preparedStatement.setString(1, routeName);
	    	preparedStatement.setString(2, departTime);
	    	resultSet = preparedStatement.executeQuery();
	    	
			String currentSalesReportLine = "";
			String compareStopName = "";
			int comparePaymentMethodIndex = 0;
			String comparePaymentMethod = "";
		
			String stopName = "";
			String paymentMethod = "";
			
			int countPaymentMethod = 0;
			int stopTotal = 0;
			int[] paymentMethodTotals = new int[paymentMethods.size()];
			
			while(resultSet.next()) {
				stopName = resultSet.getString("stop_location");
				paymentMethod = resultSet.getString("method_name");
				
				if (currentSalesReportLine.isEmpty()) {
					currentSalesReportLine += stopName;
					for (int i = 0; i < 12 - (int)(stopName.length() / 4) ; i++) {
						currentSalesReportLine += "\t";
					}
				}
	
				comparePaymentMethod = paymentMethods.get(comparePaymentMethodIndex);
				if (comparePaymentMethod != null && !comparePaymentMethod.equals(paymentMethod)) {
					currentSalesReportLine += countPaymentMethod + "\t\t\t\t";
					paymentMethodTotals[comparePaymentMethodIndex] = paymentMethodTotals[comparePaymentMethodIndex] + countPaymentMethod;
					countPaymentMethod = 0;
					comparePaymentMethodIndex++;
					if (comparePaymentMethodIndex >= paymentMethods.size()) {
						comparePaymentMethodIndex = 0;
					}
				}
				if (comparePaymentMethod != null && comparePaymentMethod.equals(paymentMethod)) {
					countPaymentMethod ++;
					stopTotal++;
				}
				
				if (!stopName.equals(compareStopName)) {
					if (comparePaymentMethod.equals(paymentMethod)) {
						currentSalesReportLine += countPaymentMethod + "\t\t\t\t";
						paymentMethodTotals[comparePaymentMethodIndex] = paymentMethodTotals[comparePaymentMethodIndex] + countPaymentMethod;
						countPaymentMethod = 0;
						comparePaymentMethodIndex++;
					}
					while(comparePaymentMethodIndex < paymentMethods.size()) {
						comparePaymentMethod = paymentMethods.get(comparePaymentMethodIndex);
						if (comparePaymentMethod.equals(paymentMethod)) {
							countPaymentMethod++;
							stopTotal++;
						}
						currentSalesReportLine += countPaymentMethod + "\t\t\t\t";
						paymentMethodTotals[comparePaymentMethodIndex] = paymentMethodTotals[comparePaymentMethodIndex] + countPaymentMethod;
						countPaymentMethod = 0;
						comparePaymentMethodIndex++;
					}
					currentSalesReportLine += stopTotal;
					stopTotal = 0;
					lines.add(currentSalesReportLine);
					currentSalesReportLine = "";
					comparePaymentMethodIndex = 0;
				} 
			}
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
	    			System.out.println("Connection closed.");
	    		}
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	    return lines;
	}
}
