package model;

import java.sql.ResultSet;

public class DepartmentManager {

    public String createDepartment(String departmentName) throws Exception {
        if (departmentName == null || departmentName.isBlank()) {
            throw new NullPointerException("Department name cannot be null or blank");
        }
        ResultSet rs = MySQL.executeSearch("SELECT * "
                + "FROM department "
                + "WHERE department_name = '" + departmentName + "';");
        if (rs.next()) {
            return "already_exists";
        }
        int count = MySQL.executeIUD("INSERT INTO department (department_name) VALUES ('" + departmentName + "')");
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String deleteDepartment(String departmentName) throws Exception {
        if (departmentName == null || departmentName.isBlank()) {
            throw new NullPointerException("Department name cannot be null or blank");
        }
        int count = MySQL.executeIUD("DELETE FROM department WHERE department_name = '" + departmentName + "'");
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public ResultSet searchDepartment(String departmentName) throws Exception {
        if (departmentName == null || departmentName.isBlank()) {
            throw new NullPointerException("Department name cannot be null or blank");
        }
        ResultSet rs = MySQL.executeSearch("SELECT * "
                + "FROM department "
                + "WHERE department_name = '" + departmentName + "'");
        return rs;
    }

    public ResultSet searchDepartment() throws Exception {
        ResultSet rs = MySQL.executeSearch("SELECT * FROM department "
                + "WHERE status = 'Active';");
        return rs;
    }

    public String updateDepartment(String departmentName, String status) throws Exception {
        if (departmentName == null || departmentName.isBlank()) {
            throw new NullPointerException("Department name cannot be null or blank");
        }

        if (status == null || (!status.equalsIgnoreCase("Active") && !status.equalsIgnoreCase("Inactive"))) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        int count = MySQL.executeIUD("UPDATE department "
                + "SET status = '" + status + "' "
                + "WHERE department_name = '" + departmentName + "';");
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }
}
