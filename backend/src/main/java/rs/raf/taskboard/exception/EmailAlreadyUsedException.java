package rs.raf.taskboard.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email " + email + " is already in use");
    }
}