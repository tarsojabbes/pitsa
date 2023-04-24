package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.ProdutoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Produto;

@FunctionalInterface
public interface ProdutoAlterarService {
    Produto alterar(Long id, ProdutoPostPutRequestDTO produtoPostPutRequestDTO);
}
