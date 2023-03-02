
/**********************************************************************************
 * Java Course 4 Capstone Project
 * 
 * Automobile Insurance Policy and Claims Administration System (PAS) Specification
 * 
 * 
 * @author: Jellie Mae Ortiz
 **********************************************************************************/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

public class Policy {

    private static String policyNum = getRandomNumberString();
    private static String effectiveDate, expirationDate, expDate, accountID;
    private static int tempID, getCount, pNum; // tempID is temporary ID used for getting the policy number
    private static double totalPremiumCost;
    private String answer, status;

    private Connection con;
    private String sqlQuery;
    private ResultSet rs;
    private PreparedStatement stmt;

    Scanner scan = new Scanner(System.in);
    DecimalFormat df = new DecimalFormat("0.00");

    public void availPolicy() throws SQLException, ParseException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        Vehicle v = new Vehicle();
        this.updateStatus(); /*
                              * method for automatically updating the status of the policy
                              * if the scheduled status will be active / otherwise expired
                              */

        System.out.println("***************************************************************************\n");
        System.out.println("****************************** GET POLICY QUOTE ***************************\n");

        try {
            System.out.printf("Enter your account number: ");
            accountID = scan.nextLine();

            // checks if the accountnumber already exists
            sqlQuery = "SELECT EXISTS (SELECT ACCOUNTNUMBER FROM CUSTOMERDETAILS WHERE ACCOUNTNUMBER=?) AS EXISTCOL";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, accountID);
            rs = stmt.executeQuery();

