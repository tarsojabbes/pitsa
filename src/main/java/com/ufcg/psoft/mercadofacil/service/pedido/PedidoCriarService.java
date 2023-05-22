package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;

@FunctionalInterface
public interface PedidoCriarService {
    
    public Pedido criar(String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO);
}
