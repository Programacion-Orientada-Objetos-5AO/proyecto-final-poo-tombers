package ar.edu.huergo.tombers.tombers.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidarPassword.SecurePasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidarPassword {

    String message() default "La contraseña debe tener al menos 16 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales (@$!%*?&)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public static class SecurePasswordValidator implements ConstraintValidator<ValidarPassword, String> {

        @Override
        public boolean isValid(String password, ConstraintValidatorContext context) {
            if (password == null || password.isEmpty()) {
                return false;
            }

            // Al menos 16 caracteres
            if (password.length() < 16) {
                return false;
            }

            // Al menos una minúscula
            if (!password.matches(".*[a-z].*")) {
                return false;
            }

            // Al menos una mayúscula
            if (!password.matches(".*[A-Z].*")) {
                return false;
            }

            // Al menos un número
            if (!password.matches(".*\\d.*")) {
                return false;
            }

            // Al menos un carácter especial
            if (!password.matches(".*[@$!%*?&].*")) {
                return false;
            }

            return true;
        }
    }
}