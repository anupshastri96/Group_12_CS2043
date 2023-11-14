package jdbctest;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

	public class DBTest {
	    public static void main(String[] args) {
	    	

	        // JDBC URL, username, and password of MySQL server
	        String url = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
	        String user = "sql9657484";
	        String password = "e8X5f44Fl9";
	        
	        // JDBC variables for opening, closing, and managing connection
	        Connection connection = null;

	        try {
	        	
	            // Establishing a connection
	            connection = DriverManager.getConnection(url, user, password);

	            // Your code for database operations goes here

	            // Example: Print a message if the connection is successful
	            System.out.println("Connected to the database!");

	        } catch (Exception e) {
	            // Handle any SQL errors
	            e.printStackTrace();
	        } finally {
	            // Close the connection in the finally block to ensure it's always closed
	            try {
	                if (connection != null && !connection.isClosed()) {
	                    connection.close();
	                    System.out.println("Connection closed.");
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

