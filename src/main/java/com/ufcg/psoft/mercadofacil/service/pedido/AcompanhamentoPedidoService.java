package com.ufcg.psoft.mercadofacil.service.pedido;

import javax.management.InvalidAttributeValueException;

import com.ufcg.psoft.mercadofacil.dto.AcompanhamentoPedidoDTO;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;

@FunctionalInterface
public interface AcompanhamentoPedidoService {

    public Acompanhamento alteraAcompanhamento(Long id,
                                     String codigoDeAcesso,
                                     AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                                     int andamento) throws InvalidAttributeValueException;
    
}
