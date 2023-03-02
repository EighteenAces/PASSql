
/**********************************************************************************
 * Java Course 4 Capstone Project
 * 
 * Automobile Insurance Policy and Claims Administration System (PAS) Specification
 * 
 * 
 * @author: Jellie Mae Ortiz
 **********************************************************************************/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static String url = "jdbc:mysql://localhost:3306/";
    private static String db_name = "CAPS";
    private static String username = "root";
    private static String password = "Root123";

    private static Connection con;

    /* for database connection */
    public static Connection dbcon() {

        try {
            con = DriverManager.getConnection(url, username, password);
            try {
                createDB();
                createDBTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /* the program will automatically create database */
    public static void createDB() throws Exception {

        String sqlCreateDB = "CREATE DATABASE IF NOT EXISTS " + db_name + ";";
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sqlCreateDB);
            con = DriverManager.getConnection(url + db_name, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
    }

    /* create database tables */
    public static void createDBTable() throws Exception {
        String useDB = "USE " + db_name;
        String sqlCustomerTable = "CREATE TABLE IF NOT EXISTS CUSTOMERDETAILS "
                + "(NUM int AUTO_INCREMENT NOT NULL UNIQUE, "
                + "ACCOUNTNUMBER VARCHAR(10) NOT NULL, "
                + "FIRSTNAME VARCHAR(50) NOT NULL, "
                + "LASTNAME varchar(50) NOT NULL, "
                + "ADDRESS varchar(100) NOT NULL, "
                + "PRIMARY KEY(ACCOUNTNUMBER));";
        String sqlPolicyTable = "CREATE TABLE IF NOT EXISTS POLICY "
                + "(NUM int AUTO_INCREMENT NOT NULL UNIQUE, "
                + "POLICY_NUM VARCHAR(10) NOT NULL, "
                + "START_DATE DATE NOT NULL, "
                + "END_DATE DATE NOT NULL, "
                + "ACCOUNTNUMBER VARCHAR(10), "
                + "TOTAL_PREMIUMCOST decimal(11,2), "
                + "STATUS VARCHAR(10) DEFAULT 'ACTIVE', "
                + "CONSTRAINT FOREIGN KEY (ACCOUNTNUMBER) REFERENCES CUSTOMERDETAILS(ACCOUNTNUMBER), "
                + "PRIMARY KEY (POLICY_NUM));";
        String sqlPoHolderTable = "CREATE TABLE IF NOT EXISTS POLICYHOLDER "
                + "(POLICYHOLDER_ID int(10) NOT NULL AUTO_INCREMENT, "
                + "FIRST_NAME VARCHAR(50) NOT NULL, "
                + "LAST_NAME VARCHAR(50) NOT NULL, "
                + "DATE_OF_BIRTH DATE, "
                + "ADDRESS VARCHAR(100), "
                + "DRIVERS_LICENSENUM VARCHAR(50), "
                + "DATEISSUED DATE NOT NULL, "
                + "ACCOUNTNUMBER VARCHAR(10), "
                + "POLICY_NUM VARCHAR(10), "
                + "PRIMARY KEY(POLICYHOLDER_ID), "
                + "FOREIGN KEY (ACCOUNTNUMBER) REFERENCES CUSTOMERDETAILS(ACCOUNTNUMBER));";
        String sqlVehicleTable = "CREATE TABLE IF NOT EXISTS VEHICLE "
                + "(VEHICLE_ID INT NOT NULL AUTO_INCREMENT, "
                + "VEHICLE_MAKE VARCHAR(50), "
                + "VEHICLE_MODEL VARCHAR(50), "
                + "VEHICLE_YEAR INT NOT NULL, "
                + "VEHICLE_TYPE VARCHAR(50), "
                + "VEHICLE_FUELTYPE VARCHAR(50), "
                + "VEHICLE_PRICE DOUBLE, "
                + "VEHICLE_COLOR VARCHAR(50), "
                + "VEHICLE_PREMIUMCHARGED DECIMAL(11,2), "
                + "POLICYHOLDER_ID INT(10), "
                + "POLICY_NUM varchar(10), "
                + "CONDUCTION_NUM varchar(50), "
                + "CLAIM_STATUS varchar(45) DEFAULT 'NOT FILED', "
                + "PRIMARY KEY (VEHICLE_ID));";
        String sqlClaimTable = "CREATE TABLE IF NOT EXISTS CLAIM "
                + "(ID INT AUTO_INCREMENT NOT NULL, "
                + "CLAIMID VARCHAR(25) NOT NULL, "
                + "DATEOFACCIDENT DATE NOT NULL, "
                + "ADDRESSOFACCIDENT varchar(100) NOT NULL, "
                + "ACCIDENTDESC VARCHAR(255) NOT NULL, "
                + "DAMAGEDESC VARCHAR(255) NOT NULL, "
                + "COSTOFREPAIRS DOUBLE, "
                + "POLICY_NUM varchar(10), "
                + "CONDUCTION_NUM varchar(45), "
                + "CONSTRAINT UNIQUE KEY(CLAIMID), "
                + "CONSTRAINT FOREIGN KEY (POLICY_NUM) REFERENCES POLICY(POLICY_NUM), "
                + "PRIMARY KEY (ID));";

        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(useDB); // execute the useDB query
            con = DriverManager.getConnection(url + db_name, username, password);

            stmt.execute(sqlCustomerTable); // create customer table
            stmt.execute(sqlPolicyTable); // create policy table
            stmt.execute(sqlPoHolderTable); // create policy holder table
            stmt.execute(sqlVehicleTable); // create vehicle table
            stmt.execute(sqlClaimTable); // create claim table
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
    }

    public void closeConnection(Connection con, PreparedStatement stmt) throws SQLException {

        stmt.close();
        con.close();
    }

}