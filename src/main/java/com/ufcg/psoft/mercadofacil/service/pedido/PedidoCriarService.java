package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface PedidoCriarService {

    Pedido criar(String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO);
}
