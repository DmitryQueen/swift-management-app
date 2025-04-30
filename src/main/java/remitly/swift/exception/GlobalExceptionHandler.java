package remitly.swift.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import remitly.swift.dto.MessageResponseDto;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            CountryIsoCodeNotFoundException.class, HttpStatus.NOT_FOUND,
            SwiftCodeNotFoundException.class, HttpStatus.NOT_FOUND,
            DuplicateHeadquarterException.class, HttpStatus.CONFLICT,
            DuplicateSwiftCodeException.class, HttpStatus.CONFLICT
    );

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDto> handleRuntimeException(RuntimeException ex) {
        String errorMessage = Optional.ofNullable(ex.getMessage())
                .filter(msg -> !msg.isBlank())
                .orElse("Invalid argument provided");

        HttpStatus status = EXCEPTION_STATUS_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(ex.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(status).body(new MessageResponseDto(errorMessage));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            ValidationException.class
    })
    public ResponseEntity<MessageResponseDto> handleValidationExceptions(Exception ex) {
        String errorMessage = extractValidationMessage(ex).orElse("Validation error");
        return ResponseEntity.badRequest().body(new MessageResponseDto(errorMessage));
    }

    private Optional<String> extractValidationMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException e) {
            return e.getBindingResult().getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .findFirst();
        }

        if (ex instanceof ConstraintViolationException e) {
            return e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .filter(Objects::nonNull)
                    .findFirst();
        }

        return Optional.empty();
    }
}
