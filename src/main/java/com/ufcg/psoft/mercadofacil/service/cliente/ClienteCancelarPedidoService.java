package com.ufcg.psoft.mercadofacil.service.cliente;

@FunctionalInterface
public interface ClienteCancelarPedidoService {
    void cancelarPedido(Long idPedido, String codigoDeAcesso);
}
