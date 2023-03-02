/**********************************************************************************
 * Java Course 4 Capstone Project
 * 
 * Automobile Insurance Policy and Claims Administration System (PAS)
 * Specification
 * 
 * 
 * @author: Jellie Mae Ortiz
 **********************************************************************************/

public class VehicleList {

	private String make, model, vType, fuelType, color, cNum;
	private int year;
	private double price, premium;

	public VehicleList(String make, String model, int year, String type, String cNum, String fuel, double price,
			String color, double premium) {
		super();
		this.make = make;
		this.model = model;
		this.year = year;
		this.vType = type;
		this.cNum = cNum;
		this.fuelType = fuel;
		this.price = price;
		this.color = color;
		this.premium = premium;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public String getType() {
		return vType;
	}

	public String getCdNum() {
		return cNum;
	}

	public String getFuel() {
		return fuelType;
	}

	public String getColor() {
		return color;
	}

	public int getYear() {
		return year;
	}

	public double getPrice() {
		return price;
	}

	public double getPremium() {
		return premium;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setType(String type) {
		this.vType = type;
	}

	public void setCdNum(String cNum) {
		this.cNum = cNum;
	}

	public void setFuel(String fuel) {
		this.fuelType = fuel;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

}
