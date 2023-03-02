
/**********************************************************************************
 * Java Course 4 Capstone Project
 * 
 * Automobile Insurance Policy and Claims Administration System (PAS) Specification
 * 
 * @author: Jellie Mae Ortiz
 **********************************************************************************/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;
import java.util.Scanner;

public class PolicyHolder extends Policy {

    private static int policyHolderID = 100;
    private static String fName, lName, dob, address, licenseNumber, dateFirstIssued;
    private static int licensedYear;
    private static int tempID;
    private Connection con;
    private String sqlQuery;
    private ResultSet rs;
    private PreparedStatement stmt;

    Scanner scan = new Scanner(System.in);

    public void loadInput() throws SQLException, ParseException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        System.out.println("***************************************************************************\n");
        System.out.println("*********************ENTER POLICY HOLDER DETAILS***************************\n");

        try {

            System.out.printf("Enter policy holder's first name: ");
            fName = scan.nextLine().trim().toString();

            // validates the string input data firstName and lastName
            this.textValidator(fName);

            System.out.printf("Enter policy holder's last name: ");
            lName = scan.nextLine().trim().toString();

            this.textValidator(lName);

            System.out.printf("Enter policy holder's date of birth (YYYY-MM-DD): ");
            dob = scan.nextLine().trim().toString();

            /*
             * all validateRequireFields came from the policy
             * class to validate the fields are not null/empty
             */
            if (validateRequiredFields(dob)) {
                this.loadInput();
            }

            // validation for the date format YYYY-MM-DD
            String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";
            if (!dob.matches(regex)) {
                System.out.println("Please input a valid date with this format (YYYY-MM-DD). Thank you.");
                this.loadInput();
            }

            else if (dob.isEmpty()) {
                System.out.println("Required field! Please enter a valid date of birth.");
            }

            else {
                /* validation to check if the policy holder is below 18 */
                sqlQuery = "SELECT DATEDIFF(CURRENT_TIMESTAMP, ?) / 365.2425 AS DiffDate";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, dob);
                rs = stmt.executeQuery();

                if (rs.next()) {

                    int age = rs.getInt("DiffDate");

                    if (age < 18) {

                        System.out.println("Your age " + age + " is not eligible to proceed to get a policy quote.\n");
                        this.loadInput();

                    } else {

                        System.out.printf("Enter policy holder's address: ");
                        address = scan.nextLine();

                        if (validateRequiredFields(address)) {
                            this.loadInput();
                        }

                        System.out.printf("Enter policy holder's license number: ");
                        licenseNumber = scan.nextLine();

                        if (validateRequiredFields(licenseNumber)) {
                            this.loadInput();
                        }

                        try {

                            System.out.printf("Enter the date when the license number first issued (YYYY-MM-DD): ");
                            dateFirstIssued = scan.nextLine();

                            if (validateRequiredFields(dateFirstIssued)) {
                                this.loadInput();
                            }

                            LocalDate date = LocalDate.parse(dateFirstIssued);

                            if (date.isAfter(LocalDate.now())) {
                                System.out.println(
                                        "Invalid! You must enter a date beyond when the license number issued ");
                                this.loadInput();
                            }

                            else if (!dateFirstIssued.matches(regex)) {
                                System.out
                                        .println("Please input a valid date with this format (YYYY-MM-DD). Thank you.");

                                System.out.printf(
                                        "Re-enter the date when the license number first issued (YYYY-MM-DD): ");
                                dateFirstIssued = scan.nextLine();

                                if (date.isAfter(LocalDate.now())) {
                                    System.out.println(
                                            "Invalid! You must enter a date beyond when the license number issued. ");
                                    this.loadInput();
                                }

                            }

                        } catch (DateTimeException d) {
                            System.out.println("Invalid date format! Please try again.");
                            this.loadInput();
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection(con, stmt);
    }

    // this method is necessary for the premium cost computation in rating engine
    // class it gets the difference between the currentdate and the date when the
    // license number issued
    public int getYearIssued() {

        LocalDate currentDate = LocalDate.now();
        LocalDate dateIssued = LocalDate.parse(dateFirstIssued);

        Period per = Period.between(dateIssued, currentDate);

        // int year1 = dateIssued.getYear();
        // int year2 = currentDate.getYear();
        licensedYear = per.getYears();
        if (licensedYear == 0) { // this is to check if the licensedyear result will be zero and need to
                                 // increment 1
            licensedYear = 1;
        }
        return licensedYear;
    }

    // save the policy holder details using insert query & preparedstatement
    public void savePolicyHolderData() throws SQLException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        sqlQuery = "INSERT INTO POLICYHOLDER(POLICYHOLDER_ID, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH " +
                ", ADDRESS, DRIVERS_LICENSENUM, DATEISSUED, ACCOUNTNUMBER, POLICY_NUM) VALUES (?, ?, ?, ? , ?, ?, ?, ?, ?)";
        stmt = con.prepareStatement(sqlQuery);
        stmt.setInt(1, policyHolderID);
        stmt.setString(2, fName);
        stmt.setString(3, lName);
        stmt.setString(4, dob);
        stmt.setString(5, address);
        stmt.setString(6, licenseNumber);
        stmt.setString(7, dateFirstIssued);
        stmt.setString(8, getAccountID());
        stmt.setString(9, getPolicyNum());
        stmt.executeUpdate();

        db.closeConnection(con, stmt);
    }

    /*
     * textValidator method is the validation for
     * string input data such as
     * first name and last name
     */
    public String textValidator(String text) {
        if (!text.matches("[a-zA-Z ]+") || text.isEmpty()) {
            System.out.println("Required field! Please enter a data without any numbers or special characters.");
            do {
                System.out.printf("Re-enter the required detail: ");
                text = scan.nextLine();

                if (!text.matches("[a-zA-Z ]+") || text.isEmpty()) {
                    System.out
                            .println("Required field! Please enter a data without any numbers or special characters.");
                }
            } while (!text.matches("[a-zA-Z ]+") || text.isEmpty());
        }
        return text;
    }

    /* gets the policy holder id */
    public int getPolicyID() throws SQLException {

        con = DatabaseConnection.dbcon();

        sqlQuery = "SELECT MAX(POLICYHOLDER_ID) AS POLICYHOLDERID FROM POLICYHOLDER";
        stmt = con.prepareStatement(sqlQuery);
        rs = stmt.executeQuery();

        while (rs.next()) {
            tempID = rs.getInt("POLICYHOLDERID");
        }
        if (tempID == 0) {
            policyHolderID += 1;
        }
        return policyHolderID;

    }

    public static String getRandomNumberString() {
        // It will generate 3 digit random Number.
        // from 0 to 999
        Random rnd = new Random();
        int number = rnd.nextInt(999);

        // this will convert any number sequence into 6 character.
        return String.format("%03d", number);
    }

    public String getfName() {

        return fName;
    }

    public String getLName() {

        return lName;
    }

}