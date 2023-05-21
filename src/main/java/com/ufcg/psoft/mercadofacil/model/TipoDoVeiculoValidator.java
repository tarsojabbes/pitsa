package com.ufcg.psoft.mercadofacil.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@NotNull(message = "Tipo de veiculo nao pode ser nulo")
@Constraint(validatedBy = TipoDoVeiculoValidatorImpl.class)
public @interface TipoDoVeiculoValidator {
    String regexp();

    String message() default "Tipo de veiculo nao eh valido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
