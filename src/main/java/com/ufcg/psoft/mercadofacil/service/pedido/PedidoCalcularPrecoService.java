package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface PedidoCalcularPrecoService {
    
    public double calcular(PedidoPostPutRequestDTO pedido);
}
