package com.ufcg.psoft.mercadofacil.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DisponibilidadeEntregadorValidatorImpl implements ConstraintValidator<DisponibilidadeEntregadorValidator, Enum<?>> {
    private Pattern pattern;

    @Override
    public void initialize(DisponibilidadeEntregadorValidator annotation) {
        try {
            pattern = Pattern.compile(annotation.regexp());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Disponibilidade entregador nao eh valido", e);
        }
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Matcher m = pattern.matcher(value.name());
        return m.matches();
    }

}
