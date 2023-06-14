package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface ClienteConfirmarEntregaService {

    public Pedido confirmarPedidoEntregue(Long id);
}
