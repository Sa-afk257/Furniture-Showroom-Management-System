USE FurnitureShowroomManagementSystem;


INSERT INTO Customer
(firstName, middelInitial, lastName, city, town, area, street, building, RegistrationDate)
VALUES
('Ahmad','M','Salem','Ramallah','Al-Tireh','Area A','Main Street','B12','2026-01-10 10:30:00'),
('Lina','A','Khaled','Nablus','Rafidia','West','University Street','N5','2026-01-12 12:00:00'),
('Omar','H','Yousef','Jenin','Center','Old City','Market Street','J3','2026-02-01 09:15:00'),
('Sara','K','Ali','Tulkarm','Downtown','Area B','School Street','T8','2026-02-08 14:20:00'),
('Mona','S','Ibrahim','Hebron','Al-Haras','South','Garden Street','H4','2026-03-01 16:40:00'),
('Noor','R','Hassan','Ramallah','Beitunia','Industrial','Factory Road','R9','2026-03-15 11:10:00'),
('Yazan','T','Nasser','Bethlehem','Center','Manger','Star Street','B7','2026-04-01 13:25:00'),
('Huda','L','Mansour','Jericho','City Center','Palm Area','Palm Street','J9','2026-04-20 10:00:00');

INSERT INTO Customer_phone VALUES
(1,'0599000001'),(1,'0568000001'),
(2,'0599000002'),
(3,'0599000003'),(3,'0568000003'),
(4,'0599000004'),
(5,'0599000005'),
(6,'0599000006'),(6,'0568000006'),
(7,'0599000007'),
(8,'0599000008');

INSERT INTO Employee
(firstName, middelInitial, lastName, city, email, salary, ShiftTime, HireDate, gender, Employee_role)
VALUES
('Admin','A','User','Ramallah','admin@furniture.com',2500,'Morning','2024-01-01','Male','admin'),
('Samir','S','Saleh','Ramallah','sales@furniture.com',1600,'Morning','2024-02-01','Male','sales_person'),
('Kareem','D','Driver','Ramallah','delivery@furniture.com',1300,'Morning','2024-03-01','Male','Delivery Employee'),
('Rana','W','Manager','Nablus','warehouse@furniture.com',1800,'Morning','2024-04-01','Female','warehouse_manager'),
('Nour','F','Staff','Hebron','staff@furniture.com',1200,'Evening','2024-05-01','Female','warehouse_staff');

INSERT INTO Employee_Phone VALUES
(1,'0599111111'),
(2,'0599222222'),
(3,'0599333333'),
(4,'0599444444'),
(5,'0599555555');

INSERT INTO Account (email, password, role) VALUES
('admin@furniture.com','123','Admin'),
('sales@furniture.com','123','EMPLOYEE'),
('delivery@furniture.com','123','EMPLOYEE'),
('warehouse@furniture.com','123','EMPLOYEE'),
('staff@furniture.com','123','EMPLOYEE'),
('ahmad@gmail.com','123','Customer'),
('lina@gmail.com','123','Customer'),
('omar@gmail.com','123','Customer'),
('sara@gmail.com','123','Customer');

INSERT INTO Employee_Account VALUES
(1,1),(2,2),(3,3),(4,4),(5,5);

INSERT INTO Customer_Account VALUES
(6,1),(7,2),(8,3),(9,4);

INSERT INTO Category (CategoryName) VALUES
('Living Room'),
('Bedroom'),
('Office'),
('Dining Room'),
('Storage'),
('Decoration');

INSERT INTO Product
(ProductName, price, CategoryID, CreatedDate, color, material, ProductDescription, ProductStatus, imagePath)
VALUES
('Modern Sofa',2500,1,'2026-01-02','LightGray','Fabric','Comfortable modern sofa','available','/images/products/modernSofa.jpg'),

('Wooden Bed',3200,2,'2026-02-07','Tangerine','Wood','King size wooden bed','available','/images/products/WoodenBed.jpg'),

('Office Desk',1500,3,'2026-03-10','Charcoal','MDF','Large office desk','available','/images/products/OfficeDesk.jpg'),

