package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;

import java.util.List;

@FunctionalInterface
public interface PedidoListarHistoricoService {

    List<Pedido> listarHistorico(Long idCliente, String codigoDeAcessoCliente);

}
