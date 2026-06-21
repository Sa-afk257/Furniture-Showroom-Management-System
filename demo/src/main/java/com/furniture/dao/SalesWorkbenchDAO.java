package com.furniture.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.furniture.model.OrderItem;
import com.furniture.model.PaymentInfo;
import com.furniture.model.OrderDetails;

import com.furniture.DBConnection;

public class SalesWorkbenchDAO {

    public int getPendingOrdersCount() {
        String sql = """
                    SELECT COUNT(*)
                    FROM Sale
                    WHERE SaleStatus IN ('pending', 'under_review', 'waiting_warehouse')
                """;

        return getInt(sql);
    }

    public String getWarehouseResponseStatus(int saleId) {

        String sql = """
                    SELECT RequestStatus
                    FROM Warehouse_Request
                    WHERE SaleID = ?
                    ORDER BY RequestDate DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("RequestStatus");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getApprovedTodayCount() {
        String sql = """
                    SELECT COUNT(*)
                    FROM Sale
                    WHERE SaleDate = CURDATE()
                    AND SaleStatus = 'approved'
                """;

        return getInt(sql);
    }

    public double getTodayRevenue() {
        String sql = """
                    SELECT IFNULL(SUM(total_Amount), 0)
                    FROM Sale
                    WHERE SaleDate = CURDATE()
                    AND SaleStatus IN ('approved', 'completed')
                """;

        return getDouble(sql);
    }

    public List<OrderSummary> getPendingOrders() {

        List<OrderSummary> orders = new ArrayList<>();

        String sql = """
                    SELECT
                        s.SaleID,
                        CONCAT(c.firstName, ' ', c.lastName) AS customerName,
                        s.SaleDate,
                        s.total_Amount,
                        IFNULL(SUM(p.amount), 0) AS paidAmount
                    FROM Sale s
                    JOIN Customer c ON s.CustomerID = c.CustomerID
                    LEFT JOIN Payment p ON s.SaleID = p.SaleID
                    WHERE s.SaleStatus IN ('pending', 'under_review', 'waiting_warehouse')
                    GROUP BY s.SaleID, customerName, s.SaleDate, s.total_Amount
                    ORDER BY s.SaleDate DESC, s.SaleID DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new OrderSummary(
                        rs.getInt("SaleID"),
                        rs.getString("customerName"),
                        rs.getString("SaleDate"),
                        rs.getDouble("total_Amount"),
                        rs.getDouble("paidAmount")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public int getOrdersInProgressCount() {
        String sql = """
                    SELECT COUNT(*)
                    FROM Delivery
                    WHERE Delivery_status = 'pending'
                """;

        return getInt(sql);
    }

    private int getInt(String sql) {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getPendingPaymentsCount() {
        String sql = """
                    SELECT COUNT(*)
                    FROM Sale s
                    LEFT JOIN (
                        SELECT SaleID, SUM(amount) AS paid
                        FROM Payment
                        GROUP BY SaleID
                    ) p ON s.SaleID = p.SaleID
                    WHERE IFNULL(p.paid, 0) < s.total_Amount
                    AND s.SaleStatus NOT IN ('rejected')
                """;

        return getInt(sql);
    }

    private double getDouble(String sql) {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static class OrderSummary {
        private int saleId;
        private String customerName;
        private String saleDate;
        private double totalAmount;
        private double paidAmount;

        public OrderSummary(int saleId, String customerName, String saleDate,
                double totalAmount, double paidAmount) {
            this.saleId = saleId;
            this.customerName = customerName;
            this.saleDate = saleDate;
            this.totalAmount = totalAmount;
            this.paidAmount = paidAmount;
        }

        public int getSaleId() {
            return saleId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getSaleDate() {
            return saleDate;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public double getPaidAmount() {
            return paidAmount;
        }
    }

    public boolean sendWarehouseRequest(int saleId, int salesEmployeeId, String message) {

        String checkSql = """
                    SELECT COUNT(*)
                    FROM Warehouse_Request
                    WHERE SaleID = ?
                    AND RequestStatus = 'pending'
                """;

        String insertSql = """
                    INSERT INTO Warehouse_Request
                    (SaleID, SalesEmployeeID, RequestMessage, RequestStatus)
                    VALUES (?, ?, ?, 'pending')
                """;

        String updateSaleSql = """
                    UPDATE Sale
                    SET SaleStatus = 'waiting_warehouse',
                        ReviewNote = 'Sent to warehouse for stock checking.'
                    WHERE SaleID = ?
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setInt(1, saleId);

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, saleId);
                ps.setInt(2, salesEmployeeId);
                ps.setString(3, message);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updateSaleSql)) {
                ps.setInt(1, saleId);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveOrder(int saleId, String note) {

        String sql = """
                    UPDATE Sale s
                    SET s.SaleStatus = 'approved',
                        s.ReviewNote = ?
                    WHERE s.SaleID = ?
                    AND NOT EXISTS (
                        SELECT 1
                        FROM Warehouse_Request wr
                        WHERE wr.SaleID = s.SaleID
                        AND wr.RequestStatus IN ('pending', 'not_available', 'partially_available')
                    )
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, note);
            ps.setInt(2, saleId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectOrder(int saleId, String reason) {

        String sql = """
                    UPDATE Sale
                    SET SaleStatus = 'rejected',
                        ReviewNote = ?
                    WHERE SaleID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reason);
            ps.setInt(2, saleId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderItem> getOrderItems(int saleId) {

        List<OrderItem> items = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductName,
                        p.color,
                        sd.quantity,
                        sd.price,
                        (sd.quantity * sd.price) AS totalPrice,
                        IFNULL(SUM(i.quantity), 0) AS availableQty,

                        CASE
                            WHEN IFNULL(SUM(i.quantity), 0) >= sd.quantity THEN 'Available'
                            WHEN IFNULL(SUM(i.quantity), 0) > 0 THEN 'Low Stock'
                            ELSE 'Out Of Stock'
                        END AS stockStatus

                    FROM SaleDetails sd
                    JOIN Product p
                        ON sd.ProductID = p.ProductID
                    LEFT JOIN Inventory i
                        ON p.ProductID = i.ProductID

                    WHERE sd.SaleID = ?

                    GROUP BY
                        p.ProductID,
                        p.ProductName,
                        p.color,
                        sd.quantity,
                        sd.price
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    items.add(new OrderItem(
                            rs.getString("ProductName"),
                            rs.getString("color"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            rs.getDouble("totalPrice"),
                            rs.getString("stockStatus")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public OrderDetails getOrderDetails(int saleId) {

        String sql = """
                    SELECT

                    CONCAT(c.firstName,' ',c.lastName) AS customerName,

                    cp.phone,

                    a.email,

                    CONCAT(
                        c.city, ', ',
                        c.town, ', ',
                        c.area, ', ',
                        c.street, ', ',
                        c.building
                    ) AS fullAddress

                    FROM Sale s

                    JOIN Customer c
                    ON s.CustomerID = c.CustomerID

                    LEFT JOIN Customer_phone cp
                    ON c.CustomerID = cp.CustomerID

                    LEFT JOIN Customer_Account ca
                    ON c.CustomerID = ca.CustomerID

                    LEFT JOIN Account a
                    ON ca.account_id = a.account_id

                    WHERE s.SaleID = ?

                    LIMIT 1
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new OrderDetails(
                            rs.getString("customerName"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("fullAddress"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PaymentInfo> getOrderPayments(int saleId) {

        List<PaymentInfo> payments = new ArrayList<>();

        String sql = """
                    SELECT PaymentID, amount, Payment_Date, Payment_Method
                    FROM Payment
                    WHERE SaleID = ?
                    ORDER BY Payment_Date DESC, PaymentID DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(new PaymentInfo(
                            rs.getInt("PaymentID"),
                            rs.getDouble("amount"),
                            rs.getString("Payment_Date"),
                            rs.getString("Payment_Method")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return payments;
    }

    public List<String> getOrderTimeline(int saleId) {

        List<String> timeline = new ArrayList<>();

        String saleSql = """
                    SELECT SaleID, SaleDate, SaleStatus, ReviewNote
                    FROM Sale
                    WHERE SaleID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(saleSql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    timeline.add("● Order Placed");
                    timeline.add(String.valueOf(rs.getDate("SaleDate")));

                    timeline.add("● Current Status");
                    timeline.add(rs.getString("SaleStatus"));

                    String note = rs.getString("ReviewNote");
                    if (note != null && !note.isBlank()) {
                        timeline.add("● Review Note");
                        timeline.add(note);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String requestSql = """
                    SELECT RequestStatus, RequestDate, ResponseDate, ResponseMessage
                    FROM Warehouse_Request
                    WHERE SaleID = ?
                    ORDER BY RequestDate DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(requestSql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    timeline.add("● Warehouse Request");
                    timeline.add(rs.getString("RequestStatus") + " | " + rs.getString("RequestDate"));

                    String response = rs.getString("ResponseMessage");
                    if (response != null && !response.isBlank()) {
                        timeline.add(response);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String deliverySql = """
                    SELECT Delivery_status, Delivery_Date
                    FROM Delivery
                    WHERE SaleID = ?
                    ORDER BY Delivery_Date DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(deliverySql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    timeline.add("● Delivery");
                    timeline.add(rs.getString("Delivery_status") + " | " + rs.getString("Delivery_Date"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeline;
    }

    public DeliveryInfo getDeliveryInfo(int saleId) {

        String sql = """
                    SELECT
                        d.Delivery_status,
                        d.Delivery_Date,
                        CONCAT(e.firstName, ' ', e.lastName) AS employeeName,
                        CONCAT(c.city, ', ', c.town, ', ', c.area, ', ', c.street, ', ', c.building) AS address
                    FROM Delivery d
                    JOIN Employee e ON d.EmployeeID = e.EmployeeID
                    JOIN Sale s ON d.SaleID = s.SaleID
                    JOIN Customer c ON s.CustomerID = c.CustomerID
                    WHERE d.SaleID = ?
                    ORDER BY d.Delivery_Date DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DeliveryInfo(
                            rs.getString("Delivery_status"),
                            rs.getString("Delivery_Date"),
                            rs.getString("employeeName"),
                            rs.getString("address"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addPayment(int saleId, double amount, String method) {

        String sql = """
                    INSERT INTO Payment
                    (SaleID, amount, Payment_Date, Payment_Method)
                    VALUES (?, ?, CURDATE(), ?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setDouble(2, amount);
            ps.setString(3, method);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public PendingOverview getPendingOverview() {

        String sql = """
                    SELECT
                        (
                            SELECT COUNT(*)
                            FROM Sale
                            WHERE SaleStatus = 'pending'
                        ) AS newOrders,

                        (
                            SELECT COUNT(*)
                            FROM Sale s
                            LEFT JOIN (
                                SELECT SaleID, SUM(amount) AS paid
                                FROM Payment
                                GROUP BY SaleID
                            ) p ON s.SaleID = p.SaleID
                            WHERE s.SaleStatus IN ('pending', 'under_review', 'waiting_warehouse')
                            AND IFNULL(p.paid, 0) < s.total_Amount
                        ) AS missingPayments,

                        (
                            SELECT COUNT(*)
                            FROM (
                                SELECT sd.SaleID
                                FROM SaleDetails sd
                                JOIN Sale s ON sd.SaleID = s.SaleID
                                LEFT JOIN Inventory i ON sd.ProductID = i.ProductID
                                WHERE s.SaleStatus IN ('pending', 'under_review', 'waiting_warehouse')
                                GROUP BY sd.SaleID, sd.ProductID, sd.quantity
                                HAVING IFNULL(SUM(i.quantity), 0) < sd.quantity
                            ) low_stock_orders
                        ) AS lowStockOrders,

                        (
                            SELECT COUNT(*)
                            FROM Sale s
                            JOIN Customer c ON s.CustomerID = c.CustomerID
                            WHERE s.SaleStatus IN ('pending', 'under_review', 'waiting_warehouse')
                            AND (
                                c.city IS NULL OR TRIM(c.city) = ''
                                OR c.town IS NULL OR TRIM(c.town) = ''
                                OR c.area IS NULL OR TRIM(c.area) = ''
                                OR c.street IS NULL OR TRIM(c.street) = ''
                                OR c.building IS NULL OR TRIM(c.building) = ''
                            )
                        ) AS missingAddresses
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new PendingOverview(
                        rs.getInt("newOrders"),
                        rs.getInt("missingPayments"),
                        rs.getInt("lowStockOrders"),
                        rs.getInt("missingAddresses"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PendingOverview(0, 0, 0, 0);
    }

    public PriorityAlerts getPriorityAlerts() {

        PendingOverview overview = getPendingOverview();

        String readySql = """
                    SELECT COUNT(*)
                    FROM Sale
                    WHERE SaleStatus = 'pending'
                    AND SaleID NOT IN (
                        SELECT SaleID
                        FROM Warehouse_Request
                        WHERE RequestStatus = 'pending'
                    )
                """;

        int readyToSend = getInt(readySql);

        return new PriorityAlerts(
                overview.getMissingPayments(),
                overview.getLowStockOrders(),
                overview.getMissingAddresses(),
                readyToSend);
    }

    public List<DailyRevenue> getWeeklyRevenueByDay() {

        List<DailyRevenue> result = new ArrayList<>();

        String sql = """
                    SELECT
                        DAYNAME(d.day_date) AS dayName,
                        IFNULL(SUM(s.total_Amount), 0) AS revenue
                    FROM (
                        SELECT CURDATE() - INTERVAL 6 DAY AS day_date
                        UNION ALL SELECT CURDATE() - INTERVAL 5 DAY
                        UNION ALL SELECT CURDATE() - INTERVAL 4 DAY
                        UNION ALL SELECT CURDATE() - INTERVAL 3 DAY
                        UNION ALL SELECT CURDATE() - INTERVAL 2 DAY
                        UNION ALL SELECT CURDATE() - INTERVAL 1 DAY
                        UNION ALL SELECT CURDATE()
                    ) d
                    LEFT JOIN Sale s
                        ON s.SaleDate = d.day_date
                        AND s.SaleStatus IN ('approved', 'completed')
                    GROUP BY d.day_date
                    ORDER BY d.day_date
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String dayName = rs.getString("dayName");

                if (dayName != null && dayName.length() > 3) {
                    dayName = dayName.substring(0, 3);
                }

                result.add(new DailyRevenue(
                        dayName,
                        rs.getDouble("revenue")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public WeeklyPerformanceSummary getWeeklyPerformanceSummary() {

        String sql = """
                    SELECT
                        IFNULL(SUM(CASE
                            WHEN SaleStatus IN ('approved', 'completed')
                            THEN total_Amount ELSE 0 END), 0) AS revenue,

                        SUM(CASE
                            WHEN SaleStatus IN ('approved', 'completed')
                            THEN 1 ELSE 0 END) AS approvedOrders,

                        COUNT(*) AS totalOrders
                    FROM Sale
                    WHERE SaleDate BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE()
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                double revenue = rs.getDouble("revenue");
                int approvedOrders = rs.getInt("approvedOrders");
                int totalOrders = rs.getInt("totalOrders");

                int approvalRate = 0;

                if (totalOrders > 0) {
                    approvalRate = (int) Math.round((approvedOrders * 100.0) / totalOrders);
                }

                return new WeeklyPerformanceSummary(
                        revenue,
                        approvedOrders,
                        approvalRate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new WeeklyPerformanceSummary(0, 0, 0);
    }

    public List<ActivityItem> getRecentActivity() {

        List<ActivityItem> activities = new ArrayList<>();

        String sql = """
                    SELECT
                        s.SaleID,
                        s.SaleStatus,
                        s.SaleDate,
                        CONCAT(c.firstName, ' ', c.lastName) AS customerName
                    FROM Sale s
                    JOIN Customer c ON s.CustomerID = c.CustomerID
                    ORDER BY s.SaleDate DESC, s.SaleID DESC
                    LIMIT 4
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String status = rs.getString("SaleStatus");

                String title = "Order #ORD-" + String.format("%04d", rs.getInt("SaleID"))
                        + " " + formatStatusText(status);

                activities.add(new ActivityItem(
                        String.valueOf(rs.getString("SaleDate")),
                        title,
                        rs.getString("customerName"),
                        formatBadgeText(status),
                        getBadgeStyle(status),
                        getDotStyle(status)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activities;
    }

    public WarehouseResponse getWarehouseResponse(int saleId) {

        String sql = """
                SELECT RequestStatus, ResponseMessage
                FROM Warehouse_Request
                WHERE SaleID = ?
                ORDER BY RequestDate DESC
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new WarehouseResponse(
                            rs.getString("RequestStatus"),
                            rs.getString("ResponseMessage"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class WarehouseResponse {
        private String status;
        private String message;

        public WarehouseResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    private String formatStatusText(String status) {

        if (status == null) {
            return "updated";
        }

        switch (status) {
            case "approved":
                return "approved";
            case "rejected":
                return "rejected";
            case "waiting_warehouse":
                return "sent to warehouse";
            case "under_review":
                return "under review";
            case "completed":
                return "completed";
            default:
                return "received";
        }
    }

    private String formatBadgeText(String status) {

        if (status == null) {
            return "Update";
        }

        switch (status) {
            case "approved":
                return "Approved";
            case "rejected":
                return "Rejected";
            case "waiting_warehouse":
                return "In Progress";
            case "completed":
                return "Completed";
            default:
                return "Pending";
        }
    }

    private String getBadgeStyle(String status) {

        if (status == null) {
            return "badge-payment";
        }

        switch (status) {
            case "approved":
            case "completed":
                return "badge-approved";
            case "rejected":
                return "badge-rejected";
            case "waiting_warehouse":
            case "under_review":
                return "badge-progress";
            default:
                return "badge-payment";
        }
    }

    private String getDotStyle(String status) {

        if (status == null) {
            return "activity-dot-blue";
        }

        switch (status) {
            case "approved":
            case "completed":
                return "activity-dot-green";
            case "rejected":
                return "activity-dot-red";
            case "waiting_warehouse":
            case "under_review":
                return "activity-dot-yellow";
            default:
                return "activity-dot-blue";
        }
    }

    public static class DeliveryInfo {

        private String status;
        private String date;
        private String employeeName;
        private String address;

        public DeliveryInfo(String status, String date, String employeeName, String address) {
            this.status = status;
            this.date = date;
            this.employeeName = employeeName;
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public String getAddress() {
            return address;
        }
    }

    public List<OrderSummary> getMissingPaymentOrders() {

        String sql = """
                    SELECT s.SaleID
                    FROM Sale s
                    LEFT JOIN (
                        SELECT SaleID, SUM(amount) AS paid
                        FROM Payment
                        GROUP BY SaleID
                    ) p ON s.SaleID = p.SaleID

                    WHERE IFNULL(p.paid,0) < s.total_Amount
                    AND s.SaleStatus IN (
                        'pending',
                        'under_review',
                        'waiting_warehouse'
                    )
                """;

        return loadOrdersBySaleIds(sql);
    }

    public List<OrderSummary> getLowStockOrders() {

        String sql = """
                    SELECT DISTINCT
                        s.SaleID
                    FROM Sale s
                    JOIN SaleDetails sd
                        ON s.SaleID = sd.SaleID
                    JOIN Inventory i
                        ON sd.ProductID = i.ProductID
                    WHERE i.quantity < sd.quantity
                """;

        return loadOrdersBySaleIds(sql);
    }

    public List<OrderSummary> getIncompleteAddressOrders() {

        String sql = """
                    SELECT s.SaleID
                    FROM Sale s
                    JOIN Customer c
                        ON s.CustomerID = c.CustomerID
                    WHERE c.city IS NULL
                       OR c.street IS NULL
                       OR c.building IS NULL
                """;

        return loadOrdersBySaleIds(sql);
    }

    public List<OrderSummary> getReadyToSendOrders() {

        String sql = """
                    SELECT SaleID
                    FROM Sale
                    WHERE SaleStatus='pending'
                """;

        return loadOrdersBySaleIds(sql);
    }

    private List<OrderSummary> loadOrdersBySaleIds(String sql) {

        List<OrderSummary> orders = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int saleId = rs.getInt("SaleID");

                OrderSummary order = getOrderSummaryById(saleId);

                if (order != null) {
                    orders.add(order);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    private OrderSummary getOrderSummaryById(int saleId) {

        String sql = """
                    SELECT
                        s.SaleID,
                        CONCAT(c.firstName,' ',c.lastName) AS customerName,
                        s.SaleDate,
                        s.total_Amount,
                        IFNULL(SUM(p.amount),0) AS paidAmount
                    FROM Sale s
                    JOIN Customer c
                        ON s.CustomerID = c.CustomerID
                    LEFT JOIN Payment p
                        ON s.SaleID = p.SaleID
                    WHERE s.SaleID = ?
                    GROUP BY
                        s.SaleID,
                        customerName,
                        s.SaleDate,
                        s.total_Amount
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new OrderSummary(
                            rs.getInt("SaleID"),
                            rs.getString("customerName"),
                            rs.getString("SaleDate"),
                            rs.getDouble("total_Amount"),
                            rs.getDouble("paidAmount"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<OrderSummary> loadOrdersByStatus(String status) {

        List<OrderSummary> orders = new ArrayList<>();

        String sql = """
                    SELECT
                        s.SaleID,
                        CONCAT(c.firstName,' ',c.lastName) AS customerName,
                        s.SaleDate,
                        s.total_Amount,
                        IFNULL(SUM(p.amount),0) AS paidAmount
                    FROM Sale s
                    JOIN Customer c ON s.CustomerID = c.CustomerID
                    LEFT JOIN Payment p ON s.SaleID = p.SaleID
                    WHERE s.SaleStatus = ?
                    GROUP BY
                        s.SaleID,
                        customerName,
                        s.SaleDate,
                        s.total_Amount
                    ORDER BY s.SaleDate DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    orders.add(new OrderSummary(
                            rs.getInt("SaleID"),
                            rs.getString("customerName"),
                            rs.getString("SaleDate"),
                            rs.getDouble("total_Amount"),
                            rs.getDouble("paidAmount")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public boolean createDeliveryForApprovedSale(int saleId) {

        String checkSql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE SaleID = ?
                """;

        String employeeSql = """
                SELECT EmployeeID
                FROM Employee
                WHERE Employee_role = 'Delivery Employee'
                ORDER BY EmployeeID
                LIMIT 1
                """;

        String insertSql = """
                INSERT INTO Delivery
                (SaleID, EmployeeID, Delivery_Date, Delivery_status)
                VALUES (?, ?, CURDATE(), 'assigned')
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(checkSql)) {

                check.setInt(1, saleId);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        return true;
                    }
                }
            }

            int deliveryEmployeeId = -1;

            try (PreparedStatement ps = con.prepareStatement(employeeSql);
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    deliveryEmployeeId = rs.getInt("EmployeeID");
                }
            }

            if (deliveryEmployeeId <= 0) {
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {

                ps.setInt(1, saleId);
                ps.setInt(2, deliveryEmployeeId);

                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class PendingOverview {

        private int newOrders;
        private int missingPayments;
        private int lowStockOrders;
        private int missingAddresses;

        public PendingOverview(int newOrders, int missingPayments, int lowStockOrders, int missingAddresses) {
            this.newOrders = newOrders;
            this.missingPayments = missingPayments;
            this.lowStockOrders = lowStockOrders;
            this.missingAddresses = missingAddresses;
        }

        public int getNewOrders() {
            return newOrders;
        }

        public int getMissingPayments() {
            return missingPayments;
        }

        public int getLowStockOrders() {
            return lowStockOrders;
        }

        public int getMissingAddresses() {
            return missingAddresses;
        }

        public int getTotal() {
            return newOrders + missingPayments + lowStockOrders + missingAddresses;
        }
    }

    public static class DailyRevenue {

        private String dayName;
        private double revenue;

        public DailyRevenue(String dayName, double revenue) {
            this.dayName = dayName;
            this.revenue = revenue;
        }

        public String getDayName() {
            return dayName;
        }

        public double getRevenue() {
            return revenue;
        }
    }

    public static class WeeklyPerformanceSummary {

        private double revenue;
        private int approvedOrders;
        private int approvalRate;

        public WeeklyPerformanceSummary(double revenue, int approvedOrders, int approvalRate) {
            this.revenue = revenue;
            this.approvedOrders = approvedOrders;
            this.approvalRate = approvalRate;
        }

        public double getRevenue() {
            return revenue;
        }

        public int getApprovedOrders() {
            return approvedOrders;
        }

        public int getApprovalRate() {
            return approvalRate;
        }
    }

    public static class ActivityItem {

        private String activityTime;
        private String title;
        private String description;
        private String badgeText;
        private String badgeStyle;
        private String dotStyle;

        public ActivityItem(String activityTime, String title, String description,
                String badgeText, String badgeStyle, String dotStyle) {

            this.activityTime = activityTime;
            this.title = title;
            this.description = description;
            this.badgeText = badgeText;
            this.badgeStyle = badgeStyle;
            this.dotStyle = dotStyle;
        }

        public String getActivityTime() {
            return activityTime;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getBadgeText() {
            return badgeText;
        }

        public String getBadgeStyle() {
            return badgeStyle;
        }

        public String getDotStyle() {
            return dotStyle;
        }
    }

    public static class PriorityAlerts {

        private int missingPayments;
        private int lowStockOrders;
        private int incompleteAddresses;
        private int readyToSend;

        public PriorityAlerts(int missingPayments, int lowStockOrders, int incompleteAddresses, int readyToSend) {
            this.missingPayments = missingPayments;
            this.lowStockOrders = lowStockOrders;
            this.incompleteAddresses = incompleteAddresses;
            this.readyToSend = readyToSend;
        }

        public int getMissingPayments() {
            return missingPayments;
        }

        public int getLowStockOrders() {
            return lowStockOrders;
        }

        public int getIncompleteAddresses() {
            return incompleteAddresses;
        }

        public int getReadyToSend() {
            return readyToSend;
        }
    }

    public List<OrderSummary> getPendingOrders(String keyword) {
        return filterOrders(getPendingOrders(), keyword);
    }

    public List<OrderSummary> getMissingPaymentOrders(String keyword) {
        return filterOrders(getMissingPaymentOrders(), keyword);
    }

    public List<OrderSummary> getLowStockOrders(String keyword) {
        return filterOrders(getLowStockOrders(), keyword);
    }

    public List<OrderSummary> getIncompleteAddressOrders(String keyword) {
        return filterOrders(getIncompleteAddressOrders(), keyword);
    }

    public List<OrderSummary> getReadyToSendOrders(String keyword) {
        return filterOrders(getReadyToSendOrders(), keyword);
    }

    public List<OrderSummary> getApprovedOrders(String keyword) {
        return filterOrders(getApprovedOrders(), keyword);
    }

    public List<OrderSummary> getRejectedOrders(String keyword) {
        return filterOrders(getRejectedOrders(), keyword);
    }

    public List<OrderSummary> getApprovedOrders() {
        return loadOrdersByStatus("approved");
    }

    public List<OrderSummary> getRejectedOrders() {
        return loadOrdersByStatus("rejected");
    }

    private List<OrderSummary> filterOrders(List<OrderSummary> orders, String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return orders;
        }

        String k = keyword.toLowerCase();

        return orders.stream()
                .filter(o -> String.valueOf(o.getSaleId()).contains(k)
                        || o.getCustomerName().toLowerCase().contains(k))
                .toList();
    }

}