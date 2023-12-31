package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface PedidoIndicarProntoService {
    Pedido indicarPedidoPronto(Long id, String codigoDeAcesso);
}
