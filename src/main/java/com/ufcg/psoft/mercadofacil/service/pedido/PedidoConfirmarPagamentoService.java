package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface PedidoConfirmarPagamentoService {

    Pedido confirmar(Long id, String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO);
}
