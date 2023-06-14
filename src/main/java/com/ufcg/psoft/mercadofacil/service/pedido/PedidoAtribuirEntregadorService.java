package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface PedidoAtribuirEntregadorService {
    Pedido atribuirEntregador(Long idPedido, Long idEntregador);
}