('Dining Table',2800,4,'2026-04-25','Champagne Gold','Wood','6-chair dining table','available','/images/products/DiningTable.jpg'),

('TV Stand',1200,1,'2026-05-19','Tan','Wood','Stylish TV stand','available','/images/products/TVStand.jpg'),

('Amber Velvet Armchair',850,1,'2026-05-20','Pumpkin Orange','Fabric','Luxury velvet accent chair for modern living rooms','available','/images/products/AmberVelvetArmchair.jpg'),

('Aurora Vanity Mirror',950,2,'2026-05-21','Cosmic Latte','Wood','Contemporary full-length vanity mirror with floating shelf','available','/images/products/AuroraVanityMirror.jpg'),

('Emerald Nightstand',750,2,'2026-05-22','DarkGreen','Wood','Modern bedside table with storage drawers','available','/images/products/EmeraldNightstand.jpg'),

('Harmony Study Desk',1800,3,'2026-05-23','WhiteSmoke','MDF','Study desk with integrated bookshelf and drawers','available','/images/products/HarmonyStudyDesk.jpg'),

('Ivory Cloud Bed',4500,2,'2026-05-24','Ivory','Fabric','Luxury upholstered cloud-style bed','available','/images/products/IvoryCloudBed.jpg'),

('Modern Wave Coffee Table',1450,1,'2026-05-25','Taupe','Wood','Modern curved coffee table with artistic design','available','/images/products/ModernWaveCoffeeTable.jpg'),

('Luxury Velvet Sofa',2700,1,'2026-05-26','Purple Obsidian','Fabric','Elegant velvet sofa with modern design','available','/images/products/1781259795726_sof.jpg');

INSERT INTO Supplier
(firstName, middelInitial, lastName, Supplier_type, email, city, town, area, street, building)
VALUES
('Khaled','A','Furni','Local','khaled@supplier.com','Ramallah','Al-Bireh','Industrial','Suppliers St','S1'),
('Omar','B','Wood','Local','omar@supplier.com','Nablus','Rafidia','West','Wood St','S2'),
('Global','C','Home','International','global@supplier.com','Amman','Center','Trade Area','Import St','S3');

INSERT INTO Supplier_Phone VALUES
(1,'0599666001'),
(2,'0599666002'),
(3,'0096279000001');

INSERT INTO Product_Supplier VALUES
(1,1),(1,5),(1,7),
(2,2),(2,4),(2,6),(2,8),
(3,3),(3,9),(3,10);

INSERT INTO Warehouse
(WarehouseName, city, town, area, street, building, capacity, EmployeeID)
VALUES
('Main Warehouse','Ramallah','Al-Bireh','Industrial','Warehouse Street','W1',1000,4),
('North Warehouse','Nablus','Rafidia','West','Storage Road','W2',700,4),
('South Warehouse','Hebron','Center','South','Depot Street','W3',500,4);

INSERT INTO Inventory VALUES
(1,1,10),(2,1,5),
(1,2,4),(2,2,1),
(1,3,20),(2,3,10),
(1,4,2),
(1,5,8),(3,5,3),
(1,6,1),
(1,7,15),
(2,8,6),
(1,9,0),
(3,10,12);

INSERT INTO Discount
(ProductID, WarehouseID, percentage, start_Date, end_Date)
VALUES
(1,1,10,'2026-06-01','2026-06-30'),
(3,1,15,'2026-06-01','2026-06-20'),
(7,1,5,'2026-06-01','2026-07-01');

INSERT INTO Sale
(CustomerID, EmployeeID, SaleDate, total_Amount, SaleStatus, ReviewNote)
VALUES
(1,2,CURDATE(),2500,'pending','New order waiting for sales review.'),
(2,2,CURDATE(),3200,'waiting_warehouse','Sent to warehouse for stock checking.'),
(3,2,CURDATE(),1500,'under_review','Warehouse replied partially available.'),
(4,2,CURDATE(),2800,'approved','Approved by sales employee.'),
(5,2,CURDATE(),1200,'completed','Order delivered successfully.'),
(6,2,CURDATE(),4100,'rejected','Rejected because stock is not available.'),
(7,2,DATE_SUB(CURDATE(), INTERVAL 1 DAY),700,'approved','Approved yesterday.'),
(8,2,DATE_SUB(CURDATE(), INTERVAL 2 DAY),900,'completed','Delivered two days ago.'),
(1,2,DATE_SUB(CURDATE(), INTERVAL 3 DAY),1100,'pending','Customer has multiple phones test.'),
(2,2,DATE_SUB(CURDATE(), INTERVAL 4 DAY),500,'waiting_warehouse','Waiting warehouse response.');

