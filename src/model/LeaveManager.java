package model;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LeaveManager {

    public String addLeave(String nic, String date, String reason) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (date == null || date.isBlank()) {
            throw new NullPointerException("Date cannot be null or blank");
        }

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }

        // Validate date format and logical correctness
        try {
            LocalDate parsedDate = LocalDate.parse(date); // Will throw exception if invalid
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format or value. Expected format: yyyy-MM-dd with valid date");
        }

        // Check if date is today or in the future
        LocalDate today = LocalDate.now();
        if (LocalDate.parse(date).isBefore(today)) {
            throw new IllegalArgumentException("Leave date cannot be in the past. It should be today or a future date.");
        }

        if (reason == null || reason.isBlank()) {
            throw new NullPointerException("Reason cannot be null or blank");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            return "employee_not_found";
        }
        int employeeId = rsEmp.getInt("id");

        // Check if leave is already recorded for the date
        String checkQuery = "SELECT * FROM `leave` WHERE employee_id = " + employeeId
                + " AND date = '" + date + "';";
        ResultSet rsCheck = MySQL.executeSearch(checkQuery);
        if (rsCheck.next()) {
            return "leave_already_recorded";
        }

        // Insert leave record
        String insertQuery = "INSERT INTO `leave` (employee_id, date, reason) VALUES ("
                + employeeId + ", '"
                + date + "', '"
                + reason + "');";

        int count = MySQL.executeIUD(insertQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String deleteLeave(String nic, String date) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (date == null || date.isBlank()) {
            throw new NullPointerException("Date cannot be null or blank");
        }

        // Validate date format and logical correctness
        try {
            LocalDate parsedDate = LocalDate.parse(date); // Throws exception if invalid
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format or value. Expected format: yyyy-MM-dd with valid date");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            return "employee_not_found";
        }
        int employeeId = rsEmp.getInt("id");

        // Delete leave record for the given date
        String deleteQuery = "DELETE FROM `leave` WHERE employee_id = " + employeeId
                + " AND date = '" + date + "';";

        int count = MySQL.executeIUD(deleteQuery);
        if (count == 0) {
            return "no_record_found";
        } else {
            return "success";
        }
    }

    public ResultSet searchLeaves(String nic, String month) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (month == null || month.isBlank()) {
            throw new NullPointerException("Month cannot be null or blank");
        }

        // Validate month: Should be a valid month name
        String[] validMonths = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        boolean isValidMonth = false;
        int monthNumber = 0;
        for (int i = 0; i < validMonths.length; i++) {
            if (validMonths[i].equalsIgnoreCase(month)) {
                isValidMonth = true;
                monthNumber = i + 1; // convert to 1-based month number
                break;
            }
        }
        if (!isValidMonth) {
            throw new IllegalArgumentException("Invalid month value");
        }

        // Format month number to 2 digits for MySQL LIKE comparison
        String monthStr = (monthNumber < 10) ? "0" + monthNumber : String.valueOf(monthNumber);

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            throw new Exception("Employee not found for the given NIC");
        }
        int employeeId = rsEmp.getInt("id");

        // Retrieve leave records within the given month
        String sql = "SELECT "
                + "l.id AS leave_id, "
                + "l.date, "
                + "l.reason, "
                + "e.fname, "
                + "e.lname "
                + "FROM `leave` l "
                + "INNER JOIN employee e ON l.employee_id = e.id "
                + "WHERE l.employee_id = " + employeeId + " "
                + "AND DATE_FORMAT(l.date, '%m') = '" + monthStr + "' "
                + "ORDER BY l.date ASC;";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet searchLeaves(String monthName) throws Exception {
        // Validation
        if (monthName == null || monthName.isBlank()) {
            throw new NullPointerException("Month name cannot be null or blank");
        }

        // Validate month name using regex (case-insensitive)
        if (!monthName.matches("(?i)January|February|March|April|May|June|July|August|September|October|November|December")) {
            throw new IllegalArgumentException("Invalid month name. Please provide a valid English month name.");
        }

        // Format month name to proper case for SQL MONTHNAME comparison
        String formattedMonth = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();

        String query = "SELECT "
                + "l.id AS leave_id, "
                + "l.employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "l.date, "
                + "l.reason "
                + "FROM `leave` l "
                + "INNER JOIN employee e ON l.employee_id = e.id "
                + "WHERE MONTHNAME(l.date) = '" + formattedMonth + "';";

        ResultSet rs = MySQL.executeSearch(query);
        return rs;
    }

}
