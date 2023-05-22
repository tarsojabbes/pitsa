package com.ufcg.psoft.mercadofacil.service.pedido;

@FunctionalInterface
public interface PedidoExcluirService {

    void excluir(Long id, String codigoDeAcesso);
    
}
