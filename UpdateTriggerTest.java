import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UpdateTriggerTest {

    public void testing() {
        System.out.println("Update Success");
    }
public static void main(String[] args) {
    
Connection connector = openConnection();
if (connector == null)
{ System.err.println("Unable to connect to the database");
System.exit(1);
}
try{Thread.currentThread().join();}
catch(Exception e){
    e.printStackTrace();
}

closeConnection(connector);

} // end main()
/************************** openConnection() ******************************/

private static Connection openConnection() 
{ final String url = "jdbc:mysql://sql9.freesqldatabase.com:3306/sql9657484";
final String user = "sql9657484";
final String password = "e8X5f44Fl9";
Connection conn = null;
try
{ conn = DriverManager.getConnection(url, user, password);
} 
catch (Exception e)
{ System.err.println("Couldn't open a connection: " + e.getMessage());
}
return conn;
}
/************************** closeConnection() *****************************/
private static void closeConnection(Connection connector)
{ try
{ connector.close();
} 
catch (Exception e) 
{ System.err.println("Couldn't close connection: " + e.getMessage());
}
}

}
