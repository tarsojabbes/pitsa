package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AcompanhamentoPedidoDTO {
    
    @JsonProperty
    @NotNull(message = "O status do pedido nao pode ser null.")
    private boolean statusPedido;

    @JsonProperty
    @NotNull
    @Positive
    private Long idCliente;

    @JsonProperty
    @NotNull
    @Positive
    private Long idEstabelecimento;

}
