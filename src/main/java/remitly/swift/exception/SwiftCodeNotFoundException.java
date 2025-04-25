package remitly.swift.exception;

public class SwiftCodeNotFoundException extends RuntimeException {
    public SwiftCodeNotFoundException(String swiftCode) {
        super("Swift code not found: " + swiftCode);
    }
}
