package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;

@FunctionalInterface
public interface PedidoCalcularPrecoService {
    
    public double calcular(PedidoPostPutRequestDTO pedido);
}
