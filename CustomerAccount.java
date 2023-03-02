
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
import java.util.Random;
import java.util.Scanner;

public class CustomerAccount extends PolicyHolder {

    private static String accountNumber = getRandomNumberString();
    private static String firstName, lastName, address;
    private static int tempID;

    private String answer, sqlQuery;
    private Connection con;
    private ResultSet rs;
    private PreparedStatement stmt;

    private String[] args = null; // to call the main method

    Scanner scan = new Scanner(System.in);

    public void CustomerInput() throws SQLException {

        // instantiate the database and gets the connection
        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        System.out.println("*********************REGISTER CUSTOMER ACCOUNT******************\n");

        System.out.printf("Enter customer's first name: ");
        firstName = scan.nextLine().trim().toString();// used trim so that spaces would not be included
        textValidator(firstName); // calls the textValidator method from the policy holder class

        System.out.printf("Enter customer's last name: ");
        lastName = scan.next().trim().toString();
        ;
        textValidator(lastName);

        scan.nextLine();

        System.out.printf("Enter customer's address: ");
        address = scan.nextLine();

        if (address.isEmpty()) {
            System.out.printf("Required field! Please enter your address:");
            address = scan.nextLine();

            if (address.isEmpty()) {
                System.out.printf("Required field! Please enter the required details being asked.\n");
                this.CustomerInput();
            }
        }

        System.out.println("\nDo you want to register your details? (Y/N)");
        answer = scan.nextLine();

        if (answer.equalsIgnoreCase("y")) {

            // checks if the customerdetails such as their firstname and lastname exist
            sqlQuery = "SELECT EXISTS (SELECT FIRSTNAME FROM CUSTOMERDETAILS WHERE FIRSTNAME= ? and LASTNAME= ?) AS RES";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            rs = stmt.executeQuery();

            if (rs.next()) {

                int res = rs.getInt("RES"); // get the RESULT column

                if (res == 1) {

                    System.out.println("The customer already exists!!");

                } else {

                    /* save customer details */
                    sqlQuery = "INSERT INTO CUSTOMERDETAILS(ACCOUNTNUMBER,FIRSTNAME, LASTNAME, ADDRESS) VALUES (?,?,?,?)";
                    stmt = con.prepareStatement(sqlQuery);
                    stmt.setString(1, accountNumber);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, address);
                    stmt.executeUpdate();

                    /*
                     * gets the accountnumber of the registered customer
                     * and displays it so the customer can see his/her accountnumber
                     */
                    sqlQuery = "SELECT ACCOUNTNUMBER FROM CUSTOMERDETAILS WHERE FIRSTNAME=? AND LASTNAME=? AND ADDRESS=?;";
                    stmt = con.prepareStatement(sqlQuery);
                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, address);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        String acNum = rs.getString("ACCOUNTNUMBER");
                        System.out.println(
                                "\nYour details are added successfully and your accountnumber is " + acNum + "\n");
                    }
                }
            }
            db.closeConnection(con, stmt);
        } else if (answer.equalsIgnoreCase("n")) {
            System.out.println("We would like to have you insured. Register now! :)");
            try {
                PASApp.main(args);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("Wrong input. Please try again.");
        }
    }

    /*
     * this is just a confirmation if the user/customer wants to register another
     * account
     */
    public void loadCustomerInput() throws SQLException {

        this.CustomerInput(); // calls the customer input method

        String answer;
        System.out.println("Do you want to register another account? (Y/N) ");
        answer = scan.nextLine();

        if (answer.equalsIgnoreCase("y")) {
            this.CustomerInput();
        } else if (answer.equalsIgnoreCase("n")) {
            System.out.println("Thank you. Back to the menu.");
        } else {
            System.out.println("Thank you for registering an account.");
        }
    }

    /* search account method */
    public void searchAccount() throws Exception {

        this.answer = "y";
        System.out.println(" *************************Search for Customer Account****************************\n ");

        do {
            if (answer.equalsIgnoreCase("y")) {

                System.out.printf("\nEnter Firstname: ");
                firstName = scan.nextLine().trim().toString();
                System.out.printf("Enter Lastname: ");
                lastName = scan.nextLine().trim().toString();
                this.custAcctResult(firstName, lastName);

            } else if (!answer.equalsIgnoreCase("n") || !answer.equalsIgnoreCase("y"))
                System.out.println("Invalid input.Please try again");
            System.out.println();
            System.out.println("Do you want to search for another customer? (y/n)");
            answer = scan.nextLine();

            if (answer.equalsIgnoreCase("n")) {
                System.out.println("Return to the menu.");
            }
        } while (!answer.equalsIgnoreCase("n"));
    }

    /* this displays the customer account details */
    public void custAcctResult(String fname, String lname) throws Exception {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        try {
            sqlQuery = "SELECT EXISTS(SELECT * FROM CUSTOMERDETAILS WHERE FIRSTNAME=? AND LASTNAME=?) AS EXISTCOL";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            rs = stmt.executeQuery();

            if (rs.next()) {

                int existcol = rs.getInt("EXISTCOL");//gets the existcolumn

                if (existcol == 1) {

                    sqlQuery = "SELECT * FROM CUSTOMERDETAILS AS C INNER JOIN POLICY AS P ON C.ACCOUNTNUMBER = P.ACCOUNTNUMBER"
                            +
                            " INNER JOIN POLICYHOLDER AS PH ON P.POLICY_NUM = PH.POLICY_NUM WHERE C.FIRSTNAME=? AND C.LASTNAME=?;";
                    stmt = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    stmt.setString(1, fname);
                    stmt.setString(2, lname);
                    rs = stmt.executeQuery();

                    if (rs.next()) {

                        // retrieve the rows/fields in DB
                        String fName = rs.getString("FIRSTNAME");
                        String lName = rs.getString("LASTNAME");
                        int accountNum = rs.getInt("ACCOUNTNUMBER");
                        String address = rs.getString("ADDRESS");
                        String fullName = fName + " " + lName;

                        System.out.println(
                                " ******************************************************************************** ");
                        System.out.println(
                                " ******************************************************************************** ");
                        System.out.printf("* %-13s %-25s %-11s %-11d *\n", "\t\tAccount Name:", fullName, "Account No:",
                                accountNum);
                        System.out.printf("* %-8s %-38s *\n", "\t\t\t\tAddress:", address);

                        System.out.println(
                                " ******************************************************************************** ");
                        System.out.printf("* %-15s%-9d *\n",
                                "\tList of Polic(ies) and its Policy Holder under Customer Acct #", accountNum);
                        System.out.printf("* %-17s %-23s %-17s %-17s *\n", "Policy No.", "Effective Date", "Status",
                                "Policy Holder");
                        do { // retrieve the values from db and display it
                            int policyNum = rs.getInt("POLICY_NUM");
                            String eDate = rs.getString("START_DATE");
                            String status = rs.getString("STATUS");
                            String firstname = rs.getString("FIRST_NAME");
                            String lastname = rs.getString("LAST_NAME");
                            String fullname = firstname + " " + lastname;
                            System.out.printf("* %-17d %-23s %-17s %-17s *\n", policyNum, eDate, status, fullname);
                        } while (rs.next());
                        System.out.println(
                                " ********************************************************************************");
                    } else {
                        /*
                         * if the account is registered but doesn't have any policies,
                         * the system will display the account details
                         */
                        sqlQuery = "SELECT * FROM CUSTOMERDETAILS WHERE FIRSTNAME=? AND LASTNAME=?";
                        stmt = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                        stmt.setString(1, fname);
                        stmt.setString(2, lname);
                        rs = stmt.executeQuery();

                        if (rs.next()) {
                            // retrieve the rows/fields in DB
                            String fName = rs.getString("FIRSTNAME");
                            String lName = rs.getString("LASTNAME");
                            int accountNum = rs.getInt("ACCOUNTNUMBER");
                            String address = rs.getString("ADDRESS");
                            String fullName = fName + " " + lName;

                            System.out.println(
                                    "*********************************************************************************");
                            System.out.println(
                                    "*********************************************************************************");
                            System.out.printf("* %-13s %-25s %-11s %-11d *\n", "\t\tAccount Name:", fullName,
                                    "Account No:", accountNum);
                            System.out.printf("* %-8s %-38s *\n", "\t\t\t\tAddress:", address);
                            System.out.println(
                                    "*********************************************************************************");
                            System.out.printf("%20s",
                                    "\t\tNo policies registered on this account. Please try again.\n");
                        }
                    }

                }

                else {
                    System.out.println("No data found. Please register an account first.");
                }
            } else {
                System.out.println("No registered customer name in the system. Register an account now.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection(con, stmt);
    }

    public static String getRandomNumberString() {
        // It will generate 4 digit random Number.
        // from 0 to 9999
        Random rnd = new Random();
        int number = rnd.nextInt(9999);
        // this will convert any number sequence tinto 6 character.
        return String.format("%04d", number);
    }

    /* gets the accountnumber */
    public String getAccountNum() throws SQLException {

        con = DatabaseConnection.dbcon();

        sqlQuery = "SELECT MAX(ACCOUNTNUMBER) AS ACCOUNTNUMBER FROM CUSTOMERDETAILS";
        stmt = con.prepareStatement(sqlQuery);
        rs = stmt.executeQuery();

        while (rs.next()) {
            tempID = rs.getInt("ACCOUNTNUMBER");
        }
        if (tempID == 0) {
            return accountNumber;
        }
        return accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
