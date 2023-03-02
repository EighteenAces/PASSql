
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Vehicle extends PolicyHolder {

    private static int vehicleNo, vehicleID, cdNum;
    private static String make, model;
    private static int year, vehicleAge;
    private static String type, fType;
    private static double purchasedPrice, premiumCost, totalPremiumCost;
    private static String color, conductionNum;

    private Connection con;
    private String sqlQuery;
    private ResultSet rs;
    private PreparedStatement stmt;

    public List<VehicleList> vList = new ArrayList<>(); // list for temporary storing the vehicle data

    Scanner scan = new Scanner(System.in);
    DecimalFormat df = new DecimalFormat("0.00");

    public void load() throws Exception, SQLException, ParseException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        try {

            System.out.println("***************************************************************************\n");
            System.out.println("***************************ENTER VEHICLE DETAILS***************************\n");

            System.out.println("How many vehicle(s) you want to be insured? ");
            vehicleNo = validateInteger(scan);

            /* validates the vehicle number/quantity if it's zero, it cannot proceed */
            if (vehicleNo == 0) {
                System.out.println("Cannot be zero. Please try again!");
                this.load();
            }

            // loops if the user/customer will input 1 or more vehicles
            for (int i = 0; i < vehicleNo; i++) {

                scan.nextLine();

                System.out.println("Vehicle Details #" + (i + 1));
                System.out.println("***************************");

                /*
                 * this serves as the unique identification
                 * of a vehicle since the policy number can have more than vehicles but has a
                 * unique feature
                 */
                System.out.println("Vehicle Conduction Number: ");
                conductionNum = scan.nextLine().trim();

                /*
                 * all validateRequiredFields method came
                 * from the policy class to validate the string variables
                 */
                if (validateRequiredFields(conductionNum)) {
                    this.load();
                }

                // checks if the conduction number exists
                sqlQuery = "SELECT EXISTS(SELECT CONDUCTION_NUM FROM VEHICLE) AS CONDUCTNUM FROM VEHICLE" +
                        " WHERE CONDUCTION_NUM=?";
                stmt = con.prepareStatement(sqlQuery);
                stmt.setString(1, conductionNum);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    cdNum = rs.getInt("CONDUCTNUM");
                    if (cdNum == 1) {
                        System.out.println("The conduction number you entered is already registered. Return to Menu");
                        System.exit(0);
                    }
                } else {
                    System.out.printf("Vehicle Manufacturer: ");
                    make = scan.nextLine().trim();
                    if (validateRequiredFields(make)) {
                        this.load();
                    }

                    System.out.printf("Vehicle Model: ");
                    model = scan.nextLine().trim();
                    if (validateRequiredFields(model)) {
                        this.load();
                    }

                    System.out.printf("Vehicle Year (YYYY): ");
                    year = validateInteger(scan);
                    this.validateYear(vehicleAge);

                    System.out.printf("Select vehicle type: \n");
                    System.out.println("1 - 4-door Sedan");
                    System.out.println("2 - 2-door sports car");
                    System.out.println("3- SUV");
                    System.out.println("4 - Truck");

                    int choice;
                    choice = scan.nextInt();

                    switch (choice) {

                        case 1:
                            type = "4-door Sedan";
                            break;
                        case 2:
                            type = "2- door sports car";
                            break;
                        case 3:
                            type = "SUV";
                            break;
                        case 4:
                            type = "Truck";
                            break;
                        default:
                            System.out.println("Please input a valid number based on the choices.");
                            this.load();
                            break;

                    }

                    System.out.printf("Select fuel type: \n");
                    System.out.println("1 - Diesel");
                    System.out.println("2 - Electric");
                    System.out.println("3- Petrol");

                    int opt;
                    opt = scan.nextInt();

                    switch (opt) {
                        case 1:
                            fType = "Diesel";
                            break;
                        case 2:
                            fType = "Electric";
                            break;
                        case 3:
                            fType = "Petrol";
                            break;
                        default:
                            System.out.println("Please input a valid number based on the choices.");
                            this.load();
                            break;
                    }

                    scan.nextLine();

                    System.out.printf("Vehicle Color: ");
                    color = scan.nextLine().trim();

                    if (validateRequiredFields(color)) {
                        this.load();
                    }

                    System.out.printf("Purchased Price: $");
                    purchasedPrice = scan.nextDouble();

                    RatingEngine rg = new RatingEngine(purchasedPrice, vehicleAge);
                    System.out.printf("Vehicle Premium Charged in ($USD): $");
                    premiumCost = rg.getPremium();
                    System.out.println(df.format(premiumCost));

                    totalPremiumCost = totalPremiumCost + premiumCost;
                    System.out.println("Total Premium Cost: $" + df.format(totalPremiumCost));
                    vList.add(new VehicleList(make, model, year, type, conductionNum, fType,
                            purchasedPrice, color, premiumCost));

                    System.out.print("\033[H\033[2J"); // clear screen
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // displays the vehicle details that is being temporarily stored
        for (VehicleList vehicle : vList) {
            String make = vehicle.getMake();
            String model = vehicle.getModel();
            String color = vehicle.getColor();
            String cNum = vehicle.getCdNum();
            double price = vehicle.getPrice();
            double premium = vehicle.getPremium();
            System.out.println("***************************************************************************");
            System.out.printf("* %-15s%-21s *\n", "\t\tList of Vehicles under this policy #", getPolicyNum());
            System.out.printf("*%-13s %-28s %-15s %-12s*\n", "Conduction No.:", cNum, "Manufacturer:", make);
            System.out.printf("*%-13s %-32s %-10s %-15s*\n", "Model:", model, "Price:", "$" + df.format(price));
            System.out.printf("*%-13s %-27s %-10s %-9s*\n", "Color:", color, "Premium Cost Charged:",
                    "$" + df.format(premium));
        }

        db.closeConnection(con, stmt);
    }

    // insert query/save the vehicle details
    public void saveVehicleData() throws SQLException {

        DatabaseConnection db = new DatabaseConnection();
        con = DatabaseConnection.dbcon();

        sqlQuery = "INSERT INTO VEHICLE VALUES(?, ?, ?, ?, ? , ? , ? , ? , ? , ? ,?, ?, DEFAULT)";
        stmt = con.prepareStatement(sqlQuery);

        for (VehicleList vehicle : vList) {
            stmt.setInt(1, vehicleID);
            stmt.setString(2, vehicle.getMake());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getType());
            stmt.setString(6, vehicle.getFuel());
            stmt.setDouble(7, vehicle.getPrice());
            stmt.setString(8, vehicle.getColor());
            stmt.setDouble(9, vehicle.getPremium());
            stmt.setInt(10, getPolicyID());
            stmt.setString(11, getPolicyNum());
            stmt.setString(12, vehicle.getCdNum());
            stmt.executeUpdate();
        }
        db.closeConnection(con, stmt);
    }

    /* validates the year if it doesn't exceed to more than 40 years */
    public int validateYear(int year2) {
        try {
            int currentYear = LocalDate.now().getYear();
            vehicleAge = currentYear - year;

            if (vehicleAge >= 40) {
                System.out.println("Vehicle must be below 40 years of age.\n"
                        + "Please input a valid year that is not being purchased above or equal to 40 years. ");
                System.out.println("*****************************************\n");
                System.out.printf("Re-enter year:");
                year = scan.nextInt();
            }
        } catch (Exception e) {
            System.out.println("You have entered a wrong year format.");
        }
        return vehicleAge;
    }

    /* validates the integer/number */
    public int validateInteger(Scanner sc) throws Exception {
        try {
            if (!sc.hasNextInt()) {
                System.out.println("\n" + sc.next() + " is not a valid number. Please try again....");
                this.load();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sc.nextInt();
    }

    public static int getVehicleAge() {
        return vehicleAge;
    }

    /**
     * @return double return the purchasedPrice
     */

    public double getPurchasedPrice() {
        return purchasedPrice;
    }

    public static int getVehicleID() {

        return vehicleID;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public double getTotalPremium() {

        return totalPremiumCost;
    }

    public static int getCdNum() {
        return cdNum;
    }

    public String getConductionNum() {
        return conductionNum;
    }

    public double getPremium() {
        return premiumCost;
    }

    public static void setTotalPremium(double total_premiumcost){
        Vehicle.totalPremiumCost = total_premiumcost;
    }

}
