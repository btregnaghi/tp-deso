package ar.edu.utn.frsf4.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> manejarElementoNoEncontrado(
            NoSuchElementException e) {

        return ResponseEntity.status(404)
                .body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> manejarEstadoInvalido(
            IllegalStateException e) {

        return ResponseEntity.status(409)
                .body(Map.of("mensaje", e.getMessage()));
    }
}