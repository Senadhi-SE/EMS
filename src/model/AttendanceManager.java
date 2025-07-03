package model;

import java.sql.ResultSet;

public class AttendanceManager {

    public String markInTime(String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(employeeQuery);
        if (!rs.next()) {
            return "employee_not_found";
        }
        int employeeId = rs.getInt("id");

        // Get current date and time details
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        int year = now.getYear();
        String month = now.getMonth().name(); // Returns UPPERCASE month name

        // Convert month to first-letter capitalized to match ENUM values
        month = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();

        String todayDate = now.toLocalDate().toString(); // yyyy-MM-dd

        // Check if in-time is already recorded today
        String checkQuery = "SELECT * FROM attendance WHERE employee_id = " + employeeId
                + " AND year = " + year
                + " AND month = '" + month + "'"
                + " AND DATE(`in`) = '" + todayDate + "';";
        ResultSet checkRs = MySQL.executeSearch(checkQuery);
        if (checkRs.next()) {
            return "in_time_already_recorded";
        }

        // Insert attendance record with in-time
        String inTime = now.toString().replace('T', ' '); // Format as "yyyy-MM-dd HH:mm:ss"

        String insertQuery = "INSERT INTO attendance (employee_id, year, month, `in`) VALUES ("
                + employeeId + ", "
                + year + ", '"
                + month + "', '"
                + inTime + "');";

        int count = MySQL.executeIUD(insertQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String markOutTime(String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(employeeQuery);
        if (!rs.next()) {
            return "employee_not_found";
        }
        int employeeId = rs.getInt("id");

        // Get current date and time details
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        int year = now.getYear();
        String month = now.getMonth().name(); // Returns UPPERCASE month name

        // Convert month to first-letter capitalized to match ENUM values
        month = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();

        String todayDate = now.toLocalDate().toString(); // yyyy-MM-dd

        // Check if in-time is recorded today
        String checkQuery = "SELECT * FROM attendance WHERE employee_id = " + employeeId
                + " AND year = " + year
                + " AND month = '" + month + "'"
                + " AND DATE(`in`) = '" + todayDate + "';";
        ResultSet checkRs = MySQL.executeSearch(checkQuery);
        if (!checkRs.next()) {
            return "in_time_not_marked";
        }

        // Check if out-time is already recorded
        String outTimeRecorded = checkRs.getString("out");
        if (outTimeRecorded != null) {
            return "out_time_already_recorded";
        }

        // Update out-time
        String outTime = now.toString().replace('T', ' '); // Format as "yyyy-MM-dd HH:mm:ss"

        String updateQuery = "UPDATE attendance SET `out` = '" + outTime + "' WHERE id = " + checkRs.getInt("id") + ";";

        int count = MySQL.executeIUD(updateQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public ResultSet getAttendanceByDate(String date) throws Exception {
        // Validation
        if (date == null || date.isBlank()) {
            throw new NullPointerException("Date cannot be null or blank");
        }

        // Check if date format is valid (basic check, you can enhance with regex or parsing if required)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }

        String sql = "SELECT "
                + "a.id AS attendance_id, "
                + "e.id AS employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "a.year, "
                + "a.month, "
                + "a.`in`, "
                + "a.`out` "
                + "FROM attendance a "
                + "INNER JOIN employee e ON a.employee_id = e.id "
                + "WHERE DATE(a.`in`) = '" + date + "' "
                + "ORDER BY a.`in` ASC;";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet getEmployeeAttendance(String nic, String month, String year) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (month == null || month.isBlank()) {
            throw new NullPointerException("Month cannot be null or blank");
        }

        if (year == null || year.isBlank()) {
            throw new NullPointerException("Year cannot be null or blank");
        }

        // Validate month: Should be a valid month name (January - December)
        String[] validMonths = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        boolean isValidMonth = false;
        for (String m : validMonths) {
            if (m.equalsIgnoreCase(month)) {
                isValidMonth = true;
                month = m; // Format month correctly with first-letter capitalized
                break;
            }
        }
        if (!isValidMonth) {
            throw new IllegalArgumentException("Invalid month value");
        }

        // Validate year: Should be 4 digits and numeric
        if (!year.matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid year format. Expected format: yyyy");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            throw new Exception("Employee not found for the given NIC");
        }
        int employeeId = rsEmp.getInt("id");

        String sql = "SELECT "
                + "a.id AS attendance_id, "
                + "a.year, "
                + "a.month, "
                + "a.`in`, "
                + "a.`out` "
                + "FROM attendance a "
                + "WHERE a.employee_id = " + employeeId + " "
                + "AND a.year = " + year + " "
                + "AND a.month = '" + month + "' "
                + "ORDER BY a.`in` ASC;";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet getDayAttendance(String nic, String date) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (date == null || date.isBlank()) {
            throw new NullPointerException("Date cannot be null or blank");
        }

        // Validate date format: yyyy-MM-dd
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            throw new Exception("Employee not found for the given NIC");
        }
        int employeeId = rsEmp.getInt("id");

        String sql = "SELECT "
                + "a.id AS attendance_id, "
                + "a.year, "
                + "a.month, "
                + "a.`in`, "
                + "a.`out` "
                + "FROM attendance a "
                + "WHERE a.employee_id = " + employeeId + " "
                + "AND DATE(a.`in`) = '" + date + "';";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public String deleteAttendance(String nic, String date) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (date == null || date.isBlank()) {
            throw new NullPointerException("Date cannot be null or blank");
        }

        // Validate date format: yyyy-MM-dd
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }

        // Check if employee exists and get their ID
        String employeeQuery = "SELECT id FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmp = MySQL.executeSearch(employeeQuery);
        if (!rsEmp.next()) {
            return "employee_not_found";
        }
        int employeeId = rsEmp.getInt("id");

        // Delete attendance record for the given date
        String deleteQuery = "DELETE FROM attendance WHERE employee_id = " + employeeId
                + " AND DATE(`in`) = '" + date + "';";

        int count = MySQL.executeIUD(deleteQuery);
        if (count == 0) {
            return "no_record_found";
        } else {
            return "success";
        }
    }

}
