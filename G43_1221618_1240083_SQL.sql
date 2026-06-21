DROP DATABASE IF EXISTS FurnitureShowroomManagementSystem;
CREATE DATABASE FurnitureShowroomManagementSystem ;
USE FurnitureShowroomManagementSystem;
	CREATE TABLE Customer(
		CustomerID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		firstName VARCHAR(32) NOT NULL,
		middelInitial VARCHAR(32) NOT NULL,
		lastName VARCHAR(32) NOT NULL,
		city VARCHAR(32) NOT NULL,
		town VARCHAR(32) NOT NULL,
		area VARCHAR(32) NOT NULL,
		street VARCHAR(32) NOT NULL,
		building VARCHAR(32) NOT NULL,
		RegistrationDate DATETIME NOT NULL
		);
    CREATE TABLE Customer_phone(
		CustomerID INT NOT NULL,
        phone VARCHAR(20) NOT NULL,
        PRIMARY KEY(CustomerID, phone),
        FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
        );
        
        CREATE TABLE Employee(
		EmployeeID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		firstName VARCHAR(32) NOT NULL,
		middelInitial VARCHAR(32) NOT NULL,
		lastName VARCHAR(32) NOT NULL,
		city VARCHAR(32) NOT NULL,
		email VARCHAR(32) NOT NULL,
		salary DOUBLE NOT NULL,
		ShiftTime VARCHAR(20) NOT NULL,
		HireDate DATE NOT NULL,
        gender VARCHAR(20) NOT NULL,
        Employee_role VARCHAR(32) NOT NULL,
        CHECK(gender IN ('Female', 'Male')),
        CHECK(Employee_role IN ('warehouse_manager', 'sales_person', 'warehouse_staff',
'Delivery Employee','admin'))
		);
    
    CREATE TABLE Employee_Phone(
		EmployeeID INT NOT NULL,
        phone VARCHAR(20) NOT NULL,
        PRIMARY KEY(EmployeeID, phone),
        FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
        );
        
	CREATE TABLE Sale(
        SaleID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        CustomerID INT NOT NULL,
        EmployeeID INT NOT NULL,
        SaleDate DATE NOT NULL,
        total_Amount DOUBLE NOT NULL,
        SaleStatus VARCHAR(30) NOT NULL,
        ReviewNote TEXT NULL,

        FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
        FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),

        CHECK (SaleStatus IN (
            'pending',
            'under_review',
            'waiting_warehouse',
            'approved',
            'rejected',
            'sent_to_warehouse',
            'completed'
        ))
    );

        
	CREATE TABLE Delivery(
		DeliveryID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		SaleID INT NOT NULL,
		EmployeeID INT NOT NULL,
		Delivery_status VARCHAR(20) NOT NULL,
		Delivery_Date Date NOT NULL,
		FOREIGN KEY (SaleID) REFERENCES Sale(SaleID),
        FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
        CHECK(Delivery_status IN ('pending',
								'assigned',
								'picked_up',
								'in_progress',
								'delivered',
								'issue_reported',
								'cancelled'))
		);
        
	CREATE TABLE Payment(
		PaymentID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		SaleID INT NOT NULL,
		amount DOUBLE NOT NULL,
		Payment_Date Date NOT NULL,
        Payment_Method VARCHAR(30) NOT NULL,
		FOREIGN KEY (SaleID) REFERENCES Sale(SaleID)
		);
        
	CREATE TABLE Category(
		CategoryID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        CategoryName VARCHAR(32) NOT NULL
		);
        
    CREATE TABLE Product(
		ProductID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		ProductName VARCHAR(32) NOT NULL,
		price DOUBLE NOT NULL,
		CategoryID INT NOT NULL,
		CreatedDate DATETIME NOT NULL,
        color VARCHAR(32) NOT NULL,
        material VARCHAR(32) NOT NULL,
        ProductDescription VARCHAR(255) DEFAULT 'No Description' ,
        ProductStatus VARCHAR(32) NOT NULL,
        imagePath VARCHAR(255) NOT NULL,
		FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID),
        CHECK(ProductStatus IN ('available', 'outOfStock'))
		);
        
	CREATE TABLE ProductReturn(
		ProductReturnID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        SaleID INT NOT NULL,
        ProductID INT NOT NULL,
		quantity INT NOT NULL,
		ProductReturn_Date DATE NOT NULL,
		comments TEXT NOT NULL,
		FOREIGN KEY (SaleID) REFERENCES Sale(SaleID),
        FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
		);

    
	CREATE TABLE SaleDetails(
        SaleID INT NOT NULL,
        ProductID INT NOT NULL,
		quantity INT NOT NULL,
		price DOUBLE NOT NULL,
        PRIMARY KEY(SaleID, ProductID),
		FOREIGN KEY (SaleID) REFERENCES Sale(SaleID),
        FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
		);
	CREATE TABLE Supplier(
        SupplierID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		firstName VARCHAR(32) NOT NULL,
		middelInitial VARCHAR(32) NOT NULL,
		lastName VARCHAR(32) NOT NULL,
		Supplier_type VARCHAR(32) NOT NULL,
		email VARCHAR(32) NOT NULL,
		city VARCHAR(32) NOT NULL,
		town VARCHAR(32) NOT NULL,
		area VARCHAR(32) NOT NULL,
		street VARCHAR(32) NOT NULL,
		building VARCHAR(32) NOT NULL,
        CHECK(Supplier_type IN ('Local', 'International'))
		);
	CREATE TABLE Supplier_Phone(
		SupplierID INT NOT NULL,
        phone VARCHAR(20) NOT NULL,
        PRIMARY KEY(SupplierID, phone),
        FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID)
        );
	
    CREATE TABLE Product_Supplier(
        SupplierID INT NOT NULL,
        ProductID INT NOT NULL ,
		PRIMARY KEY(SupplierID, ProductID),
        FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID),
		FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
		);
    CREATE TABLE Warehouse(
        WarehouseID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		WarehouseName VARCHAR(32) NOT NULL,
		city VARCHAR(32) NOT NULL,
		town VARCHAR(32) NOT NULL,
		area VARCHAR(32) NOT NULL,
		street VARCHAR(32) NOT NULL,
		building VARCHAR(32) NOT NULL,
        capacity INT NOT NULL,
        EmployeeID INT NOT NULL,
        FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
		);  
	CREATE TABLE Discount(
        DiscountID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		ProductID INT NOT NULL,
        WarehouseID INT NOT NULL,
		percentage DOUBLE NOT NULL,
		start_Date Date NOT NULL,
		end_Date Date NOT NULL,
        FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE,
        FOREIGN KEY (WarehouseID) REFERENCES Warehouse(WarehouseID) ON DELETE CASCADE
		);
	CREATE TABLE Inventory(
        WarehouseID INT NOT NULL,
		ProductID INT NOT NULL,
		quantity INT NOT NULL,
		PRIMARY KEY(WarehouseID, ProductID),
        FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE,
        FOREIGN KEY (WarehouseID) REFERENCES Warehouse(WarehouseID)
		);
	CREATE TABLE Purchase_Transaction(
        PurchaseID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		SupplierID INT NOT NULL,
		Purchase_Date Date NOT NULL,
        EmployeeID INT NOT NULL,
        total_amount DOUBLE NOT NULL,
        FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID),
        FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
		);
	CREATE TABLE Purchase_Details(
        PurchaseID INT NOT NULL,
		ProductID INT NOT NULL,
		quantity DOUBLE NOT NULL,
        price DOUBLE NOT NULL,
		PRIMARY KEY(PurchaseID,ProductID),
        FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE,
        FOREIGN KEY (PurchaseID) REFERENCES Purchase_Transaction(PurchaseID)
		);
	CREATE TABLE StockMovement(
        movementID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		ProductID INT NOT NULL,
		movementType VARCHAR(32) NOT NULL,
        quantity DOUBLE NOT NULL,
        movement_date Date NOT NULL,
        FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
		);
        
	CREATE TABLE Account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN','EMPLOYEE','Customer') NOT NULL
);

