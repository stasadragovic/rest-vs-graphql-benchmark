package rs.raf.taskboard.exception;

// Ista poruka za nepostojeći email i pogrešnu lozinku, da se ne otkrije koji emailovi postoje
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}