            if (rs.next()) {

                int existCol = rs.getInt("EXISTCOL");

                if (existCol == 1) {

                    sqlQuery = "SELECT * FROM CUSTOMERDETAILS WHERE ACCOUNTNUMBER=?";
                    stmt = con.prepareStatement(sqlQuery);
                    stmt.setString(1, accountID);
                    rs = stmt.executeQuery();

                    if (rs.next()) {

                        // retrieving the values stored in the database
                        String aNum = rs.getString("ACCOUNTNUMBER");
                        String fName = rs.getString("FIRSTNAME");
                        String lName = rs.getString("LASTNAME");
                        String address = rs.getString("ADDRESS");
                        String fullName = fName.substring(0, 1).toUpperCase() +
                                fName.substring(1) + " " + lName.substring(0, 1).toUpperCase()
                                + lName.substring(1);

                        System.out.println("Account Number: " + aNum);
                        System.out.println("Customer's Name: " + fullName);
                        System.out.println("Address: " + address.substring(0, 1).toUpperCase()
                                + address.substring(1));

                    }
                    System.out.println("\nAre the above customer details correct? (Y/N) ");
                    answer = scan.next();

                    if (answer.equalsIgnoreCase("y")) {
                        scan.nextLine();
                        System.out.print("\033[H\033[2J"); // clear screen

                        System.out.println(
                                "***************************************************************************\n");
                        String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";

                        /* validation and user input for effective date */
                        do {
                            try {
                                System.out.printf("Enter effective date in this format (YYYY-MM-DD): ");
                                effectiveDate = scan.nextLine();

                                if (effectiveDate.matches(regex)) {
                                    this.getStatusEffectiveDate();
                                    
                                } else if (effectiveDate.isEmpty()) {
                                    System.out.println(
                                            "Required field!!! Please enter a valid effective date.");
                                } else {
                                    System.out.println(
                                            "You have entered an invalid format of effective date. Please try again.");
                                }
                            } catch (DateTimeException d) {
                                System.out.println("Invalid date format!! Please try again.");
                                d.printStackTrace();
                                this.availPolicy();
                            }
                        } while (!effectiveDate.matches(regex));

                        v.loadInput();// policyholder input data
                        v.load(); // vehicle input data

                        // displaying the preview of the policy quotation before proceeding/availing
                        System.out
                                .println("***************************************************************************");

                        sqlQuery = "SELECT * FROM CUSTOMERDETAILS WHERE ACCOUNTNUMBER=?";
                        stmt = con.prepareStatement(sqlQuery);
                        stmt.setString(1, accountID);
                        rs = stmt.executeQuery();

                        while (rs.next()) {

                            String fName = rs.getString("FIRSTNAME");
                            String lName = rs.getString("LASTNAME");
                            String full = fName + " " + lName;
                            String phFull = v.getfName() + " " + v.getLName();

                            System.out.printf("* %-52s *\n", "\t\t\tReview your Policy Quote");
                            System.out.printf("* %-15s %-25s %-15s %-10s*\n", "Policy Number:", "#" + getPolicyNum(),
                                    "Total Premium Cost:", "$" + df.format(v.getTotalPremium()));
                            System.out.printf("* %-13s %-22s %-11s %-17s*\n", "Customer's Name:", full,
                                    "Policy Holder:", phFull);
                            System.out
                                    .println(
                                            "***************************************************************************");

                        }

                        System.out.println(
                                "Are you sure you want to avail this policy number #" + getPolicyNum() + " ? (Y/N)");
                        answer = scan.next();

                        if (answer.equalsIgnoreCase("y")) {

                            v.savePolicyHolderData(); // calls the method for saving the policy holder details
                            v.saveVehicleData(); // calls the method for saving the vehicle details

                            // saves the policy and sets the effective date & expiration date with status
                            sqlQuery = "INSERT INTO POLICY(policy_num, start_date, end_date, accountnumber, total_premiumcost, status) VALUES ('"
                                    + getPolicyNum() + "', '"
                                    + getEffectiveDate() + "', '"
                                    + expirationDate + "', '"
                                    + accountID + "', "
                                    + v.getTotalPremium() + ","
                                    + "'" + status + "'" + ")";
                            stmt.execute(sqlQuery);
                            
                   

                            System.out.println(" Your policy no. " + getPolicyNum() + " has been added to your account."
                                    + "\n Please take note of your policy number!\n");

                            System.out.println(
                                    "*****************************POLICY QUOTATION**********************************");

                            // this is for the view of the policy quotation that has been bought
                            sqlQuery = "SELECT FIRSTNAME, LASTNAME FROM CUSTOMERDETAILS WHERE ACCOUNTNUMBER=?";
                            stmt = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE,
                                    ResultSet.CONCUR_UPDATABLE);
                            stmt.setString(1, accountID);
                            rs = stmt.executeQuery();

                            if (rs.next()) {

                                /* retrieves column/fields values to display */
                                String fName = rs.getString("FIRSTNAME");
                                String lName = rs.getString("LASTNAME");
                                String fullname = fName.substring(0, 1).toUpperCase()
                                        + fName.substring(1) + " " + lName.substring(0, 1).toUpperCase()
                                        + lName.substring(1);
                                String pFullname = v.getfName() + " " + v.getLName();

                                System.out.printf("* %-15s %-31s*\n", "\t\t\t\tPolicy Number:", getPolicyNum());
                                System.out.printf("* %-13s %-28s %-11s %-15s*\n", "Customer's Name:", fullname,
                                        "Policy Holder:", pFullname);
                                System.out.printf("* %-13s %-28s %-11s %-14s*\n", "Effective Date:", effectiveDate,
                                        "Expiration Date:", expirationDate);
                            }

                            // count the number of vehicles
                            sqlQuery = "SELECT COUNT(vehicle_id) AS NUMOFVEHICLES FROM VEHICLE WHERE POLICY_NUM="
                                    + getPolicyNum();
                            PreparedStatement stmt1 = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE,
                                    ResultSet.CONCUR_UPDATABLE);
                            rs = stmt1.executeQuery();

                            if (rs.next()) {

                                getCount = rs.getInt("NUMOFVEHICLES");

                                System.out.printf("* %-13s %-16s %-11s %-11s*\n", "Number of Vehicles Insured:",
                                        getCount,
                                        "Total Premium Cost:", "$" + df.format(v.getTotalPremium()));
                                System.out.println(
                                        "*******************************************************************************");
                            }

                            Vehicle.setTotalPremium(0.00);
                        } else if (answer.equalsIgnoreCase("n")) {
                            System.out.println("Get a new policy quote.");
                        } else {
                            System.out.println("Wrong input! Please try again.");
                        }
                    } else if (answer.equalsIgnoreCase("n")) {
                        System.out.println("Please try again. Return to the Menu.");
                    } else {
                        System.out.println("Wrong input! Please try again.");
                    }
                } else if (accountID.isEmpty()) {
                    System.out.println("Required field!! Please enter your account number.");
                    this.availPolicy();
                } else if (!accountID.contains("[a-zA-Z]+")) {
                    System.out.println(
                            "Account number must be 4 digit and it must be registered first. Register a customer account to get a policy.");
                } else {
                    System.out.println("You must have an account first before getting a policy quote.");
                }
            }
            db.closeConnection(con, stmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAccountID() throws SQLException {
        return accountID;
    }

    /* gets the policy number */
    public String getPolicyNum() throws SQLException {

        con = DatabaseConnection.dbcon();

        sqlQuery = "SELECT MAX(POLICY_NUM) AS POLICYNUM FROM POLICY";
        stmt = con.prepareStatement(sqlQuery);
        rs = stmt.executeQuery();

        while (rs.next()) {
            tempID = rs.getInt("POLICYNUM");
        }

        if (tempID == 0) {
            return policyNum;
        }
        return policyNum;
    }

    /* search policy method */
    public void searchPolicy() throws SQLException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        System.out.println("Search for specific policy.");
        System.out.printf("\nEnter policy number: ");
        policyNum = scan.nextLine();

        /* checks the policy number if existing */
        sqlQuery = "SELECT EXISTS(SELECT POLICY_NUM FROM POLICY WHERE POLICY_NUM =?) AS POLICYNUM";
        stmt = con.prepareStatement(sqlQuery);
        stmt.setString(1, policyNum);
        rs = stmt.executeQuery();

        if (rs.next()) {
            int poCol = rs.getInt("POLICYNUM");
            if (poCol == 1) {
                displayPolicy(); // calls this method to display the policy details
            } else {
                System.out.println("The policy number doesn't exist. Please try again.");
            }
        }
        db.closeConnection(con, stmt);
    }

    /*
     * cancel policy that also displays the preview of the policy under the account
     * to
     * select which policy number does the customer wants to cancel
     */
    public void cancelPolicy() throws Exception {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        System.out.println("**********************************CANCEL POLICY*****************************\n");
        System.out.printf("Enter your account number: ");
        accountID = scan.nextLine();

        // checks if the accountnumber already exists
        sqlQuery = "SELECT EXISTS (SELECT ACCOUNTNUMBER FROM CUSTOMERDETAILS WHERE ACCOUNTNUMBER =?) AS EXISTCOL";
        stmt = con.prepareStatement(sqlQuery);
        stmt.setString(1, accountID);
        rs = stmt.executeQuery();

        if (rs.next()) {

            int existCol = rs.getInt("EXISTCOL");// get the column to check if the accountnumber exists

            if (existCol == 1) {

                // counts the number of policies registered under the customer's accountnumber
                sqlQuery = "SELECT COUNT(policy_num) AS NUMOFPOLICIES FROM POLICY WHERE ACCOUNTNUMBER=? and STATUS=?";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, accountID);
                stmt.setString(2, "ACTIVE");
                rs = stmt.executeQuery();

                if (rs.next()) {

                    int getCount = rs.getInt("NUMOFPOLICIES");
                    System.out.println("There is/are active " + getCount + " policies registered under your account.");

                    if (getCount == 0) {
                        System.out.println("There are no policies under your account.");

                    } else {
                        System.out.println("\nDisplay the policies under your account? (Y/N) ");
                        answer = scan.next();

                        if (answer.equalsIgnoreCase("y")) {
                            System.out.print("\033[H\033[2J"); // clear screen

                            // select query to join the 3 objects's fields/attributes to display
                            sqlQuery = "SELECT P.POLICY_NUM, P.START_DATE, P.END_DATE, "
                                    + "V.VEHICLE_ID, V.VEHICLE_MAKE, V.VEHICLE_MODEL, "
                                    + "V.VEHICLE_COLOR, V.VEHICLE_PRICE, V.VEHICLE_PREMIUMCHARGED, "
                                    + "V.CONDUCTION_NUM, PH.FIRST_NAME, PH.LAST_NAME FROM VEHICLE AS V "
                                    + "INNER JOIN POLICY AS P ON V.POLICY_NUM = P.POLICY_NUM "
                                    + "INNER JOIN POLICYHOLDER AS PH ON PH.POLICY_NUM = V.POLICY_NUM "
                                    + "WHERE PH.ACCOUNTNUMBER=? and P.STATUS=?";
                            stmt = con.prepareStatement(sqlQuery);
                            stmt.setString(1, accountID);
                            stmt.setString(2, "ACTIVE");
                            rs = stmt.executeQuery();

                            while (rs.next()) {

                                // retrieve values from columns in the PASDB
                                policyNum = rs.getString("POLICY_NUM");
                                effectiveDate = rs.getString("START_DATE");
                                expirationDate = rs.getString("END_DATE");
                                String make = rs.getString("VEHICLE_MAKE");
                                String cdNum = rs.getString("CONDUCTION_NUM");
                                String model = rs.getString("VEHICLE_MODEL");
                                String color = rs.getString("VEHICLE_COLOR");
                                String fname = rs.getString("FIRST_NAME");
                                String lname = rs.getString("LAST_NAME");
                                String fullName = fname.substring(0, 1).toUpperCase()
                                        + fname.substring(1) + " " + lname.substring(0, 1).toUpperCase()
                                        + lname.substring(1);
                                double price = rs.getDouble("VEHICLE_PRICE");
                                double premium = rs.getDouble("VEHICLE_PREMIUMCHARGED");

                                // display the policy details
                                System.out.println(
                                        "\n****************************************************************************");
                                System.out.println("*******************************POLICY DETAILS*******************************");
                                System.out.println(" ");
                                System.out.printf("* %-15s%-15s %-15s %-20s*\n", "Policy No.", policyNum,
                                        "Policy Holder's Name:", fullName);
                                System.out.print("*--------------------------------------------------------------------------*");
                                System.out.println(" ");
                                System.out.printf("* %-15s %-17s %-10s %-9s*\n", "Vehicle Conduction No.:", cdNum,
                                        "Vehicle Manufacturer:", make);
                                System.out.printf("* %-15s %-25s %-10s %-16s*\n", "Vehicle Model:", model,
                                        "Vehicle Color:", color);
                                System.out.printf("* %-15s %-25s %-10s %-9s*\n", "Vehicle Price:", "$"
                                        + df.format(price), "Premium Cost Charged:", "$" + df.format(premium));
                                System.out.printf("* %-15s %-24s %-10s %-14s*\n", "Date Registered:", effectiveDate,
                                        "Expiration Date:", expirationDate);

                                System.out.println(
                                        "****************************************************************************");

                            }

                            System.out.println("\nEnter the policy number you would like to cancel: ");
                            pNum = validateInteger(scan);

                            sqlQuery = "SELECT EXISTS(SELECT POLICY_NUM FROM POLICY WHERE POLICY_NUM=?) AS POLICYNUM";
                            stmt = con.prepareStatement(sqlQuery);
                            stmt.setInt(1, pNum);
                            rs = stmt.executeQuery();

                            if (rs.next()) {

                                int p = rs.getInt("POLICYNUM");
                                if (p == 1) {

                                    System.out.println(
                                            "\nAre you sure you want to cancel this policy number " + pNum + " ? (Y/N)");
                                    answer = scan.next();

                                    if (answer.equalsIgnoreCase("y")) {

                                        // update the status of the policy if being cancelled
                                        sqlQuery = "UPDATE POLICY SET END_DATE=?, STATUS= ? WHERE POLICY_NUM=?";
                                        stmt = con.prepareStatement(sqlQuery);

                                        stmt.setString(1, changeExpDate());
                                        stmt.setString(2, "CANCELLED");
                                        stmt.setInt(3, pNum);
                                        stmt.executeUpdate();

                                        System.out.println("Your policy number " + pNum
                                                + " has been cancelled and expired on this day, " + changeExpDate());

                                        sqlQuery = "SELECT COUNT(policy_num) AS NUMOFPOLICIES FROM POLICY WHERE ACCOUNTNUMBER=? and STATUS=?";
                                        stmt = con.prepareStatement(sqlQuery);
                                        stmt.setString(1, accountID);
                                        stmt.setString(2, "ACTIVE");
                                        rs = stmt.executeQuery();

                                        if (rs.next()) {
                                            int getCount2 = rs.getInt("NUMOFPOLICIES");
                                            System.out.println("There is/are only " + getCount2
                                                    + " active policy(ies) registered under your account.");
                                        }
                                    } else if (answer.equalsIgnoreCase("n")) {
                                        System.out.println("No policy number has been cancelled.");
                                    } else {
                                        System.out.println("Wrong input. Please try again.");
                                    }
                                } else {
                                    System.out.println("The policy number doesn't exist.");
                                }
                            }
                        } else {
                            System.out.println("Return to the Menu.");
                        }
                    }
                }
            } else {
                System.out.println(
                        "The account number doesn't exist. Please check the account number you enter. Perhaps, register first. :)");
            }
        }
        db.closeConnection(con, stmt);
    }

    /* display policy details needed for search method */
    public void displayPolicy() {

        try {
            // select query to display the 2 objects/tables policyholder and the policy
            sqlQuery = "SELECT P.POLICY_NUM, P.START_DATE, P.END_DATE, PH.FIRST_NAME," +
                    " PH.LAST_NAME FROM POLICYHOLDER AS PH INNER JOIN POLICY AS P ON P.POLICY_NUM = PH.POLICY_NUM" +
                    " WHERE PH.POLICY_NUM=?";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, policyNum);
            rs = stmt.executeQuery();

            while (rs.next()) {

                effectiveDate = rs.getString("START_DATE");
                expirationDate = rs.getString("END_DATE");
                String fname = rs.getString("FIRST_NAME");
                String lname = rs.getString("LAST_NAME");

                System.out.println("\n**************************************************************************");
                System.out.printf("* %-13s %-25s %-11s %-7s %-7s*\n", "Policy Number:", policyNum, "Policy Holder:",
                        fname, lname);
                System.out.printf("* %-13s %-23s %-18s %-10s *\n", "Date Registered:", effectiveDate,
                        "Expiration Date:",
                        expirationDate);

                sqlQuery = "SELECT SUM(vehicle_premiumcharged) as TOTALPREMIUM FROM VEHICLE WHERE POLICY_NUM=?";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, policyNum);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    totalPremiumCost = rs.getDouble("TOTALPREMIUM");

                    // counts the number of vehicles
                    sqlQuery = "SELECT COUNT(vehicle_id) AS NUMOFVEHICLES FROM VEHICLE"
                            + " WHERE POLICY_NUM=" + policyNum;
                    stmt = con.prepareStatement(sqlQuery);
                    rs = stmt.executeQuery();

                    if (rs.next()) {

                        getCount = rs.getInt("NUMOFVEHICLES");

                        System.out.printf("* %-25s %-14d %-20s %-7s *\n", "Number of Vehicle(s): ", getCount,
                                "Total Premium Cost: $", df.format(totalPremiumCost));
                        System.out
                                .println("**************************************************************************");

                    }

                } else {
                    System.out.println("Invalid. Please try again.");
                }

                // showing the vehicle details needed
                sqlQuery = "SELECT VEHICLE_MAKE, VEHICLE_MODEL, VEHICLE_PRICE, VEHICLE_PREMIUMCHARGED, CONDUCTION_NUM, CLAIM_STATUS "
                        + "FROM VEHICLE WHERE POLICY_NUM=?";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, policyNum);
                rs = stmt.executeQuery();

                while (rs.next()) {

                    /* retrieves the values from the PAS columns/fields */
                    String cdNum = rs.getString("CONDUCTION_NUM");
                    String make = rs.getString("VEHICLE_MAKE");
                    String model = rs.getString("VEHICLE_MODEL");
                    double vPrice = rs.getDouble("VEHICLE_PRICE");
                    double vPremium = rs.getDouble("VEHICLE_PREMIUMCHARGED");
                    String claimStatus = rs.getString("CLAIM_STATUS");

                    System.out.println("*----------------------------VEHICLE DETAILS-----------------------------*");
                    System.out.printf("* %-13s %-19s %-6s %-10s*\n", "Conduction Number:", cdNum, "Make/Manufacturer:",
                            make);
                    System.out.printf("* %-13s %-30s %-11s %-7s*\n", "Vehicle Model:", model, "Vehicle Price: $",
                            df.format(vPrice));
                    System.out.printf("* %-13s %-13s %-21s %-11s*\n", "Premium Cost Charged: $", df.format(vPremium), "Claim Status:", claimStatus);
                    System.out.println("**************************************************************************");

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * change the expiration date automatically today if the policy has been
     * cancelled
     */
    public String changeExpDate() {
        try {
            // get the current date if the policy has been cancelled - change the expiration
            // date to current date
            Calendar c = Calendar.getInstance();
            int dayEx = c.get(Calendar.DAY_OF_MONTH);

            String day3 = (dayEx + "").length() < 2 ? ("0" + dayEx) : (dayEx + ""); // add 0 before the actual day if
                                                                                    // the length of the day is one (the
                                                                                    // day 1 will become 01)
            int monthEx = c.get(Calendar.MONTH) + 1;

            String monthEx3 = (monthEx + "").length() < 2 ? ("0" + monthEx) : (monthEx + ""); // add 0 before the actual
                                                                                              // month if the length of
                                                                                              // the month is one (the
                                                                                              // 1st month will become
                                                                                              // 01)
            int yearEx = c.get(Calendar.YEAR);
            expDate = (yearEx + "").substring(0, 4) + "-" + monthEx3 + "-" + day3; // dateformat is YYYY-MM-DD
        } catch (DateTimeException d) {
            d.printStackTrace();
        }
        return expDate;
    }

    /*
     * checks the effective date status and also sets the expiration date in 6
     * months
     */
    public void getStatusEffectiveDate() throws SQLException {

        LocalDate sDate = LocalDate.parse(effectiveDate);

        /* this will get the expiration date */
        LocalDate expireDate = LocalDate.parse(effectiveDate).plusMonths(6);
        String nd = expireDate + "";
        String day2 = nd.substring(8);
        String month2 = nd.substring(5, 7);
        String year2 = nd.substring(0, 4);
        expirationDate = year2 + "-" + month2 + "-" + day2;

        System.out.println("The expiration date will be on " + expirationDate);
        /*
         * checks the status of the policy
         * that will depend on the effective date user input
         */
        if (sDate.isBefore(LocalDate.now()) || sDate.isEqual(LocalDate.now())) {
            this.status = "ACTIVE";
            System.out.println("Your policy is " + status + " .");
        } else if (sDate.isAfter(LocalDate.now())) {
            this.status = "SCHEDULED";
            System.out.println("Your policy is " + status + " .");
        }
    }

    /* automatically updates the policy status */
    public void updateStatus() throws SQLException {

        con = DatabaseConnection.dbcon();
        try {
            sqlQuery = "SET SQL_SAFE_UPDATES=0;";
            String sqlSetActive = "UPDATE POLICY SET STATUS = 'ACTIVE' WHERE DATE(START_DATE) <= DATE(now());";
            String sqlSetExpired = "UPDATE POLICY SET STATUS = 'EXPIRED' WHERE DATE(END_DATE) <= DATE(now());";
            stmt = con.prepareStatement(sqlQuery);
            stmt.execute(sqlQuery);
            stmt.execute(sqlSetActive);
            stmt.execute(sqlSetExpired);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* validation for the required String variables if empty */
    public boolean validateRequiredFields(String text) {
        boolean txt = text.isEmpty();
        try {
            if (txt) {
                System.out.println("Required field!! Please enter the details needed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txt;
    }

    /* validates the integer/number */
    public int validateInteger(Scanner sc) throws Exception {
        try {
            if (!sc.hasNextInt()) {
                System.out.println("\n" + sc.next() + " is not a valid number. Please try again....");
                this.availPolicy();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sc.nextInt();
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getStatus() {
        return status;
    }

}
