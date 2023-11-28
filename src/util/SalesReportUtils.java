package src.util;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

public class SalesReportUtils {
	
	public static void generateSalesReport(Date reportDate) {
		ArrayList<String> paymentMethods = SalesReportDataUtils.getPaymentMethods();
		String fileName = "SalesReport-" + reportDate.toString();
		
		try {
			FileWriter myWriter = new FileWriter(fileName);
			myWriter.write("Daily Sales Report " + reportDate);
			myWriter.write("\n\nRoute\t\t\t\t\tDeparture Time");
			myWriter.write(header(paymentMethods));
			
			for(String s : SalesReportDataUtils.getDailyInfo(reportDate, paymentMethods)) {
				myWriter.write(s + "\n");
			}
			
			ArrayList<String> routeNamesAndTimes = SalesReportDataUtils.getRoutesAndTimes(reportDate);
			for(String r : routeNamesAndTimes) {
				myWriter.write("\n\n" + r);
				for (int i = 36 - r.length(); i > 0; i -= 4) {
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
	
}
