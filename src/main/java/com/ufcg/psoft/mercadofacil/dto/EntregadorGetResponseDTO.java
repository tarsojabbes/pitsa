package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntregadorGetResponseDTO {

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("placaDoVeiculo")
    private String placaDoVeiculo;

    @JsonProperty("tipoDoVeiculo")
    @Enumerated(EnumType.STRING)
    private TipoDoVeiculo tipoDoVeiculo;

    @JsonProperty("corDoVeiculo")
    private String corDoVeiculo;

}
