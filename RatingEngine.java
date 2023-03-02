
/**********************************************************************************
 * Java Course 4 Capstone Project
 * 
 * Automobile Insurance Policy and Claims Administration System (PAS) Specification
 * 
 * @author: Jellie Mae Ortiz
 **********************************************************************************/

public class RatingEngine extends PolicyHolder {

    private static double price; // Premium
    private static double vp; // vehicle purchase price
    private static double vpf, dlx; // vehicle price factor

    public RatingEngine(double p, int y) {

        y = Vehicle.getVehicleAge(); // vehicle age
        vp = p;
        dlx = super.getYearIssued();

        if (y < 1) {
            vpf = 0.01;

        } else if (y < 3) {
            vpf = 0.008;

        } else if (y < 5) {
            vpf = 0.007;

        } else if (y < 10) {
            vpf = 0.006;

        } else if (y < 15) {
            vpf = 0.004;

        } else if (y < 20) {
            vpf = 0.002;
            
        } else if (y < 40) {
            vpf = 0.001;
        }
        price = (vp * vpf) + ((vp / 100)/dlx);
    }

    public double getPremium() {
        return price;
    }

    public double getDlx() {
        return dlx;
    }

}
