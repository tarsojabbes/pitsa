package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.dto.SaborAlterarDisponivelDTO;
import com.ufcg.psoft.mercadofacil.model.Sabor;

@FunctionalInterface
public interface SaborAlterarDisponivelService {
    public Sabor alterar(Long id, String codigoDeAcesso, SaborAlterarDisponivelDTO saborAlterarDisponivelDTO);
}
