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

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDto> handle(RuntimeException e) {
        String errorMessage = e.getMessage() != null && !e.getMessage().isBlank()
                ? e.getMessage()
                : "Invalid argument provided";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponseDto(errorMessage));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            ValidationException.class})
    public ResponseEntity<MessageResponseDto> handleValidationExceptions(Exception ex) {
        String errorMessage = "Validation error";

        if (ex instanceof MethodArgumentNotValidException e) {
            List<ObjectError> errors = e.getBindingResult().getAllErrors();
            errorMessage = errors.stream()
                    .map(ObjectError::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(errorMessage);
        } else if (ex instanceof ConstraintViolationException e) {
            errorMessage = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(errorMessage);
        }

        return ResponseEntity.badRequest().body(new MessageResponseDto(errorMessage));
    }

}
