package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoInformationHasBeenFoundException.class)
    public ResponseEntity<String> handleNoInformationHasBeenFoundException(NoInformationHasBeenFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Неизвестная ошибка: " + ex.getMessage());
    }

    @ExceptionHandler(MedicalClientException.class)
    public ResponseEntity<String> handleMedicalClientException(MedicalClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("Ошибка с получением информации из medical service: " + ex.getMessage());
    }

    @ExceptionHandler(PersonClientException.class)
    public ResponseEntity<String> handlePersonClientException(PersonClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("Ошибка с получением информации из person service: " + ex.getMessage());
    }

    @ExceptionHandler(QRClientException.class)
    public ResponseEntity<String> handleQRClientException(QRClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("Ошибка с получением информации из qr service: " + ex.getMessage());
    }
}