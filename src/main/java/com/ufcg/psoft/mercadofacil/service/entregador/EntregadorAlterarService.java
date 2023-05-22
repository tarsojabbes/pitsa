package com.ufcg.psoft.mercadofacil.service.entregador;

import com.ufcg.psoft.mercadofacil.dto.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Entregador;

@FunctionalInterface
public interface EntregadorAlterarService {
    Entregador alterar(Long id, String codigoDeAcesso, EntregadorPostPutRequestDTO entregadorPostPutRequestDTO);
}
