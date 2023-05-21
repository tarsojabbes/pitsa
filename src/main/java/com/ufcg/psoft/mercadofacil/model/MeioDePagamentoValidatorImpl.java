package com.ufcg.psoft.mercadofacil.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MeioDePagamentoValidatorImpl implements ConstraintValidator<MeioDePagamentoValidator, Enum<?>> {
    private Pattern pattern;

    @Override
    public void initialize(MeioDePagamentoValidator annotation) {
        try {
            pattern = Pattern.compile(annotation.regexp());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Meio de pagamento nao eh valido", e);
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
