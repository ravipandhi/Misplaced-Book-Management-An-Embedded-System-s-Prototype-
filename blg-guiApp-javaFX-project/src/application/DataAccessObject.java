package application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ravi
 */
import java.sql.*;
//import com.almworks.sqlite4java.*;

public class DataAccessObject {

    static Connection con = null;  //connection with the database server

    static Statement st = null; //handles sql statements 

    //static ResultSet rs;  //table of data
    public void ConnectJDBC() {
        int flag = 0;
        try {
            Class.forName("org.sqlite.JDBC"); //loads JDBC driver class
            con = DriverManager.getConnection("jdbc:sqlite:/home/ravipandhi/workspaceEE/blg-guiApp-javaFX-project/src/application/UniversalBooksDatabase.db"); //establishes connection
            //to the database

            st = con.createStatement();
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
            System.out.println(e);
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

   
 public ResultSet RetrieveFromDatabase(String a, String b, String c) {
        
        String sql = "SELECT Name,Author, Publication, Position FROM BookDetails where Name LIKE '%"+a+"%' AND Author LIKE '%"+b+"%' AND Publication LIKE '%"+c+"%'";
        
        try {
            ResultSet rs;
            rs = st.executeQuery(sql);
            return rs;
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }
 
 public ResultSet RetrieveFromDatabase(String a, String b) {
        
        String sql = "SELECT Name, Author, Publication, Position FROM BookDetails where "+a+" LIKE '%"+b+"%'";
        try {
            ResultSet rs;
            rs = st.executeQuery(sql);
            return rs;
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }
 
 public ResultSet RetrieveFromDatabase(String a, String b, String c, String d) {
        
        String sql = "SELECT Name,Author, Publication, Position FROM BookDetails where "+a+" LIKE '%"+b+"%' AND "+c+" LIKE '%"+d+"%'";
        try {
            ResultSet rs;
            rs = st.executeQuery(sql);
            return rs;
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }
 

    void CloseConnection() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
}

