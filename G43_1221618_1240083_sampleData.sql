USE FurnitureShowroomManagementSystem;

INSERT INTO Customer
(firstName, middelInitial, lastName, city, town, area, street, building, RegistrationDate)
VALUES
('Ahmad','M','Salem','Ramallah','Al-Tireh','North','Main Street','B1','2026-01-03'),
('Lina','A','Khaled','Nablus','Rafidia','West','AlQuds Street','B2','2026-02-11'),
('Omar','H','Yousef','Jenin','Center','East','Freedom Street','B3','2026-03-08'),
('Sara','K','Ali','Tulkarm','Downtown','South','Palestine Street','B4','2026-04-18'),
('Mona','S','Ibrahim','Hebron','Al-Haras','North','King Street','B5','2026-05-22'),
('Noor','R','Hamad','Ramallah','Al-Masyoun','Center','Olive Street','B6','2026-05-28'),
('Karam','N','Saleh','Bethlehem','Beit Sahour','Center','Church Street','B7','2026-06-15'),
('Huda','L','Nasser','Jericho','Center','East','Palm Street','B8','2026-06-20'),
('Tariq','Y','Awad','Qalqilya','Downtown','West','Market Street','B9','2026-07-01'),
('Reem','D','Zidan','Ramallah','Al-Bireh','Center','School Street','B10','2026-07-12');

INSERT INTO Customer_phone(CustomerID, phone)
VALUES
(1,'0599123456'),
(1,'022400111'),
(2,'0598765432'),
(2,'092345678'),
(3,'0567112233'),
(4,'0599988776'),
(4,'022901234'),
(5,'0567889900'),
(6,'0597332211'),
(7,'0597000007'),
(8,'0597000008'),
(9,'0597000009'),
(10,'0597000010');

INSERT INTO Category(CategoryName)
VALUES
('Living Room'), ('Bedroom'), ('Office'), ('Dining Room');

INSERT INTO Product
(ProductName, price, CategoryID, CreatedDate, color, material, ProductDescription, ProductStatus, imagePath)
VALUES
('Modern Sofa',2500,1,'2026-01-02','Gray','Fabric','Comfortable modern sofa','available','/images/products/modernSofa.jpg'),
('Wooden Bed',3200,2,'2026-02-07','Brown','Wood','King size wooden bed','available','/images/products/WoodenBed.jpg'),
('Office Desk',1500,3,'2026-03-10','Black','MDF','Large office desk','available','/images/products/OfficeDesk.jpg'),
('Dining Table',2800,4,'2026-04-25','White','Wood','6-chair dining table','available','/images/products/DiningTable.jpg'),
('TV Stand',1200,1,'2026-05-19','Brown','Wood','Stylish TV stand','available','/images/products/TVStand.jpg');

INSERT INTO Employee
(firstName, middelInitial, lastName, city, email, salary, ShiftTime, HireDate, gender, Employee_role)
VALUES
('Yousef','A','Mahmoud','Ramallah','yousef@gmail.com',3500,'Morning','2024-01-15','Male','warehouse_manager'),
('Rama','K','Naser','Nablus','rama@gmail.com',4200,'Evening','2023-10-11','Female','sales_person'),
('Ali','M','Hassan','Hebron','ali@gmail.com',3900,'Morning','2022-05-01','Male','delivery_manager'),
('Dana','S','Omar','Jenin','dana@gmail.com',4100,'Night','2021-12-20','Female','warehouse_manager');

INSERT INTO Warehouse
(WarehouseName, city, town, area, street, building, capacity, EmployeeID)
VALUES
('Main Warehouse','Ramallah','Industrial','North','Storage Street','W1',1000,1),
('Secondary Warehouse','Nablus','East Area','East','Warehouse Road','W2',700,4);

INSERT INTO Inventory(WarehouseID, ProductID, quantity)
VALUES
(1,1,18),
(1,2,5),
(1,3,20),
(2,4,2),
(2,5,8);

INSERT INTO Sale(CustomerID, EmployeeID, SaleDate, total_Amount)
VALUES
(1,2,'2026-01-12',5000),
(2,2,'2026-02-08',6400),
(3,2,'2026-03-14',4300),
(4,2,'2026-04-10',2000),
(5,2,'2026-05-01',2500),
(1,2,'2026-06-03',9800),
(2,2,'2026-07-18',1200),
(3,2,'2026-08-22',7600),
(4,2,'2026-09-05',5600),
(5,2,'2026-10-11',8400),
(6,2,'2026-11-19',3000),
(1,2,'2026-12-25',11000),
(10,2,'2026-07-20',7000);

