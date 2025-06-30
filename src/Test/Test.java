
package Test;

import model.ApplicationManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
    public static void main(String[] args) {
        ResultSet rs = ApplicationManager.departmentManager.search("Sales");
        try {
            rs.next();
            System.out.println(rs.getString(2));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
