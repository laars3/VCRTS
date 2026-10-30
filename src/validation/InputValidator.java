package validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public final class InputValidator {
    private InputValidator() { }
    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
    // Commas would break the CSV files, so no saved text field may contain one.
    public static boolean noComma(String value) {
        return value != null && !value.contains(",");
    }
    // Account rules live here so the form and the store use the same ones.
    private static final int USERNAME_MIN = 3;
    private static final int USERNAME_MAX = 20;
    private static final int PASSWORD_MIN = 6;
    private static final int PASSWORD_MAX = 30;

    // Returns the first problem with a new account, or null if it is fine.
    public static String accountError(String username, String password) {
        if (username == null || username.length() < USERNAME_MIN
                || username.length() > USERNAME_MAX) {
            return "Username must be " + USERNAME_MIN + "-" + USERNAME_MAX
                + " characters.";
        }
        if (!username.matches("[A-Za-z0-9._-]+")) {
            return "Username can only use letters, numbers, . _ and -.";
        }
        if (password == null || password.length() < PASSWORD_MIN
                || password.length() > PASSWORD_MAX) {
            return "Password must be " + PASSWORD_MIN + "-" + PASSWORD_MAX
                + " characters.";
        }
        if (password.contains(" ") || password.contains(",")) {
            return "Password can't contain spaces or commas.";
        }
        return null;
    }
    public static boolean validAccount(String name, String password, String role) {
        return accountError(name == null ? null : name.trim(), password) == null
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
                && noComma(id) && noComma(make) && noComma(model)
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
        return hasText(id) && noComma(id) && validJobType(type) && positiveNumber(duration) && validDeadline(deadline);
    }
    public static boolean validStatus(String status) {
        return "Pending".equals(status) || "In Progress".equals(status) || "Completed".equals(status);
    }
    public static boolean validCompletion(double value) {
        return !Double.isNaN(value) && value >= 0 && value <= 100;
    }
}
