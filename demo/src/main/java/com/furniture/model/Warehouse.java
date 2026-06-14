package com.furniture.model;

public class Warehouse {

	private int warehouse_id;
	private String warehouseName;
	private String city;
	private String town;
	private String area;
	private String street;
	private String building;
	private int capacity;
	private int employee_id;
	private int no;

	private String managerName;

	private double usedCapacity;

	private double remainingCapacity;

	private double usedPercent;

	private int productsCount;

	private String warehouseStatus;

	public Warehouse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Warehouse(String warehouseName, String city, String town, String area, String street, String building,
			int capacity, int employee_id) {
		this.warehouseName = warehouseName;
		this.city = city;
		this.town = town;
		this.area = area;
		this.street = street;
		this.building = building;
		this.capacity = capacity;
		this.employee_id = employee_id;
	}

	public Warehouse(int warehouse_id, String warehouseName, String city, String town, String area, String street,
			String building, int capacity, int employee_id) {
		super();
		this.warehouse_id = warehouse_id;
		this.warehouseName = warehouseName;
		this.city = city;
		this.town = town;
		this.area = area;
		this.street = street;
		this.building = building;
		this.capacity = capacity;
		this.employee_id = employee_id;
	}

	public int getWarehouseID() {
		return warehouse_id;
	}

	public void setWarehouseID(int warehouseID) {
		this.warehouse_id = warehouseID;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getEmployeeID() {
		return employee_id;
	}

	public void setEmployeeID(int employeeID) {
		this.employee_id = employeeID;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public double getUsedCapacity() {
		return usedCapacity;
	}

	public void setUsedCapacity(double usedCapacity) {
		this.usedCapacity = usedCapacity;
	}

	public double getRemainingCapacity() {
		return remainingCapacity;
	}

	public void setRemainingCapacity(double remainingCapacity) {
		this.remainingCapacity = remainingCapacity;
	}

	public double getUsedPercent() {
		return usedPercent;
	}

	public void setUsedPercent(double usedPercent) {
		this.usedPercent = usedPercent;
	}

	public int getProductsCount() {
		return productsCount;
	}

	public void setProductsCount(int productsCount) {
		this.productsCount = productsCount;
	}

	public String getWarehouseStatus() {
		return warehouseStatus;
	}

	public void setWarehouseStatus(String warehouseStatus) {
		this.warehouseStatus = warehouseStatus;
	}

	@Override
	public String toString() {
		return "Warehouse [warehouse_id=" + warehouse_id + ", warehouseName=" + warehouseName + ", city=" + city
				+ ", town=" + town + ", area=" + area + ", street=" + street + ", building=" + building + ", capacity="
				+ capacity + ", employee_id=" + employee_id + ", no=" + no + ", managerName=" + managerName
				+ ", usedCapacity=" + usedCapacity + ", remainingCapacity=" + remainingCapacity + ", usedPercent="
				+ usedPercent + ", productsCount=" + productsCount + ", warehouseStatus=" + warehouseStatus + "]";
	}

}
