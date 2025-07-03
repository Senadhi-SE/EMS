package model;

import java.sql.ResultSet;

public class SalaryManager {

    public ResultSet search(String monthName) throws Exception {
        // Validation
        if (monthName == null || monthName.isBlank()) {
            throw new NullPointerException("Month name cannot be null or blank");
        }

        // Validate month name using regex (case-insensitive)
        if (!monthName.matches("(?i)January|February|March|April|May|June|July|August|September|October|November|December")) {
            throw new IllegalArgumentException("Invalid month name. Please provide a valid English month name.");
        }

        // Format month name to proper case for SQL ENUM comparison
        String formattedMonth = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

        String query = "SELECT "
                + "s.id AS salary_id, "
                + "s.year, "
                + "s.month, "
                + "s.employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "s.gross_amount, "
                + "s.deductions, "
                + "`paid_amount`, "
                + "s.created_at "
                + "FROM salary s "
                + "INNER JOIN employee e ON s.employee_id = e.id "
                + "WHERE s.month = '" + formattedMonth + "';";

        ResultSet rs = MySQL.executeSearch(query);
        return rs;
    }

    public ResultSet search(String monthName, String nic) throws Exception {
        // Validations
        if (monthName == null || monthName.isBlank()) {
            throw new NullPointerException("Month name cannot be null or blank");
        }
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Validate month name using regex (case-insensitive)
        if (!monthName.matches("(?i)January|February|March|April|May|June|July|August|September|October|November|December")) {
            throw new IllegalArgumentException("Invalid month name. Please provide a valid English month name.");
        }

        // Format month name to proper case for SQL ENUM comparison
        String formattedMonth = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

        // Check if employee exists with given NIC
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            throw new IllegalArgumentException("Employee with NIC " + nic + " not found.");
        }
        int employeeId = rsEmp.getInt("id");

        // Build final query
        String query = "SELECT "
                + "s.id AS salary_id, "
                + "s.year, "
                + "s.month, "
                + "s.employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "s.gross_amount, "
                + "s.deductions, "
                + "`paid_amount`, "
                + "s.created_at "
                + "FROM salary s "
                + "INNER JOIN employee e ON s.employee_id = e.id "
                + "WHERE s.month = '" + formattedMonth + "' "
                + "AND e.nic = '" + nic + "';";

        ResultSet rs = MySQL.executeSearch(query);
        return rs;
    }

    public ResultSet getSalaries(String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Check if employee exists with given NIC
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            throw new IllegalArgumentException("Employee with NIC " + nic + " not found.");
        }
        int employeeId = rsEmp.getInt("id");

        // Build query to retrieve salary records for the employee
        String query = "SELECT "
                + "s.id AS salary_id, "
                + "s.year, "
                + "s.month, "
                + "s.employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "s.gross_amount, "
                + "s.deductions, "
                + "`paid_amount`, "
                + "s.created_at "
                + "FROM salary s "
                + "INNER JOIN employee e ON s.employee_id = e.id "
                + "WHERE e.nic = '" + nic + "';";

        ResultSet rs = MySQL.executeSearch(query);
        return rs;
    }

    public String delete(String monthName, String nic) throws Exception {
        // Validation
        if (monthName == null || monthName.isBlank()) {
            throw new NullPointerException("Month name cannot be null or blank");
        }
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Validate month name using regex (case-insensitive)
        if (!monthName.matches("(?i)January|February|March|April|May|June|July|August|September|October|November|December")) {
            throw new IllegalArgumentException("Invalid month name. Please provide a valid English month name.");
        }

        // Format month name to proper case for ENUM comparison
        String formattedMonth = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

        // Check if employee exists with given NIC
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            return "employee_not_found";
        }
        int employeeId = rsEmp.getInt("id");

        // Delete salary records for given employee and month
        String deleteQuery = "DELETE FROM salary WHERE employee_id = " + employeeId
                + " AND month = '" + formattedMonth + "';";

        int count = MySQL.executeIUD(deleteQuery);
        if (count == 0) {
            return "no_records_deleted";
        } else {
            return "success";
        }
    }

    public String createSalaryRecord(String year, String monthName, String nic, double grossAmount, double deductions) throws Exception {
        // Validations
        if (year == null || year.isBlank()) {
            throw new NullPointerException("Year cannot be null or blank");
        }
        if (!year.matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid year format. Expected format: yyyy");
        }

        if (monthName == null || monthName.isBlank()) {
            throw new NullPointerException("Month name cannot be null or blank");
        }

        if (!monthName.matches("(?i)January|February|March|April|May|June|July|August|September|October|November|December")) {
            throw new IllegalArgumentException("Invalid month name. Please provide a valid English month name.");
        }

        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (grossAmount < 0) {
            throw new IllegalArgumentException("Gross amount cannot be negative");
        }

        if (deductions < 0) {
            throw new IllegalArgumentException("Deductions cannot be negative");
        }

        if (deductions > grossAmount) {
            throw new IllegalArgumentException("Deductions cannot exceed gross amount");
        }

        double paidAmount = grossAmount - deductions;

        // Format month name properly for ENUM comparison
        String formattedMonth = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

        // Check if employee exists and get ID
        String empQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(empQuery);
        if (!rsEmp.next()) {
            return "employee_not_found";
        }
        int employeeId = rsEmp.getInt("id");

        // Check if salary record already exists for that employee and month
        String checkQuery = "SELECT * FROM salary WHERE employee_id = " + employeeId
                + " AND month = '" + formattedMonth + "' AND year = " + year + ";";
        ResultSet rsCheck = MySQL.executeSearch(checkQuery);
        if (rsCheck.next()) {
            return "salary_record_already_exists";
        }

        // Check attendance + leave count for the month
        String attendanceQuery = "SELECT COUNT(*) AS attendance_count "
                + "FROM attendance "
                + "WHERE employee_id = " + employeeId
                + " AND month = '" + formattedMonth + "' "
                + "AND YEAR(`in`) = " + year + ";";
        ResultSet rsAttendance = MySQL.executeSearch(attendanceQuery);
        int attendanceCount = 0;
        if (rsAttendance.next()) {
            attendanceCount = rsAttendance.getInt("attendance_count");
        }

        String leaveQuery = "SELECT COUNT(*) AS leave_count "
                + "FROM `leave` "
                + "WHERE employee_id = " + employeeId
                + " AND MONTHNAME(date) = '" + formattedMonth + "' "
                + "AND YEAR(date) = " + year + ";";
        ResultSet rsLeave = MySQL.executeSearch(leaveQuery);
        int leaveCount = 0;
        if (rsLeave.next()) {
            leaveCount = rsLeave.getInt("leave_count");
        }

        int totalDays = attendanceCount + leaveCount;
        if (totalDays < 20) {
            return "cannot_pay_salary_due_to_low_attendance_and_leave_count";
        }

        // Insert salary record
        String insertQuery = "INSERT INTO salary (year, month, employee_id, gross_amount, deductions, `paid_amount`) VALUES ('"
                + year + "', '"
                + formattedMonth + "', "
                + employeeId + ", '"
                + grossAmount + "', '"
                + deductions + "', '"
                + paidAmount + "');";

        int count = MySQL.executeIUD(insertQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

}
