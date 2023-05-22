package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;

@FunctionalInterface
public interface PedidoAlterarService {
    
    public Pedido alterar(Long id, String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO);
}
