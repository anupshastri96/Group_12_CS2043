package miniproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

            // Query to retrieve data from the stop table
            String query = "SELECT stop_id, stop_location FROM stop";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                // Process the result set and create Stop objects
                while (resultSet.next()) {
                    int stopId = resultSet.getInt("stop_id");
                    String stopLocation = resultSet.getString("stop_location");
                    int expectedArrivalTime = 5;

                    // Create Stop objects using the retrieved data
                    Stop stop = new Stop(stopId, stopLocation, expectedArrivalTime);

                    // Print or process the Stop object as needed
                    System.out.println(stop.getLocation());
                }
            }
            String query2 = "SELECT method_id, method_name, method_price FROM payment_method";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query2);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                // Process the result set and create PaymentMethod objects
                while (resultSet.next()) {
                    int methodId = resultSet.getInt("method_id");
                    String methodName = resultSet.getString("method_name");
                    double methodPrice = resultSet.getDouble("method_price");

                    // Create PaymentMethod objects using the retrieved data
                    PaymentMethod paymentMethod = new PaymentMethod(methodId, methodName, methodPrice);

                    // Print or process the PaymentMethod object as needed
                    System.out.println(paymentMethod.getType());
                }
            }
            
            String query3 = "SELECT driver_id, driver_name, driver_salary FROM driver";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query3);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                // Process the result set and create Driver objects
                while (resultSet.next()) {
                    int driverId = resultSet.getInt("driver_id");
                    String driverName = resultSet.getString("driver_name");
                    double driverSalary = resultSet.getDouble("driver_salary");

                    // Create Driver object using retrieved data
                    Driver driver = new Driver(driverId, driverName, driverSalary);

                    // Print or process the Driver object as needed
                    System.out.println(driver.getName());
                }
            }
           
            Stop stop = new Stop(connection, 111); // Assuming 111 is the stopId you want to retrieve
            System.out.println(stop.getLocation() + "Tsticle");
            
            PaymentMethod paymentMethod = new PaymentMethod(connection, 1); // Assuming 1 is the paymentMethodId you want to retrieve
            System.out.println(paymentMethod.getType());
            
             Driver d1 = new Driver(101, connection);
             System.out.println(d1.getSalary());
             d1.setSalary(6969, connection);
             System.out.println(d1.getSalary());
             System.out.println(d1.getId());
             Route r1 = new Route(11, connection);
             System.out.println();
             for (Stop element : r1.getStops()) {
                 System.out.println(element.getLocation());
             }
             
             BusRoute bR1 = new BusRoute(110, connection);
             System.out.println(bR1.getRoute().getName());
             for (Passenger element : bR1.getPassengers()) {
                 element.getPaymentMethod().setPrice(69.69, connection);
             }
             
             
             
             Bus b1 = new Bus(d1, connection);
             
             System.out.println(b1.getDriver().getName());
        } catch (SQLException e) {
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


