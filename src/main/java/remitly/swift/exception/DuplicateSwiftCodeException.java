package remitly.swift.exception;

public class DuplicateSwiftCodeException extends RuntimeException {
    public DuplicateSwiftCodeException(String swiftCode) {
        super("Swift code already exists: " + swiftCode);
    }
}
