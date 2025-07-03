package Test;

import model.SystemManager;
import java.sql.ResultSet;

public class Test {
    
    public static void main(String[] args) {
        try {
//            ResultSet result = SystemManager.salaryManager.getSalaries("909876543V");
//            while (result.next()) {
//                System.out.println(result.getString("fname") + " " + result.getString("month") + " " + result.getString("paid_amount"));
//            }
              System.out.println(SystemManager.employeeManager.createEmployee(19, "saman", "dissanayake", "200327311409", "Male", "maya@gmail.com", "0784544285", "matara"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
}
