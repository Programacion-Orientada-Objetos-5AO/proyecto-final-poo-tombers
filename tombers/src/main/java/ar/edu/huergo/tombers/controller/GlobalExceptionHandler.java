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

    /**
     * Maneja excepciones de validación de argumentos de método.
     * @param ex La excepción MethodArgumentNotValidException lanzada.
     * @return Un ProblemDetail con detalles de los errores de validación.
     */
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

    /**
     * Maneja excepciones de violación de restricciones.
     * @param ex La excepción ConstraintViolationException lanzada.
     * @return Un ProblemDetail con detalles de la violación.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Violación de constraint");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/constraint-violation"));
        log.warn("Violación de constraint: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja excepciones cuando una entidad no es encontrada.
     * @param ex La excepción EntityNotFoundException lanzada.
     * @return Un ProblemDetail indicando que el recurso no fue encontrado.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/not-found"));
        log.info("Recurso no encontrado: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja excepciones cuando un recurso no es encontrado.
     * @param ex La excepción NoResourceFoundException lanzada.
     * @return Un ProblemDetail indicando que el recurso no fue encontrado.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(NoResourceFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Recurso no encontrado");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/not-found"));
        log.info("Recurso no encontrado: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja excepciones cuando el método HTTP no está soportado.
     * @param ex La excepción HttpRequestMethodNotSupportedException lanzada.
     * @return Un ProblemDetail indicando que el método no está permitido.
     */
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

    /**
     * Maneja excepciones de argumentos inválidos.
     * @param ex La excepción IllegalArgumentException lanzada.
     * @return Un ProblemDetail indicando argumento inválido.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Argumento inválido");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("https://http.dev/problems/invalid-argument"));
        return problem;
    }

    /**
     * Maneja excepciones genéricas no controladas.
     * @param ex La excepción Exception lanzada.
     * @return Un ProblemDetail indicando error interno del servidor.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Error interno del servidor");
        problem.setDetail("Ha ocurrido un error inesperado");
        problem.setType(URI.create("https://http.dev/problems/internal-error"));
        log.error("Error no controlado", ex);
        // agregar que indique la excepcion que salto
        problem.setProperty("exception", ex.getClass().getName());

        return problem;
    }

    /**
     * Maneja excepciones de credenciales inválidas.
     * @param ex La excepción BadCredentialsException lanzada.
     * @return Un ProblemDetail indicando credenciales inválidas.
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Credenciales inválidas");
        problem.setDetail("El nombre de usuario o la contraseña son incorrectos");
        problem.setType(URI.create("https://http.dev/problems/unauthorized"));
        log.warn("Intento de autenticación fallido: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja excepciones de servicio de autenticación interno.
     * @param ex La excepción InternalAuthenticationServiceException lanzada.
     * @return Un ProblemDetail con detalles del error de autenticación.
     */
    @ExceptionHandler(org.springframework.security.authentication.InternalAuthenticationServiceException.class)
    public ProblemDetail handleInternalAuthenticationServiceException(org.springframework.security.authentication.InternalAuthenticationServiceException ex) {
        // Verificar si la causa es EntityNotFoundException (usuario no encontrado)
        if (ex.getCause() instanceof jakarta.persistence.EntityNotFoundException) {
            ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
            problem.setTitle("Recurso no encontrado");
            problem.setDetail(ex.getCause().getMessage());
            problem.setType(URI.create("https://http.dev/problems/not-found"));
            log.info("Usuario no encontrado durante autenticación: {}", ex.getCause().getMessage());
            return problem;
        }

        // Para otros errores de autenticación interna
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Error de autenticación");
        problem.setDetail("Error interno en el proceso de autenticación");
        problem.setType(URI.create("https://http.dev/problems/unauthorized"));
        log.error("Error interno de autenticación", ex);
        return problem;
    }

    /**
     * Maneja excepciones de JWT expirado.
     * @param ex La excepción ExpiredJwtException lanzada.
     * @return Un ProblemDetail indicando token expirado.
     */
    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(io.jsonwebtoken.ExpiredJwtException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Token expirado");
        problem.setDetail("El token JWT ha expirado. Por favor, inicie sesión nuevamente.");
        problem.setType(URI.create("https://http.dev/problems/token-expired"));
        log.warn("Token JWT expirado: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja excepciones de JWT malformado.
     * @param ex La excepción MalformedJwtException lanzada.
     * @return Un ProblemDetail indicando token inválido.
     */
    @ExceptionHandler(io.jsonwebtoken.MalformedJwtException.class)
    public ProblemDetail handleMalformedJwtException(io.jsonwebtoken.MalformedJwtException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Token inválido");
        problem.setDetail("El token JWT está malformado o corrupto.");
        problem.setType(URI.create("https://http.dev/problems/invalid-token"));
        log.warn("Token JWT malformado: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja excepciones de firma JWT inválida.
     * @param ex La excepción SignatureException lanzada.
     * @return Un ProblemDetail indicando token inválido.
     */
    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    public ProblemDetail handleSignatureException(io.jsonwebtoken.security.SignatureException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Token inválido");
        problem.setDetail("La firma del token JWT es inválida.");
        problem.setType(URI.create("https://http.dev/problems/invalid-token"));
        log.warn("Firma JWT inválida: {}", ex.getMessage());
        return problem;
    }

    /**
     * Maneja otras excepciones relacionadas con JWT.
     * @param ex La excepción JwtException lanzada.
     * @return Un ProblemDetail indicando error de token.
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ProblemDetail handleJwtException(io.jsonwebtoken.JwtException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Error de token");
        problem.setDetail("Error relacionado con el token JWT.");
        problem.setType(URI.create("https://http.dev/problems/token-error"));
        log.warn("Error JWT: {}", ex.getMessage());
        return problem;
    }
}
