package model;

import java.sql.ResultSet;

public class DepartmentManager {

    public void create(String departmentName) {
        try {
            // requirement - we are not allowing to register departments which are already exists
            //INSERT INTO department (department_name) VALUES ('Human Resources');
            MySQL.executeIUD("INSERT INTO department (department_name) VALUES ('" + departmentName + "')");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delete(String departmentName) {
        try {
            MySQL.executeIUD("DELETE FROM department WHERE department_name = '" + departmentName + "'");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet search(String departmentName) {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * "
                    + "FROM department "
                    + "WHERE department_name = '" + departmentName + "'");
            return rs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }        
    }

}
