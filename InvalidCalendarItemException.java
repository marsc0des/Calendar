package util;

public class InvalidCalendarItemException extends Exception {
    private String message;

    public InvalidCalendarItemException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}