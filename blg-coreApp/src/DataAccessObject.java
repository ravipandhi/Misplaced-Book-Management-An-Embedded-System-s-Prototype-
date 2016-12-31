
import java.sql.*;
import org.sqlite.JDBC;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;


public class DataAccessObject extends MisplcdBookMngController{

    static Connection con = null;  //connection with the database server

    static Statement st = null; //handles sql statements 

    public void ConnectJDBC() {
        System.out.println("Into the ConnectJDBC method");
        System.out.println("Before try");
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.LOW) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                        System.out.println("Stop Scanning");
                        System.exit(0);
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                }
            }

        });
        int flag = 0;
        try {
            System.out.println("In try block");
            Class.forName("org.sqlite.JDBC"); //loads JDBC driver class at run time. this string is unique for different database systems
            System.out.println("After class.forname");
            con = DriverManager.getConnection("jdbc:sqlite:UniversalBooksDatabase.db"); //establishes connection to the database
            System.out.println("After connection establishmnet");
            //to the database
            st = con.createStatement();  // Statement for handling sql queries
        } catch (Exception e) {
            flag = 1;
            System.out.println("Could not connect to the database");
            e.printStackTrace();
        }
        if (flag == 0) {
            System.out.println("Connected to the database");
        }
    }

    public void CreateTable() {
        
        int flag = 0;
        String sql = "CREATE TABLE BookDetails"
                + "(EPCcode INT     VARCHAR(15) PRIMARY KEY,"
                + " Name            VARCHAR(255)     NOT NULL, "
                + " Author        VARCHAR(255), "
                + " Publication      VARCHAR(255), "
                + " IssuedStatus       VARCHAR(10))";
        try {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            flag = 1;
            System.out.println("Could not create the table");
            e.printStackTrace();
        }
        if (flag == 0) {
            System.out.println("Table Created");
        }
    }

    public void InsertIntoTable(String a[]) {
        int flag = 0;
        String sql1 = "INSERT INTO BookDetails"
                + "(EPCcode,Name,Author,Publication,IssuedStatus)"
                + "values('" + a[0] + "'"
                + ",'" + a[1] + "'"
                + ",'" + a[2] + "'"
                + ",'" + a[3] + "'"
                + ",'" + a[4] + "')";
        try {
            st.executeUpdate(sql1);
        } catch (Exception e) {
            flag = 1;
            System.out.println("Record not inserted");
            System.out.println(e);
        }
        if (flag == 0) {
            System.out.println("Record Inserted");
        }
    }

 

 public ResultSet RetrieveFromDatabase(String a,String b,String c) {

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        String sql = "SELECT "+a+","+b+" FROM BookDetails where EPCcode='"+c+"'";
        try {
            ResultSet rs;
            rs = st.executeQuery(sql);
            return rs;
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }  
 
  public int CheckExistence(String a,String b) {

        String sql = "SELECT * FROM BookDetails WHERE EPCcode = '"+a+"' AND Position='"+b+"'";
        try {
            ResultSet rs;
            rs = st.executeQuery(sql);
            if(rs.next())
            {
                return 1;
            }
            else 
            {
                return 0;
            }    
        } catch (SQLException ex) {
            System.out.println("Exception in checkExistence");
            System.out.println(ex);
        }
        return 0;
     
    }  
 
    void CloseConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    
}
