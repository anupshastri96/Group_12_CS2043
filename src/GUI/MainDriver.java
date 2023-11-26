package GUI;

import java.sql.Date;
import java.sql.Timestamp;

import javafx.application.Application;
import util.SalesReportUtils;

public class MainDriver {
	
	public static void main(String[] args) {
		
		Date date = new Date((new Timestamp(System.currentTimeMillis()).getTime()));
		
		// Launch GUI
		BusTransitGUI gui = new BusTransitGUI();
		Application.launch(gui.getClass(), args);
		
		// Launch secondary driver to mimic database updates
		// ***
		
		// Generate daily sales report
		SalesReportUtils.generateSalesReport(date);
	}
}
