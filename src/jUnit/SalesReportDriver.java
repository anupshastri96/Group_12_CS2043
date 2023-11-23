import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesReportDriver {
	private static final String URL = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
	private static final String USER = "sql9657484";
	private static final String PASSWORD = "e8X5f44Fl9";
	
	public static void generateSalesReport(Date reportDate) {
		ArrayList<String> paymentMethods = getPaymentMethods();
		String fileName = "SalesReport-" + reportDate.toString();
		
		try {
			FileWriter myWriter = new FileWriter(fileName);
			myWriter.write("Daily Sales Report " + reportDate);
			myWriter.write("\n\nRoute\t\t\t\t\tDeparture Time");
			myWriter.write(header(paymentMethods));
			
			for(String s : SalesReportDataUtils.getDailyInfo(reportDate, paymentMethods)) {
				myWriter.write(s + "\n");
			}
			
			ArrayList<String> routeNamesAndTimes = getRoutesAndTimes(reportDate);
			for(String r : routeNamesAndTimes) {
				myWriter.write("\n\n" + r);
				if (r.length() < 36) {
					myWriter.write("\t");
				}
				myWriter.write(header(paymentMethods));
				int nameCutoff = r.length() - 20;
				String name = r.substring(0, nameCutoff);
				String time = r.substring(nameCutoff + 1);
				ArrayList<String> perRouteLines = SalesReportDataUtils.getPerRouteInfo(name, time, paymentMethods);
				for(String s : perRouteLines) {
					myWriter.write(s + "\n");
				}
			}
			
			
			myWriter.close();
			System.out.println("Successfully output sales report.");
		} catch (IOException e) {
			System.out.println("A file error occurred.");
			e.printStackTrace();
		}
		
	}
	
	private static String header(ArrayList<String> paymentMethods) {
		String header = "";
		for (String s : paymentMethods) {
			header += "\t\t\t" + s;
		}
		header += "\t\t\tTotal\n";
		return header;
	}
		
	private static ArrayList<String> getPaymentMethods() {
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
	    			System.out.println("Connection closed.");
	    		}
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	    return paymentMethods;
	}
	
	private static ArrayList<String> getRoutesAndTimes(Date date) {
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
	    			System.out.println("Connection closed.");
	    		}
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	    return routeNames;
	}
	
}
