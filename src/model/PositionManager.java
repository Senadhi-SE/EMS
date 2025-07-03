package model;

import java.sql.ResultSet;

public class PositionManager {

    public String createPosition(int departmentId, String positionName, int maxHeadcount, int headcount, double salary, String status) throws Exception {
        if (positionName == null || positionName.isBlank()) {
            throw new NullPointerException("Position name cannot be null or blank");
        }

        if (status == null || (!status.equals("Active") && !status.equals("Inactive"))) {
            throw new IllegalArgumentException("Invalid status value");
        }

        if (maxHeadcount == 0) {
            throw new IllegalArgumentException("Max headcount cannot be zero");
        }

        // Check if position already exists for the given department
        String checkQuery = "SELECT * FROM position WHERE department_id = " + departmentId
                + " AND position_name = '" + positionName + "';";
        ResultSet rs = MySQL.executeSearch(checkQuery);
        if (rs.next()) {
            return "already_exists";
        }

        // Insert new position
        String insertQuery = "INSERT INTO position (department_id, position_name,maxHeadcount,headcount, salary, status) VALUES ("
                + departmentId + ", '"
                + positionName + "', "
                + maxHeadcount + ", "
                + headcount + ", "
                + salary + ", '"
                + status + "');";

        int count = MySQL.executeIUD(insertQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public ResultSet searchPositions() throws Exception {
        ResultSet rs = MySQL.executeSearch("SELECT "
                + "d.id AS department_id, "
                + "d.department_name, "
                + "d.status AS department_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.maxHeadcount, "
                + "p.headcount, "
                + "p.salary, "
                + "p.status AS position_status "
                + "FROM "
                + "department d "
                + "INNER JOIN "
                + "position p ON d.id = p.department_id");
        return rs;
    }

    public ResultSet searchPositions(String departmentName, String positionName) throws Exception {
        if (departmentName == null || departmentName.isBlank()) {
            throw new NullPointerException("Department name cannot be null or blank");
        }
        if (positionName == null || positionName.isBlank()) {
            throw new NullPointerException("Position name cannot be null or blank");
        }

        String sql = "SELECT "
                + "d.id AS department_id, "
                + "d.department_name, "
                + "d.status AS department_status, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "p.maxHeadcount, "
                + "p.headcount, "
                + "p.salary, "
                + "p.status AS position_status "
                + "FROM department d "
                + "INNER JOIN position p ON d.id = p.department_id "
                + "WHERE d.department_name = '" + departmentName + "' "
                + "AND p.position_name = '" + positionName + "'";

        ResultSet rs = MySQL.executeSearch(sql);
        return rs;
    }

    public ResultSet getVacancies() throws Exception {
        ResultSet rs = MySQL.executeSearch("SELECT "
                + "d.id AS department_id, "
                + "d.department_name, "
                + "p.id AS position_id, "
                + "p.position_name, "
                + "(p.maxHeadcount - p.headCount) AS vacant_positions, "
                + "p.maxHeadcount, "
                + "p.salary "
                + "FROM "
                + "position p "
                + "JOIN "
                + "department d ON p.department_id = d.id "
                + "WHERE "
                + "(p.maxHeadcount - p.headCount) > 0 "
                + "AND p.status = 'Active' "
                + "AND d.status = 'Active'");
        return rs;
    }

    public String updatePositions(String positionId, int maxHeadcount, String salary) throws Exception {

        if (positionId == null || positionId.isBlank()) {
            throw new NullPointerException("Position id cannot be null or blank");
        }

        if (maxHeadcount == 0) {
            throw new NullPointerException("maxHeadcount cannot be 0");
        }

        if (salary == null || salary.isBlank()) {
            throw new NullPointerException("salary value cannot be null or blank");
        }

        int count = MySQL.executeIUD("UPDATE position "
                + "SET "
                + "salary = '" + salary + "', "
                + "maxHeadcount = '" + maxHeadcount + "' "
                + "WHERE "
                + "id = '" + positionId + "';");
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String updatePositionStatus(String positionId, String status) throws Exception {
        if (positionId == null || positionId.isBlank()) {
            throw new NullPointerException("Position id cannot be null or blank");
        }

        if (status == null || (!status.equalsIgnoreCase("Active") && !status.equalsIgnoreCase("Inactive"))) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        int count = MySQL.executeIUD("UPDATE position "
                + "SET "
                + "status = '" + status + "' "
                + "WHERE "
                + "id = '" + positionId + "';");
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }

    }

}
