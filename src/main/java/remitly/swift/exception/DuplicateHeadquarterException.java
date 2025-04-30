package remitly.swift.exception;

public class DuplicateHeadquarterException extends RuntimeException {
    public DuplicateHeadquarterException(String swiftCode) {
        super("Duplicate headquarter code: " + swiftCode);
    }
}
