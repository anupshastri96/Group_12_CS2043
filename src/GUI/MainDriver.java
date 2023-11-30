package src.GUI;

import java.sql.Date;
import java.sql.Timestamp;

import javafx.application.Application;
import src.classes.TestDriver;
import src.util.SalesReportUtils;

public class MainDriver {
	
	public static void main(String[] args) {
		
		Date date = new Date((new Timestamp(System.currentTimeMillis()).getTime()));
		
		// Launch GUI
		TestDriver hopes_and_dreams = new TestDriver();
		hopes_and_dreams.main(args);
		
		// Launch secondary driver to mimic database updates
		BusTransitGUI gui = new BusTransitGUI();
		Application.launch(gui.getClass(), args);
		SalesReportUtils Amongus = new SalesReportUtils();
		//Generate daily sales report
		Amongus.generateSalesReport(date);
	}
}
