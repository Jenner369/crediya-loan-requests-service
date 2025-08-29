package co.com.crediya.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND", "Usuario no encontrado");
    }
}