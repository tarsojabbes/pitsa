package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

public enum Acompanhamento {

    PEDIDO_RECEBIDO,
    PEDIDO_EM_PREPARO,
    PEDIDO_PRONTO,
    PEDIDO_EM_ROTA,
    PEDIDO_ENTREGUE
    
}
