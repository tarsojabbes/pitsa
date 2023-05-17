package com.ufcg.psoft.mercadofacil.service.pedido;

@FunctionalInterface
public interface PedidoExcluirService {

    public void excluir(Long id, String codigoDeAcesso);
    
}
