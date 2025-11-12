package Test;

import model.SystemManager;
import java.sql.ResultSet;
import model.BarcodeGenerator;

public class Test {

    public static void main(String[] args) {
        try {
            /*            
            Steps to create an employee
            SystemManager -> employeeManager -> .createEmployee(....)
            */
            
            //following line prints "success" if that operation is successful
            System.out.println(SystemManager.employeeManager.createEmployee(18, "hhhhh", "ddddd", "200327319410", "Female", "sithmi@gmail.com", "0784544285", "Thalalla"));
        } catch (Exception e) {
        }
    }
}
