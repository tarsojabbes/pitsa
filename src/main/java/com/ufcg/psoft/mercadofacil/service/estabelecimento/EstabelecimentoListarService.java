package com.ufcg.psoft.mercadofacil.service.estabelecimento;

import com.ufcg.psoft.mercadofacil.model.Estabelecimento;

import java.util.List;

@FunctionalInterface
public interface EstabelecimentoListarService {
    public List<Estabelecimento> listar(Long id);
}
