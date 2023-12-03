package src.GUI;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javafx.application.Application;
import src.classes.TestDriver;
import src.util.SalesReportUtils;

public class MainDriver {
	
	public static void main(String[] args) {
		
		Date date = new Date((new Timestamp(System.currentTimeMillis()).getTime()));
		
		// Launch GUI
		TestDriver driver = new TestDriver();
		driver.main(args);
		
		// Launch secondary driver to mimic database updates
		BusTransitGUI gui = new BusTransitGUI();
		Application.launch(gui.getClass(), args);
		SalesReportUtils sales = new SalesReportUtils();
		//Generate daily sales report
		sales.generateSalesReport(date);
		
		
		
        	try(Connection connection = DriverManager.getConnection("jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484", "sql9657484", "e8X5f44Fl9");){
        	
        	Statement statement = connection.createStatement();

            // Execute SQL statements
            statement.executeUpdate("delete from `sql9657484`.`passenger` where passenger_id < 120");
            statement.executeUpdate("delete from `sql9657484`.`route_stop` where stop_id < 280");
            statement.executeUpdate("delete from `sql9657484`.`stop` where stop_id < 280");
            statement.executeUpdate("delete from `sql9657484`.`bus_route` where bus_route_id < 2001");
            statement.executeUpdate("delete from `sql9657484`.`bus_route_passenger` where passenger_id < 120");
            statement.executeUpdate("delete from `sql9657484`.`bus` where bus_id < 100");
            statement.executeUpdate("delete from `sql9657484`.`route` where route_id < 100");
            statement.executeUpdate("delete from `sql9657484`.`driver` where driver_id < 100");
            System.out.println("Database Test Data Cleared");
        	}
        	catch (SQLException e) {
            e.printStackTrace();
        }
        
	}
	
	
}
	