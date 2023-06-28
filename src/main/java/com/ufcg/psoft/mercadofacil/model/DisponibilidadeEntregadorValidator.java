package com.ufcg.psoft.mercadofacil.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DisponibilidadeEntregadorValidatorImpl.class)
public @interface DisponibilidadeEntregadorValidator {
    String regexp();

    String message() default "Disponibilidade Entregador nao eh valido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
