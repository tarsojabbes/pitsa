package com.ufcg.psoft.mercadofacil.service.sabor;

import java.util.List;

@FunctionalInterface
public interface SaborNotificarService {
    public List<String> notificar(Long id);
}
