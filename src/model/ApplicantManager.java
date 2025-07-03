package model;

import java.sql.ResultSet;

public class ApplicantManager {

    public String createApplicant(
            int positionId, String fname, String lname, String nic,
            String gender, String email, String mobile, String address, String cvPath) throws Exception {

        // Validation
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

        if (cvPath == null || cvPath.isBlank()) {
            throw new NullPointerException("CV path cannot be null or blank");
        }

        // Check if applicant with same NIC already exists
        String checkQuery = "SELECT * FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (rs.next()) {
            return "already_exists";
        }

        // Check if applicant is already registered as an employee
        String checkEmployeeQuery = "SELECT * FROM employee WHERE nic = '" + nic + "';";
        ResultSet rsEmployee = MySQL.executeSearch(checkEmployeeQuery);
        if (rsEmployee.next()) {
            return "already_registered_as_employee";
        }

        // Insert new applicant
        String insertQuery = "INSERT INTO applicants (position_id, fname, lname, nic, gender, email, mobile, address, cv_path) VALUES ("
                + positionId + ", '"
                + fname + "', '"
                + lname + "', '"
                + nic + "', '"
                + gender + "', '"
                + email + "', '"
                + mobile + "', '"
                + address + "', '"
                + cvPath + "');";

        int count = MySQL.executeIUD(insertQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public ResultSet searchAllApplicants() throws Exception {
        String sql = "SELECT "
                + "a.id AS applicant_id, "
                + "a.fname, "
                + "a.lname, "
                + "a.nic, "
                + "a.gender, "
                + "a.email, "
                + "a.mobile, "
                + "a.address, "
                + "a.cv_path, "
                + "a.selection_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.salary "
                + "FROM applicants a "
                + "INNER JOIN position p ON a.position_id = p.id;";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet searchApplicant(String nic) throws Exception {
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        String sql = "SELECT "
                + "a.id AS applicant_id, "
                + "a.fname, "
                + "a.lname, "
                + "a.nic, "
                + "a.gender, "
                + "a.email, "
                + "a.mobile, "
                + "a.address, "
                + "a.cv_path, "
                + "a.selection_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.salary "
                + "FROM applicants a "
                + "INNER JOIN position p ON a.position_id = p.id "
                + "WHERE a.nic = '" + nic + "';";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet searchApplicants(String positionId) throws Exception {
        if (positionId == null || positionId.isBlank()) {
            throw new NullPointerException("position id cannot be null or blank");
        }

        String sql = "SELECT "
                + "a.id AS applicant_id, "
                + "a.fname, "
                + "a.lname, "
                + "a.nic, "
                + "a.gender, "
                + "a.email, "
                + "a.mobile, "
                + "a.address, "
                + "a.cv_path, "
                + "a.selection_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.salary "
                + "FROM applicants a "
                + "INNER JOIN position p ON a.position_id = p.id "
                + "WHERE p.id = " + positionId + ";";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public String updateSelectionStatus(String nic, String selectionStatus) throws Exception {

        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (selectionStatus == null
                || (!selectionStatus.equals("Under-Review") && !selectionStatus.equals("Short-Listed"))) {
            throw new IllegalArgumentException("Invalid selection status value");
        }

        String checkQuery = "SELECT * FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (!rs.next()) {
            return "not_found";
        }

        String updateQuery = "UPDATE applicants SET selection_status = '" + selectionStatus + "' WHERE nic = '" + nic + "';";

        int count = MySQL.executeIUD(updateQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String deleteApplicant(String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Check if applicant exists
        String checkQuery = "SELECT * FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (!rs.next()) {
            return "not_found";
        }

        // Delete applicant
        String deleteQuery = "DELETE FROM applicants WHERE nic = '" + nic + "';";

        int count = MySQL.executeIUD(deleteQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String deleteApplicants(String positionId) throws Exception {
        // Validation
        if (positionId == null || positionId.isBlank()) {
            throw new NullPointerException("Position cannot be null or blank");
        }

        // Check if any applicants exist for this position
        String checkQuery = "SELECT * FROM applicants WHERE position_id = " + positionId + ";";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (!rs.next()) {
            return "not_found";
        }

        // Delete applicants for the given position ID
        String deleteQuery = "DELETE FROM applicants WHERE position_id = " + positionId + ";";

        int count = MySQL.executeIUD(deleteQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

}
