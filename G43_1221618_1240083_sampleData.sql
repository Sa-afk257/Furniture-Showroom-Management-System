USE FurnitureShowroomManagementSystem;

INSERT INTO Customer
(firstName, middelInitial, lastName, city, town, area, street, building, RegistrationDate)
VALUES
('Ahmad','M','Salem','Ramallah','Al-Tireh','North','Main Street','B1','2026-01-03 10:15:00'),
('Lina','A','Khaled','Nablus','Rafidia','West','AlQuds Street','B2','2026-02-11 14:20:00'),
('Omar','H','Yousef','Jenin','Center','East','Freedom Street','B3','2026-03-08 09:45:00'),
('Sara','K','Ali','Tulkarm','Downtown','South','Palestine Street','B4','2026-04-18 16:30:00'),
('Mona','S','Ibrahim','Hebron','Al-Haras','North','King Street','B5','2026-05-22 11:10:00'),
('Noor','R','Hamad','Ramallah','Al-Masyoun','Center','Olive Street','B6','2026-05-28 12:00:00');

INSERT INTO Category(CategoryName)
VALUES
('Living Room'), ('Bedroom'), ('Office'), ('Dining Room');

INSERT INTO Product
(ProductName, price, CategoryID, CreatedDate, color, material, ProductDescription, ProductStatus, imagePath)
VALUES
('Modern Sofa',2500,1,'2026-01-02 12:00:00','Gray','Fabric','Comfortable modern sofa','available','products/modernSofa.jpg'),
('Wooden Bed',3200,2,'2026-02-07 13:30:00','Brown','Wood','King size wooden bed','available','products/WoodenBed.jpg'),
('Office Desk',1500,3,'2026-03-10 09:00:00','Black','MDF','Large office desk','available','products/OfficeDesk.jpg'),
('Dining Table',2800,4,'2026-04-25 15:45:00','White','Wood','6-chair dining table','available','products/DiningTable.jpg'),
('TV Stand',1200,1,'2026-05-19 10:25:00','Brown','Wood','Stylish TV stand','available','products/TVStand.jpg');

INSERT INTO Employee
(firstName, middelInitial, lastName, city, email, salary, ShiftTime, HireDate, gender, Employee_role)
VALUES
('Yousef','A','Mahmoud','Ramallah','yousef@gmail.com',3500,'Morning','2024-01-15','Male','warehouse'),
('Rama','K','Naser','Nablus','rama@gmail.com',4200,'Evening','2023-10-11','Female','sales_person'),
('Ali','M','Hassan','Hebron','ali@gmail.com',3900,'Morning','2022-05-01','Male','delivery_managerShowroom'),
('Dana','S','Omar','Jenin','dana@gmail.com',4100,'Night','2021-12-20','Female','delivery_managerWarehouse');

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
(1,2,'2026-12-25',11000);

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
(12,1,4,2500);

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
(12,3,'delivered','2026-12-26');

INSERT INTO Payment(SaleID, amount, Payment_Date)
VALUES
(1,5000,'2026-01-12'),
(2,6400,'2026-02-08'),
(3,4300,'2026-03-14'),
(4,2000,'2026-04-10'),
(5,2500,'2026-05-01'),
(6,9800,'2026-06-03'),
(7,1200,'2026-07-18'),
(8,7600,'2026-08-22'),
(9,5600,'2026-09-05'),
(10,8400,'2026-10-11'),
(11,3000,'2026-11-19'),
(12,11000,'2026-12-25');

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
(ProductID, percentage, start_Date, end_Date)
VALUES
(1,15,'2026-01-01','2027-01-01'),
(3,10,'2026-05-01','2027-05-01'),
(5,20,'2026-03-01','2027-03-01');

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
