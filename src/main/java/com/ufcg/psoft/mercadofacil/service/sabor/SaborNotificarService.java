package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.model.Sabor;

import java.util.List;

@FunctionalInterface
public interface SaborNotificarService {
    public List<String> notificar(Long id);
}
