package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;

import java.util.List;

@FunctionalInterface
public interface PedidoListarService {

    List<Pedido> listar(Long id, String codigoDeAcesso);

}
