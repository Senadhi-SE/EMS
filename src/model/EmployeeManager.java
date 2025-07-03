package model;

import java.sql.ResultSet;

public class EmployeeManager {

    public String createEmployee(int positionId, String fname, String lname, String nic, String gender, String email, String mobile, String address) throws Exception {

        if (fname == null || fname.isBlank()) {
            throw new NullPointerException("First name cannot be null or blank");
        }

        if (lname == null || lname.isBlank()) {
            throw new NullPointerException("Last name cannot be null or blank");
        }

        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (gender == null || (!gender.equals("Male") && !gender.equals("Female"))) {
            throw new IllegalArgumentException("Invalid gender value");
        }

        if (email == null || email.isBlank()) {
            throw new NullPointerException("Email cannot be null or blank");
        }

        if (mobile == null || mobile.isBlank()) {
            throw new NullPointerException("Mobile number cannot be null or blank");
        }

        if (address == null || address.isBlank()) {
            throw new NullPointerException("Address cannot be null or blank");
        }

//        if (status == null || (!status.equals("Active") && !status.equals("Inactive"))) {
//            throw new IllegalArgumentException("Invalid status value");
//        }

        // Check if employee with same NIC already exists
        String checkQuery = "SELECT * FROM employee WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (rs.next()) {
            return "already_exists";
        }

        // Check if applicant with same NIC exists and delete if found
        String checkApplicantQuery = "SELECT * FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rsApplicant = MySQL.executeSearch(checkApplicantQuery);
        if (rsApplicant.next()) {
            int applicantId = rsApplicant.getInt("id");
            String deleteApplicantQuery = "DELETE FROM applicants WHERE id = " + applicantId + ";";
            MySQL.executeIUD(deleteApplicantQuery);
        }

        // Insert new employee
        String insertQuery = "INSERT INTO employee (position_id, fname, lname, nic, gender, email, mobile, address) VALUES ("
                + positionId + ", '"
                + fname + "', '"
                + lname + "', '"
                + nic + "', '"
                + gender + "', '"
                + email + "', '"
                + mobile + "', '"
                + address + "');";

        String updateQuery = "UPDATE position SET headCount = headCount + 1 WHERE id = '" + positionId + "'";
        int count = MySQL.executeIUD(insertQuery);
        MySQL.executeIUD(updateQuery);

        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String updateEmployee(
            String nic, String fname, String lname, String gender,
            String email, String mobile, String address) throws Exception {

        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (fname == null || fname.isBlank()) {
            throw new NullPointerException("First name cannot be null or blank");
        }

        if (lname == null || lname.isBlank()) {
            throw new NullPointerException("Last name cannot be null or blank");
        }

        if (gender == null || (!gender.equals("Male") && !gender.equals("Female"))) {
            throw new IllegalArgumentException("Invalid gender value");
        }

        if (email == null || email.isBlank()) {
            throw new NullPointerException("Email cannot be null or blank");
        }

        if (mobile == null || mobile.isBlank()) {
            throw new NullPointerException("Mobile number cannot be null or blank");
        }

        if (address == null || address.isBlank()) {
            throw new NullPointerException("Address cannot be null or blank");
        }

        // Check if employee exists
        String checkQuery = "SELECT * FROM employee WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (!rs.next()) {
            return "not_found";
        }

        // Update employee details
        String updateQuery = "UPDATE employee SET "
                + "fname = '" + fname + "', "
                + "lname = '" + lname + "', "
                + "gender = '" + gender + "', "
                + "email = '" + email + "', "
                + "mobile = '" + mobile + "', "
                + "address = '" + address + "' "
                + "WHERE nic = '" + nic + "';";

        int count = MySQL.executeIUD(updateQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String offboardEmployee(String nic, String status) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (status == null || (!status.equals("Active") && !status.equals("Inactive"))) {
            throw new IllegalArgumentException("Invalid status value");
        }

        // Check if employee exists
        String checkQuery = "SELECT * FROM employee WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (!rs.next()) {
            return "not_found";
        }

        // Update employee status
        String updateQuery = "UPDATE employee SET status = '" + status + "' WHERE nic = '" + nic + "';";

        int count = MySQL.executeIUD(updateQuery);
        if (count == 0) {
            return "failed";
        } else {
            return "success";
        }
    }

    public String updateAllowStatus(String nic, String status) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (status == null || (!status.equals("Allowed") && !status.equals("Not-Allowed"))) {
            throw new IllegalArgumentException("Invalid status value for allow status");
        }

        // Check if employee exists
        String checkQuery = "SELECT * FROM employee WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (!rs.next()) {
            return "not_found";
        }

        // Update employee status
        String updateQuery = "UPDATE employee SET allow_status = '" + status + "' WHERE nic = '" + nic + "';";

        int count = MySQL.executeIUD(updateQuery);
        if (count == 0) {
            return "failed";
        } else {
            return "success";
        }
    }

    public ResultSet searchEmployees() throws Exception {
        String sql = "SELECT "
                + "e.id AS employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "e.gender, "
                + "e.email, "
                + "e.mobile, "
                + "e.address, "
                + "e.status AS employee_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.salary "
                + "FROM employee e "
                + "INNER JOIN position p ON e.position_id = p.id;";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet searchEmployees(String nic) throws Exception {
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        String sql = "SELECT "
                + "e.id AS employee_id, "
                + "e.fname, "
                + "e.lname, "
                + "e.nic, "
                + "e.gender, "
                + "e.email, "
                + "e.mobile, "
                + "e.address, "
                + "e.status AS employee_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.salary "
                + "FROM employee e "
                + "INNER JOIN position p ON e.position_id = p.id "
                + "WHERE e.nic = '" + nic + "';";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

}
