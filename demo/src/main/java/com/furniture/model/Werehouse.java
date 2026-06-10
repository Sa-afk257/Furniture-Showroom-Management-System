package com.furniture.model;

public class Werehouse {

	private int  warehouse_id;
	private String warehouseName;
	private String city;
	private String town;
	private String area;
	private String street;
	private String building;
	private int capacity;
	private int employee_id;

	public Werehouse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Werehouse(String warehouseName, String city, String town, String area, String street, String building,
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

	public Werehouse(int warehouse_id, String warehouseName, String city, String town, String area, String street,
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
	public int getWarehouse_id() {
		return warehouse_id;
	}
	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
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
	public int getEmployee_id() {
		return employee_id;
	}
	public void setEmployee_id(int employee_id) {
		this.employee_id = employee_id;
	}
	@Override
	public String toString() {
		return "Werehouse [warehouse_id=" + warehouse_id + ", warehouseName=" + warehouseName + ", city=" + city
				+ ", town=" + town + ", area=" + area + ", street=" + street + ", building=" + building + ", capacity="
				+ capacity + ", employee_id=" + employee_id + "]";
	} 
	
	
}


