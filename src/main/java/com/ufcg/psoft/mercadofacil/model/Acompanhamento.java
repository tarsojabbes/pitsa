package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Acompanhamento {

    @JsonProperty("pedidoConfirmado")
    private boolean pedidoConfirmado;

    @JsonProperty("pedidoEmPreparacao")
    private boolean pedidoEmPreparacao;

    @JsonProperty("pedidoPronto")
    private boolean pedidoPronto;

    @JsonProperty("pedidoACaminho")
    private boolean pedidoACaminho;

    @JsonProperty("pedidoEntregue")
    private boolean pedidoEntregue;
    
}
