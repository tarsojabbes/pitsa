package com.ufcg.psoft.mercadofacil.service.cliente;

@FunctionalInterface
public interface ClienteExcluirService {
    public void excluir(Long id, String codigoDeAcesso);
}
