package com.ufcg.psoft.mercadofacil.service.estabelecimento;

import com.ufcg.psoft.mercadofacil.dto.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;

@FunctionalInterface
public interface EstabelecimentoCriarService {
    public Estabelecimento criar(EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO);
}
