package ar.edu.huergo.tombers.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;


/**
* Manejador global de excepciones de la API.
*
* Beneficios:
* - Centraliza el manejo de errores: evita try/catch repetidos en controladores.
* - Respuestas consistentes: devuelve Problem Details (RFC 7807) con estructura uniforme.
* - Observabilidad: registra logs claros por tipo de error para facilitar el troubleshooting.
*
* Qué es:
* - {@link RestControllerAdvice}: intercepta excepciones lanzadas por controladores REST
*   y transforma los errores en respuestas HTTP estandarizadas.
* - Usa {@link ProblemDetail} para describir el problema con status, title, detail y propiedades extra.
* - Usa SLF4J vía Lombok (@Slf4j) para emitir logs con el nivel adecuado.
*/
@Slf4j // Lombok: inyecta un logger SLF4J llamado 'log'
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validación fallida");
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        problem.setDetail("Se encontraron errores de validación en el payload");
        problem.setProperty("errores", errors);
        problem.setType(URI.create("https://http.dev/problems/validation-error"));
        log.warn("Solicitud inválida: errores de validación {}", errors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Violación de constraint");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/constraint-violation"));
        log.warn("Violación de constraint: {}", ex.getMessage());
        return problem;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/not-found"));
        log.info("Recurso no encontrado: {}", ex.getMessage());
        return problem;
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(NoResourceFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/not-found"));
        log.info("Recurso no encontrado: {}", ex.getMessage());
        return problem;
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        problem.setTitle("Método no permitido");
        problem.setDetail(String.format("El método '%s' no está soportado para esta solicitud. Los métodos soportados son %s",
                ex.getMethod(), ex.getSupportedHttpMethods()));
        problem.setType(URI.create("https://http.dev/problems/method-not-allowed"));
        // Log de advertencia con el detalle de la solicitud
        log.warn("Método HTTP no soportado: {}", ex.getMessage());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Argumento inválido");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/invalid-argument"));
        return problem;
    }
    
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Error interno del servidor");
        problem.setDetail("Ha ocurrido un error inesperado");
        problem.setType(URI.create("https://http.dev/problems/internal-error"));
        log.error("Error no controlado", ex);
        return problem;
    }
}