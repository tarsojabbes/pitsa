package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.AcompanhamentoValidator;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @AcompanhamentoValidator(regexp = "PEDIDO_RECEBIDO|PEDIDO_EM_PREPARO|PEDIDO_PRONTO|PEDIDO_EM_ROTA|PEDIDO_ENTREGUE")
    @Enumerated(EnumType.STRING)
    private Acompanhamento acompanhamento;

    @JsonProperty
    @NotNull
    @Positive
    private Long idCliente;

    @JsonProperty
    @NotNull
    @Positive
    private Long idEstabelecimento;

    @JsonProperty
    @NotNull
    @Positive
    private Long idPedido;

}
