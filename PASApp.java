
/**********************************************************************************
 * Java Course 4 Capstone Project
 * 
 * Automobile Insurance Policy and Claims Administration System (PAS) Specification
 * 
 * 
 * @author: Jellie Mae Ortiz
 **********************************************************************************/

import java.util.Scanner;

public class PASApp {

    public static void main(String[] args) throws Exception {

        String choice;
        Scanner sc = new Scanner(System.in);
        do {

            System.out.println("\n \t \t \t PAS Menu of Options");
            System.out.println("**************************************************************************\n");
            System.out.println("\t\t[1] CREATE A NEW CUSTOMER ACCOUNT \n" +
                    "\t\t[2] GET A POLICY QUOTE AND BUY THE POLICY\n" +
                    "\t\t[3] CANCEL A SPECIFIC POLICY\n" +
                    "\t\t[4] FILE AN ACCIDENT CLAIM AGAINST A POLICY\n" +
                    "\t\t[5] SEARCH FOR CUSTOMER ACCOUNT\n" +
                    "\t\t[6] SEARCH FOR AND DISPLAY A SPECIFIC POLICY\n" +
                    "\t\t[7] SEARCH FOR AND DISPLAY A SPECIFIC CLAIM\n" +
                    "\t\t[8] EXIT THE PAS SYSTEM\n");
            System.out.println("*************************************************************************");
            System.out.printf("\t\t\tChoose one of the options (1-8): ");
            choice = sc.next();

            switch (choice) {
                case "1":
                    System.out.print("\033[H\033[2J"); // clear screen
                    CustomerAccount cus = new CustomerAccount();
                    cus.loadCustomerInput() ;
                    break;
                case "2":
                    System.out.print("\033[H\033[2J"); // clear screen
                    Policy po = new Policy();
                    po.availPolicy();
                    break;
                case "3":
                    System.out.print("\033[H\033[2J"); // clear screen
                    Policy cancelPo = new Policy();
                    cancelPo.cancelPolicy();
                    break;
                case "4":
                    System.out.print("\033[H\033[2J"); // clear screen
                    Claim c = new Claim();
                    c.loadClaim();
                    break;
                case "5":
                    System.out.print("\033[H\033[2J");
                    CustomerAccount cusFind = new CustomerAccount();
                    cusFind.searchAccount();
                    break;
                case "6":
                    System.out.print("\033[H\033[2J");
                    Policy pSearch = new Policy();
                    pSearch.searchPolicy();
                    break;
                case "7":
                    System.out.print("\033[H\033[2J");
                    Claim cSearch = new Claim();
                    cSearch.searchClaim();
                    break;
                case "8":
                    System.out.print("\033[H\033[2J");
                    System.out.println("Thank you so much. Come again to get your policy!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }

        } while (!choice.equals("8"));

        sc.close();
    }
}
