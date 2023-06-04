package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoGetResponseDTO;
import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface PedidoBuscarService {
     public Pedido buscaPedido(Long idCliente, Long idPedido, String codigoDeAcessoCliente);
}
