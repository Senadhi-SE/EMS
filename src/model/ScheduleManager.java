package model;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ScheduleManager {

    public String createSchedule(String date, String time, String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        if (date == null || date.isBlank()) {
            throw new NullPointerException("Date cannot be null or blank");
        }

        if (time == null || time.isBlank()) {
            throw new NullPointerException("Time cannot be null or blank");
        }

        // Validate date format and logical correctness
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date); // Throws exception if invalid
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format or value. Expected format: yyyy-MM-dd with valid date");
        }

        // Validate time format and logical correctness (HH:mm format only)
        LocalTime parsedTime;
        try {
            // Use DateTimeFormatter to parse HH:mm format strictly
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            parsedTime = LocalTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format or value. Expected format: HH:mm with valid time");
        }

        // Check if date is today or future
        LocalDate today = LocalDate.now();
        if (parsedDate.isBefore(today)) {
            throw new IllegalArgumentException("Schedule date must be today or a future date.");
        }

        // If date is today, check time is future
        if (parsedDate.isEqual(today)) {
            LocalTime now = LocalTime.now();
            if (parsedTime.isBefore(now)) {
                throw new IllegalArgumentException("Schedule time must be a future time for today.");
            }
        }

        // Check if applicant exists and get their ID
        String applicantQuery = "SELECT id FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rsApp = MySQL.executeSearch(applicantQuery);
        if (!rsApp.next()) {
            return "applicant_not_found";
        }
        int applicantId = rsApp.getInt("id");

        // Check if a schedule already exists for this applicant (only one allowed per applicant)
        String checkQuery = "SELECT * FROM schedule WHERE applicant_id = " + applicantId + ";";
        ResultSet rsCheck = MySQL.executeSearch(checkQuery);
        if (rsCheck.next()) {
            return "schedule_already_exists_for_applicant";
        }

        // Insert schedule record
        String insertQuery = "INSERT INTO schedule (date, time, applicant_id) VALUES ('"
                + date + "', '"
                + time + "', "
                + applicantId + ");";

        int count = MySQL.executeIUD(insertQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public String deleteSchedule(String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        // Check if applicant exists and get their ID
        String applicantQuery = "SELECT id FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rsApp = MySQL.executeSearch(applicantQuery);
        if (!rsApp.next()) {
            return "applicant_not_found";
        }
        int applicantId = rsApp.getInt("id");

        // Check if schedule exists for this applicant
        String checkQuery = "SELECT * FROM schedule WHERE applicant_id = " + applicantId + ";";
        ResultSet rsCheck = MySQL.executeSearch(checkQuery);
        if (!rsCheck.next()) {
            return "schedule_not_found";
        }

        // Delete schedule record
        String deleteQuery = "DELETE FROM schedule WHERE applicant_id = " + applicantId + ";";
        int count = MySQL.executeIUD(deleteQuery);

        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

    public ResultSet getAllSchedules() throws Exception {
        String query = "SELECT "
                + "s.id AS schedule_id, "
                + "s.date, "
                + "s.time, "
                + "a.id AS applicant_id, "
                + "a.fname, "
                + "a.lname, "
                + "a.nic, "
                + "a.email, "
                + "a.mobile, "
                + "a.position_id "
                + "FROM schedule s "
                + "INNER JOIN applicants a ON s.applicant_id = a.id;";

        ResultSet rs = MySQL.executeSearch(query);
        return rs;
    }

    public ResultSet getSchedule(String nic) throws Exception {
        // Validation
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }

        String query = "SELECT "
                + "s.id AS schedule_id, "
                + "s.date, "
                + "s.time, "
                + "a.id AS applicant_id, "
                + "a.fname, "
                + "a.lname, "
                + "a.nic, "
                + "a.email, "
                + "a.mobile, "
                + "a.position_id "
                + "FROM schedule s "
                + "INNER JOIN applicants a ON s.applicant_id = a.id "
                + "WHERE a.nic = '" + nic + "';";

        ResultSet rs = MySQL.executeSearch(query);
        return rs;
    }

    public String updateSchedule(String nic, String newDate, String newTime) throws Exception {
        // Input validations
        if (nic == null || nic.isBlank()) {
            throw new NullPointerException("NIC cannot be null or blank");
        }
        if (newDate == null || newDate.isBlank()) {
            throw new NullPointerException("New date cannot be null or blank");
        }
        if (newTime == null || newTime.isBlank()) {
            throw new NullPointerException("New time cannot be null or blank");
        }

        // Validate date format and logical correctness
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(newDate); // Throws exception if invalid
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format or value. Expected format: yyyy-MM-dd with valid date");
        }

        // Validate time format and logical correctness (HH:mm format only)
        LocalTime parsedTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            parsedTime = LocalTime.parse(newTime, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format or value. Expected format: HH:mm with valid time");
        }

        // Check if date is today or future
        LocalDate today = LocalDate.now();
        if (parsedDate.isBefore(today)) {
            throw new IllegalArgumentException("Schedule date must be today or a future date.");
        }

        // If date is today, check time is future
        if (parsedDate.isEqual(today)) {
            LocalTime now = LocalTime.now();
            if (parsedTime.isBefore(now)) {
                throw new IllegalArgumentException("Schedule time must be a future time for today.");
            }
        }

        // Check if applicant exists and get their ID
        String applicantQuery = "SELECT id FROM applicants WHERE nic = '" + nic + "';";
        ResultSet rsApp = MySQL.executeSearch(applicantQuery);
        if (!rsApp.next()) {
            return "applicant_not_found";
        }
        int applicantId = rsApp.getInt("id");

        // Check if schedule exists for this applicant
        String checkQuery = "SELECT * FROM schedule WHERE applicant_id = " + applicantId + ";";
        ResultSet rsCheck = MySQL.executeSearch(checkQuery);
        if (!rsCheck.next()) {
            return "schedule_not_found";
        }

        // Update schedule record
        String updateQuery = "UPDATE schedule SET date = '" + newDate + "', time = '" + newTime + "' WHERE applicant_id = " + applicantId + ";";

        int count = MySQL.executeIUD(updateQuery);
        if (count == 0) {
            return "Failed";
        } else {
            return "success";
        }
    }

}