CREATE TABLE Employee_Account (
    account_id INT PRIMARY KEY,
    EmployeeID INT NOT NULL UNIQUE,

    FOREIGN KEY (account_id)
        REFERENCES Account(account_id)
        ON DELETE CASCADE,

    FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID)
);

CREATE TABLE Customer_Account (
    account_id INT PRIMARY KEY,
    CustomerID INT NOT NULL UNIQUE,

    FOREIGN KEY (account_id)
        REFERENCES Account(account_id)
        ON DELETE CASCADE,

    FOREIGN KEY (CustomerID)
        REFERENCES Customer(CustomerID)
);

CREATE TABLE Favorite (
    CustomerID INT NOT NULL,
    ProductID INT NOT NULL,

    PRIMARY KEY (CustomerID, ProductID),

    FOREIGN KEY (CustomerID)
        REFERENCES Customer(CustomerID)
        ON DELETE CASCADE,

    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);

CREATE TABLE Cart (
    CartID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT NOT NULL UNIQUE,

    FOREIGN KEY (CustomerID)
        REFERENCES Customer(CustomerID)
        ON DELETE CASCADE
);

CREATE TABLE CartItem (
    CartID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,

    PRIMARY KEY (CartID, ProductID),

    FOREIGN KEY (CartID)
        REFERENCES Cart(CartID)
        ON DELETE CASCADE,

    FOREIGN KEY (ProductID)
        REFERENCES Product(ProductID)
        ON DELETE CASCADE
);
	
CREATE TABLE Warehouse_Request (
    RequestID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    SaleID INT NOT NULL,
    SalesEmployeeID INT NOT NULL,
    WarehouseEmployeeID INT NULL,

    RequestMessage TEXT NOT NULL,
    ResponseMessage TEXT NULL,

    RequestStatus VARCHAR(30) NOT NULL DEFAULT 'pending',
    RequestDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ResponseDate DATETIME NULL,

    FOREIGN KEY (SaleID) REFERENCES Sale(SaleID),
    FOREIGN KEY (SalesEmployeeID) REFERENCES Employee(EmployeeID),
    FOREIGN KEY (WarehouseEmployeeID) REFERENCES Employee(EmployeeID),

    CHECK (RequestStatus IN (
		'pending',
        'available',
        'partially_available',
        'not_available'
    ))
);

	
	
	
    
	
	
    
	
	
    
	
        
	
        
        
        
        
	
    