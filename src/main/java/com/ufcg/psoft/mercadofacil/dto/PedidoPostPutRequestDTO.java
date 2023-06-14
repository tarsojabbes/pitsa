package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.mercadofacil.model.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PedidoPostPutRequestDTO {

    @JsonProperty("pizzas")
    @NotNull(message = "A listagem de pedidos nao pode ser null.")
    @NotEmpty(message = "A listagem de pedidos nao pode estar vazia.")
    private List<Pizza> pizzas;

    @JsonProperty("codigoDeAcesso")
    @NotBlank(message = "Codigo de acesso nao pode estar em branco.")
    @NotNull(message = "Codigo de acesso nao pode ser null.")
    @NotEmpty(message = "Codigo de acesso nao pode ser vazio.")
    @Size(min = 6, message = "Codigo de acesso deve ter tamanho minimo de 6 digitos")
    private String codigoDeAcesso;

    @JsonProperty("meioDePagamento")
    @MeioDePagamentoValidator(regexp = "PIX|CREDITO|DEBITO")
    @Enumerated(EnumType.STRING)
    private MeioDePagamento meioDePagamento;

    @JsonProperty("acompanhamento")
    @AcompanhamentoValidator(regexp = "PEDIDO_RECEBIDO|PEDIDO_EM_PREPARO|PEDIDO_PRONTO|PEDIDO_EM_ROTA|PEDIDO_ENTREGUE")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Acompanhamento acompanhamento = Acompanhamento.PEDIDO_RECEBIDO;

    @JsonProperty("idCliente")
    @NotNull(message = "A id do cliente nao deve ser nula.")
    @Positive(message = "A id do cliente deve ser maior que zero.")
    private Long idCliente;

    @JsonProperty("enderecoAlternativo")
    private String enderecoAlternativo;

    @JsonProperty("idEstabelecimento")
    @NotNull(message = "O id do estabelecimento nao pode ser nulo.")
    private Long idEstabelecimento;

}