INSERT INTO SaleDetails(SaleID, ProductID, quantity, price)
VALUES
(1,1,2,2500),
(2,2,2,3200),
(3,4,1,2800),
(3,5,1,1500),
(4,3,1,1500),
(5,1,1,2500),
(6,3,4,1500),
(6,5,1,1200),
(7,5,1,1200),
(8,1,3,2500),
(9,4,2,2800),
(10,2,2,3200),
(11,3,2,1500),
(12,1,4,2500),
(13,2,1,3200),
(13,4,1,2800),
(13,5,1,1000);

INSERT INTO Delivary(SaleID, EmployeeID, Delivary_status, Delivary_Date)
VALUES
(1,3,'delivered','2026-01-13'),
(2,3,'pending','2026-02-09'),
(3,3,'delivered','2026-03-15'),
(4,3,'cancelled','2026-04-11'),
(5,3,'pending','2026-05-02'),
(6,3,'delivered','2026-06-04'),
(7,3,'pending','2026-07-19'),
(8,3,'delivered','2026-08-23'),
(9,3,'delivered','2026-09-06'),
(10,3,'cancelled','2026-10-12'),
(11,3,'pending','2026-11-20'),
(12,3,'delivered','2026-12-26'),
(13,3,'pending','2026-07-21');

INSERT INTO Payment
(SaleID, amount, Payment_Date, Payment_Method)
VALUES
(1,5000,'2026-01-12','Cash'),
(2,6400,'2026-02-08','Cash'),
(3,4300,'2026-03-14','Card'),
(4,2000,'2026-04-10','Cash'),
(5,2500,'2026-05-01','Bank Transfer'),
(6,9800,'2026-06-03','Cash'),
(7,1200,'2026-07-18','Card'),
(8,7600,'2026-08-22','Cash'),
(9,5600,'2026-09-05','Bank Transfer'),
(10,8400,'2026-10-11','Cash'),
(11,3000,'2026-11-19','Card'),
(12,11000,'2026-12-25','Cash'),
(13,3000,'2026-07-20','Cash');

INSERT INTO Supplier
(firstName, middelInitial, lastName, Supplier_type, email,
 city, town, area, street, building)
VALUES
('Ahmad','M','Furniture','Local',
 'ahmad@furniture.com',
 'Ramallah','Center','North',
 'Industrial St','S1'),

('Global','A','Wood','International',
 'globalwood@gmail.com',
 'Nablus','Industrial','East',
 'Wood St','S2'),

('Modern','K','Design','Local',
 'moderndesign@gmail.com',
 'Tulkarm','Center','South',
 'Design St','S3');
 
INSERT INTO Product_Supplier
(SupplierID, ProductID)
VALUES
(1,1),
(1,5),
(2,2),
(2,4),
(3,3),
(3,5);

INSERT INTO Discount
(ProductID, WarehouseID, percentage, start_Date, end_Date)
VALUES
(1,1,15,'2026-01-01','2027-01-01'),
(3,1,10,'2026-05-01','2027-05-01'),
(5,2,20,'2026-03-01','2027-03-01');

INSERT INTO ProductReturn
(SaleID, ProductID, quantity,
 ProductReturn_Date, comments)
VALUES
(1,1,1,'2026-01-20','Damaged fabric'),
(5,1,1,'2026-05-05','Wrong color'),
(10,2,1,'2026-10-15','Customer request');

INSERT INTO Purchase_Transaction
(SupplierID, Purchase_Date,
 EmployeeID, total_amount)
VALUES
(1,'2026-01-01',1,12000),
(2,'2026-02-01',1,18000),
(3,'2026-03-01',1,10000);

INSERT INTO Purchase_Details
(PurchaseID, ProductID, quantity, price)
VALUES
(1,1,20,2000),
(1,5,15,900),
(2,2,10,2500),
(2,4,8,2200),
(3,3,12,1200);

INSERT INTO StockMovement
(ProductID, movementType,
 quantity, movement_date)
VALUES
(1,'IN',20,'2026-01-01'),
(1,'OUT',2,'2026-01-12'),
(2,'IN',10,'2026-02-01'),
(2,'OUT',2,'2026-02-08'),
(3,'IN',12,'2026-03-01'),
(3,'OUT',4,'2026-06-03'),
(4,'IN',8,'2026-02-01'),
(4,'OUT',1,'2026-03-14'),
(5,'IN',15,'2026-01-01'),
(5,'OUT',1,'2026-07-18');

INSERT INTO Employee_Phone(EmployeeID, phone)
VALUES
(1,'0591111111'),
(2,'0592222222'),
(3,'0593333333'),
(4,'0594444444');

INSERT INTO Supplier_Phone(SupplierID, phone)
VALUES
(1,'022955111'),
(1,'0595551111'),
(2,'092333222'),
(3,'0597778888');