INSERT INTO SaleDetails VALUES
(1,1,1,2500),
(2,2,1,3200),
(3,3,1,1500),
(4,4,1,2800),
(5,5,1,1200),
(6,6,1,4100),
(7,7,1,700),
(8,8,1,900),
(9,9,1,1100),
(10,10,1,500);

INSERT INTO Payment
(SaleID, amount, Payment_Date, Payment_Method)
VALUES
(1,0,CURDATE(),'Cash'),
(2,3200,CURDATE(),'Visa'),
(3,800,CURDATE(),'Cash'),
(4,2800,CURDATE(),'Cash'),
(5,1200,CURDATE(),'Visa'),
(6,0,CURDATE(),'Cash'),
(7,700,DATE_SUB(CURDATE(), INTERVAL 1 DAY),'Cash'),
(8,900,DATE_SUB(CURDATE(), INTERVAL 2 DAY),'Visa'),
(9,0,DATE_SUB(CURDATE(), INTERVAL 3 DAY),'Cash'),
(10,500,DATE_SUB(CURDATE(), INTERVAL 4 DAY),'Cash');

INSERT INTO Warehouse_Request
(SaleID, SalesEmployeeID, WarehouseEmployeeID, RequestMessage, ResponseMessage, RequestStatus, RequestDate, ResponseDate)
VALUES
(2,2,4,'Please check product availability.','All items are available.','available',NOW(),NOW()),
(3,2,4,'Please check product availability.','Only part of the requested quantity is available.','partially_available',NOW(),NOW()),
(6,2,4,'Please check product availability.','Product is not available in stock.','not_available',NOW(),NOW()),
(10,2,NULL,'Please check product availability.',NULL,'pending',NOW(),NULL);

INSERT INTO Delivery
(SaleID, EmployeeID, Delivery_status, Delivery_Date)
VALUES
(4,3,'assigned',CURDATE()),
(5,3,'delivered',CURDATE()),
(7,3,'in_progress',CURDATE()),
(8,3,'delivered',DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(2,3,'pending',CURDATE()),
(3,3,'picked_up',CURDATE()),
(10,3,'issue_reported',CURDATE());

INSERT INTO ProductReturn
(SaleID, ProductID, quantity, ProductReturn_Date, comments)
VALUES
(5,5,1,CURDATE(),'Customer returned TV Stand due to color mismatch.');

INSERT INTO Purchase_Transaction
(SupplierID, Purchase_Date, EmployeeID, total_amount)
VALUES
(1,'2026-05-01',5,10000),
(2,'2026-05-10',5,15000),
(3,'2026-05-15',5,8000);

INSERT INTO Purchase_Details VALUES
(1,1,5,2000),
(1,5,5,900),
(2,2,3,2800),
(2,4,4,2300),
(3,10,10,350);

INSERT INTO StockMovement
(ProductID, movementType, quantity, movement_date)
VALUES
(1,'IN',5,'2026-05-01'),
(1,'OUT',1,CURDATE()),
(2,'IN',3,'2026-05-10'),
(3,'IN',10,'2026-05-11'),
(4,'OUT',1,CURDATE()),
(5,'RETURN',1,CURDATE()),
(9,'OUT',1,CURDATE());

INSERT INTO Cart (CustomerID) VALUES
(1),(2),(3),(4),(5),(6),(7),(8);

INSERT INTO CartItem VALUES
(1,1,1),
(1,7,2),
(2,2,1),
(3,3,1),
(4,4,1),
(5,5,1);

INSERT INTO Favorite VALUES
(1,1),(1,3),(1,7),
(2,2),(2,4),
(3,5),(3,8),
(4,1),(4,10);


