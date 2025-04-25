package remitly.swift.exception;

public class DuplicateSwiftCodeException extends RuntimeException {
    public DuplicateSwiftCodeException(String code) {
        super("Swift code already exists: " + code);
    }
}
