package com.ufcg.psoft.mercadofacil.service.entregador;

@FunctionalInterface
public interface EntregadorExcluirService {
    void excluir(Long id, String codigoDeAcesso);
}
