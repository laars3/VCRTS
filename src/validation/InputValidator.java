package validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public final class InputValidator {
    private InputValidator() { }
    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
    public static boolean validAccount(String name, String password, String role) {
        return hasText(name) && hasText(password)
                && ("VEHICLE_OWNER".equals(role) || "JOB_OWNER".equals(role));
    }
    public static boolean positiveNumber(String value) {
        if (!hasText(value)) return false;
        try {
            double number = Double.parseDouble(value.trim());
            return !Double.isNaN(number) && !Double.isInfinite(number) && number > 0;
        } catch (NumberFormatException e) { return false; }
    }
    public static boolean validYear(String value) {
        if (!hasText(value)) return false;
        try {
            int year = Integer.parseInt(value.trim());
            return year >= 1886 && year <= LocalDate.now().getYear() + 1;
        } catch (NumberFormatException e) { return false; }
    }
    public static boolean validVehicle(String id, String make, String model,
            String year, String power, String residency) {
        return hasText(id) && hasText(make) && hasText(model) && validYear(year)
                && positiveNumber(power) && positiveNumber(residency);
    }
    public static boolean validJobType(String type) {
        return "Language Translation".equals(type) || "Image Generation".equals(type)
                || "Data Analysis".equals(type) || "Video Rendering".equals(type);
    }
    // Uses the same date format as the job table.
    public static boolean validDeadline(String value) {
        if (value == null || !value.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) return false;
        try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern("MM/dd/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT));
            return true;
        } catch (java.time.DateTimeException e) { return false; }
    }
    public static boolean validJob(String id, String type, String duration, String deadline) {
        return hasText(id) && validJobType(type) && positiveNumber(duration) && validDeadline(deadline);
    }
    public static boolean validStatus(String status) {
        return "Pending".equals(status) || "In Progress".equals(status) || "Completed".equals(status);
    }
    public static boolean validCompletion(double value) {
        return !Double.isNaN(value) && value >= 0 && value <= 100;
    }
}
