package com.oriontek.clients.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = IdentificationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIdentification {

    String message() default
            "Identificación inválida: debe ser una cédula (11 dígitos) o RNC (9 dígitos) válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
