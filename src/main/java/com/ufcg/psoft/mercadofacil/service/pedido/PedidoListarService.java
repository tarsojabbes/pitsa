package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.model.Pedido;

import java.util.List;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;

@FunctionalInterface
public interface PedidoListarService {
    
    public List<Pedido> listar(Long id, String codigoDeAcesso);
}
