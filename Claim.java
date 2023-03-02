
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
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;

public class Claim extends Policy {

    private static String claimID;
    private static String accidentDate, accidentAddress, accidentDesc;
    private static String damageDesc, policyNum, status, effectDate, expDate, conductionNo;
    private static float estimatedCost;

    boolean isPolicyExist;
    private String[] args = null; // to call the main method
    private Connection con;
    private String sqlQuery, answer;
    private ResultSet rs;
    private PreparedStatement stmt;

    DecimalFormat df = new DecimalFormat();
    Scanner sc = new Scanner(System.in);

    public void loadClaim() throws Exception {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        try {
            System.out.println("***************************************************************************\n");
            System.out.println("***********************************FILE A CLAIM****************************\n");

            System.out.printf("Enter policy number: ");
            policyNum = sc.nextLine();

            if (validateRequiredFields(policyNum)) {
                this.loadClaim();
            }
            /* retrieves the column/values in the database and checks the policy number if existing*/
            sqlQuery = "SELECT * FROM POLICY WHERE POLICY_NUM=?";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, policyNum);
            rs = stmt.executeQuery();

            if (!rs.next()) {

                isPolicyExist = false;
                System.out.println("The policy number doesn't exist.");

            } else {

                isPolicyExist = true;

                // retrieve the values from columns
                status = rs.getString("STATUS");
                effectDate = rs.getString("START_DATE");
                expDate = rs.getString("END_DATE");

                this.checkPolicyStatus(null);/*calls the check policy status method to validate the policy number 
                                                    if it's applicable to file a claim*/
                this.checkPolicyClaimStatus();
                sqlQuery = "SELECT * FROM VEHICLE WHERE POLICY_NUM=?";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, policyNum);
                rs = stmt.executeQuery();

                while (rs.next()) {

                    String vMake = rs.getString("VEHICLE_MAKE");
                    conductionNo = rs.getString("CONDUCTION_NUM");
                    int vAge = rs.getInt("VEHICLE_YEAR");
                    String vModel = rs.getString("VEHICLE_MODEL");
                    String color = rs.getString("VEHICLE_COLOR");
                    String vType = rs.getString("VEHICLE_TYPE");
                    double premium = rs.getDouble("VEHICLE_PREMIUMCHARGED");
                    double price = rs.getDouble("VEHICLE_PRICE");

                    System.out.println(" ");
                    System.out.println("***************************************************************************");
                    System.out.printf("* %-15s%-19s *\n", "\t\tList of Vehicle(s) under this policy #", policyNum);
                    System.out.printf("*%-13s %-28s %-15s %-11s*\n", " Conduction No.:", conductionNo, "Manufacturer:",
                            vMake);
                    System.out.printf("*%-13s %-32s %-10s %-15s*\n", " Model:", vModel, "Type:", vType);
                    System.out.printf("*%-13s %-32s %-10s %-15s*\n", " Age:", vAge, "Color:", color);
                    System.out.printf("*%-13s %-27s %-10s %-9s*\n", " Price", "$" + df.format(price),
                            "Premium Cost Charged:",
                            "$" + df.format(premium));
                    System.out.println("***************************************************************************");
                }

                sqlQuery = "SELECT COUNT(VEHICLE_ID) AS NUMOFVEHICLES FROM VEHICLE WHERE POLICY_NUM=" + policyNum;
                stmt = con.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();

                if (rs.next()) {

                    int numVehicle = rs.getInt("NUMOFVEHICLES");

                    if (numVehicle > 1) {
                        System.out.printf(
                                "Please select the vehicle you want to file a claim.\n");
                        System.out.printf("\nEnter the conduction number: ");
                        String conductionInput = scan.nextLine();

                        sqlQuery = "SELECT EXISTS(SELECT * FROM VEHICLE WHERE CONDUCTION_NUM=?) AS EXISTVEHICLE;";
                        stmt = con.prepareStatement(sqlQuery);
                        stmt.setString(1, conductionInput);
                        rs = stmt.executeQuery();

                        if (rs.next()) {
                            int existVehicle = rs.getInt("EXISTVEHICLE");
                            if (existVehicle == 1) {
                                this.inputClaim();
                            }
                        }
                    } else if (numVehicle == 1) {

                        this.inputClaim();
                    }
                } else {
                    System.out.println("No data found. Please try again.");
                }
            }
            db.closeConnection(con, stmt);
        } catch (DateTimeException e) {
            System.out.println("Invalid date format! Please try again.");
            this.loadClaim();
        }
    }

    public static String getRandomNumberString() {
        // It will generate 5 digit random Number from 0 to 99999
        Random rnd = new Random();
        int number = rnd.nextInt(99999);

        // this will convert any number sequence into 6 character.
        return String.format("%05d", number);
    }

    public void searchClaim() throws SQLException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        System.out.println("************************************Search for Claim***************************************\n");

        System.out.printf("Enter the claim number: ");
        claimID = sc.nextLine();

        // checks if the claimID exists
        sqlQuery = "SELECT EXISTS(SELECT CLAIMID FROM CLAIM WHERE CLAIMID=?) AS CLAIMID FROM CLAIM";
        stmt = con.prepareStatement(sqlQuery);
        stmt.setString(1, claimID);
        rs = stmt.executeQuery();

        if (rs.next()) {

            int c = rs.getInt("CLAIMID");

            // executes when the claim id exists and displays the claim details
            if (c == 1) {

                sqlQuery = "SELECT * FROM CLAIM AS C INNER JOIN VEHICLE AS V ON C.CONDUCTION_NUM = V.CONDUCTION_NUM WHERE CLAIMID=?";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, claimID);
                rs = stmt.executeQuery();

                while (rs.next()) {

                    // retrieves the values from columns in the database
                    String claimID = rs.getString("CLAIMID");
                    String date = rs.getString("DATEOFACCIDENT");
                    String address = rs.getString("ADDRESSOFACCIDENT");
                    String accidentDesc = rs.getString("ACCIDENTDESC");
                    String damageDesc = rs.getString("DAMAGEDESC");
                    double cost = rs.getDouble("COSTOFREPAIRS");
                    String make = rs.getString("VEHICLE_MAKE");
                    String yearModel = rs.getString("VEHICLE_YEAR");
                    policyNum = rs.getString("POLICY_NUM");
                    conductionNo = rs.getString("CONDUCTION_NUM");

                    // displays the claim details
                    System.out.println("*********************************CLAIM DETAILS*********************************************\n");
                    System.out.printf("* %-13s%-44s %-3s%-15s *\n", "Claim ID:", claimID, "Policy Number:", policyNum);
                    System.out.printf("* %-13s%-39s %-12s%-16s *\n", "Conduction Number:", conductionNo, "Vehicle Make:", make);
                    System.out.printf("* %-15s%-21s %-15s%-15s* \n", "Address where the accident happened:", address, "Date:", date);
                    System.out.printf("* %-13s %-40s %-17s %-10s*\n", "Cost of Repairs:", "$" + df.format(cost), "Vehicle Year Model:", yearModel);
                    System.out.printf("* %-13s %-28s %-16s %-7s*\n", "Description of the Accident:", accidentDesc,
                            "Damage:", damageDesc);
                    System.out.println("*******************************************************************************************\n");
                }
            } else {
                System.out.println("The claim number is invalid. Please try again.");
            }
        }else{

            System.out.println("No data found. Please check your claim number.");
        }
        db.closeConnection(con, stmt);
    }

    public String checkPolicyStatus(String[] args) throws Exception {

        /* method used to validate the policy status */
        if (status.equals("CANCELLED")) {
            System.out
                    .println("Sorry, you can't file a claim because the policy number you entered has been cancelled.");
            PASApp.main(args);
        }

        else if (status.equals("EXPIRED")) {
            System.out.println("Sorry, you can't file a claim because the policy number you entered has been expired.");
            PASApp.main(args);
        }

        else if (status.equals("SCHEDULED")) {
            System.out.println("Sorry, you can't file a claim because your policy is not yet effective."
                    + "\nIt will be active on " + effectDate);
            PASApp.main(args);
        }
        return status;
    }

    public void inputClaim() throws Exception {

        con = DatabaseConnection.dbcon();

        System.out.printf("Enter the date when the accident happened (YYYY-MM-DD): ");
        accidentDate = sc.nextLine();

        if (validateRequiredFields(accidentDate)) {
            this.loadClaim();
        }
        /*
         * these localdate are needed to parse the string value for accident date
         * validation
         */
        LocalDate aDate = LocalDate.parse(accidentDate);
        LocalDate eDate = LocalDate.parse(effectDate);
        LocalDate exp = LocalDate.parse(expDate);

        // validation for the date format
        String regex = "\\d{4}-\\d{1,2}-\\d{1,2}";

        if (!accidentDate.matches(regex)) {

            System.out.println("Please input a valid date with this format (YYYY-MM-DD). Thank you.");
            loadClaim();
        }

        /* checks if accident date is within the policy date range/coverage */
        else if (aDate.isBefore(eDate) || aDate.isAfter(exp)) {

            System.out.println(
                    "Sorry, you can't file a claim if the accident date is out of the policy date range/coverage.");

        }

        /* validates the accident date if future date is being entered */
        else if (aDate.isAfter(LocalDate.now())) {

            System.out.println("Invalid input! Sorry, you can't enter a future date of the accident."
                    + "\nIt must be beyond the actual date and in the policy date coverage.");
        }

        /*
         * the program will proceed if the entered accident date meets
         * the expected requirement(within the policy effective date & expiration date)
         */
        else {

            System.out.printf("Enter the address where the accident takes place: ");
            accidentAddress = sc.nextLine();

            if (validateRequiredFields(accidentAddress)) {
                this.loadClaim();
            }

            System.out.printf("Enter the accident description: ");
            accidentDesc = sc.nextLine();

            if (validateRequiredFields(accidentDesc)) {
                this.loadClaim();
            }

            System.out.printf("Enter the description of the damage to vehicle: ");
            damageDesc = sc.nextLine();

            if (validateRequiredFields(damageDesc)) {
                this.loadClaim();
            }

            System.out.print("What is the estimated cost of the repairs? $");
            estimatedCost = sc.nextFloat();

            System.out.printf("\nAre you sure you want to file a claim? (Y/N)");
            answer = scan.nextLine();

            if (answer.equalsIgnoreCase("y")) {
                this.saveClaimDetails();
            } else if (answer.equalsIgnoreCase("n")) {
                System.out.println("You can file a claim next time. Thank you. ");
            }
        }
    }

    public void saveClaimDetails() throws SQLException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        try {
            claimID = "C" + getRandomNumberString();

            /* save the claim details */
            sqlQuery = "INSERT INTO CLAIM (CLAIMID, DATEOFACCIDENT,ADDRESSOFACCIDENT,ACCIDENTDESC," +
                    " DAMAGEDESC, COSTOFREPAIRS, POLICY_NUM, CONDUCTION_NUM)VALUES (?,?,?,?,?,?,?,?)";
            stmt = con.prepareStatement(sqlQuery);

            stmt.setString(1, claimID);
            stmt.setString(2, accidentDate);
            stmt.setString(3, accidentAddress);
            stmt.setString(4, accidentDesc);
            stmt.setString(5, damageDesc);
            stmt.setDouble(6, estimatedCost);
            stmt.setString(7, policyNum);
            stmt.setString(8, conductionNo);
            stmt.executeUpdate();

            sqlQuery = "SET SQL_SAFE_UPDATES=0;";
            sqlQuery = "UPDATE VEHICLE SET CLAIM_STATUS='FILED' WHERE CONDUCTION_NUM=?";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, conductionNo);
            stmt.executeUpdate();

            // this.updateVehicleStatus();
            System.out.println("Your claim has been recorded. This is your claim number: " + claimID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection(con, stmt);
    }

    /*
     * this is to check the status of the policy if the account already filed a
     * claim
     */
    public void checkPolicyClaimStatus() throws SQLException {

        con = DatabaseConnection.dbcon();
        try {

            sqlQuery = "SELECT EXISTS(SELECT POLICY_NUM FROM VEHICLE WHERE CLAIM_STATUS='FILED' AND POLICY_NUM=?) AS CHECKSTATUS";
            stmt = con.prepareStatement(sqlQuery);
            stmt.setString(1, policyNum);
            rs = stmt.executeQuery();

            if (rs.next()) {

                int checkPoStatus = rs.getInt("CHECKSTATUS");
                if (checkPoStatus == 1) {
                    System.out.println("You can't file a claim right now since you have an ongoing claim transaction.");
                    PASApp.main(args);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
