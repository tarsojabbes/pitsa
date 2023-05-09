package com.ufcg.psoft.mercadofacil.service.cliente;

@FunctionalInterface
public interface ClienteExcluirService {
    void excluir(Long id, String codigoDeAcesso);
}
