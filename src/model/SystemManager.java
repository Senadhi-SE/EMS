package model;

public class SystemManager {
    
    private static DepartmentManager departmentManager = new DepartmentManager();
    private static AttendanceManager attendanceManager = new AttendanceManager();
    private static PositionManager positionManager = new PositionManager();
    private static EmployeeManager employeeManager;
    private static ApplicantManager applicantManager;
    private static LeaveManager leaveManager;
    private static ScheduleManager scheduleManager;
    private static SalaryManager salaryManager;

    public static AttendanceManager getAttendanceManager() {
        if (attendanceManager == null) {
            attendanceManager = new AttendanceManager();
        }
        return attendanceManager;
    }

    public static DepartmentManager getDepartmentManager() {
        if (departmentManager == null) {
            departmentManager = new DepartmentManager();
        }
        return departmentManager;
    }

    public static PositionManager getPositionManager() {
        if (positionManager == null) {
            positionManager = new PositionManager();
        }
        return positionManager;
    }

    public static EmployeeManager getEmployeeManager() {
        if (employeeManager == null) {
            employeeManager = new EmployeeManager();
        }
        return employeeManager;
    }

    public static ApplicantManager getApplicantManager() {
        if (applicantManager == null) {
            applicantManager = new ApplicantManager();
        }
        return applicantManager;
    }

    public static LeaveManager getLeaveManager() {
        if (leaveManager == null) {
            leaveManager = new LeaveManager();
        }
        return leaveManager;
    }

    public static ScheduleManager getScheduleManager() {
        if (scheduleManager == null) {
            scheduleManager = new ScheduleManager();
        }
        return scheduleManager;
    }

    public static SalaryManager getSalaryManager() {
        if (salaryManager == null) {
            salaryManager = new SalaryManager();
        }
        return salaryManager;
    }
}
