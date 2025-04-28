package remitly.swift.exception;

public class CountryIsoCodeNotFoundException extends RuntimeException {
    public CountryIsoCodeNotFoundException(String isoCode) {
        super("There is no requested ISO2 code in our database: " + isoCode);
    }
}
