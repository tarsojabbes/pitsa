package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.model.Pedido;

@FunctionalInterface
public interface ClienteBuscarPedidoService {
     public Pedido buscaPedido(Long idCliente, Long idPedido, String codigoDeAcessoCliente);
}
