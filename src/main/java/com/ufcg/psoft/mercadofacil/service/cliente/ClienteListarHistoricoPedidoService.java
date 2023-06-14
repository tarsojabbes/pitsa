package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Pedido;

import java.util.List;

@FunctionalInterface
public interface ClienteListarHistoricoPedidoService {

    List<Pedido> listarHistorico(Long idCliente, String codigoDeAcessoCliente, Acompanhamento filtroDeAcompanhamento);

}
