package com.ufcg.psoft.mercadofacil.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TipoDoVeiculoValidatorImpl implements ConstraintValidator<TipoDoVeiculoValidator, Enum<?>> {
    private Pattern pattern;

    @Override
    public void initialize(TipoDoVeiculoValidator annotation) {
        try {
            pattern = Pattern.compile(annotation.regexp());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Tipo de veiculo nao eh valido", e);
        }
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Matcher m = pattern.matcher(value.name());
        return m.matches();
    }
}
