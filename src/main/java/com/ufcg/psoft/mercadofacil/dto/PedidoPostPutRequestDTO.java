package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ufcg.psoft.mercadofacil.model.Pizza;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PedidoPostPutRequestDTO {

    @JsonProperty("pizzas")
    @NotNull(message = "A listagem de pedidos nao pode ser null.")
    @NotEmpty(message = "A listagem de pedidos nao pode estar vazia.")
    private Map<Pizza,Integer> pizzas;

    @JsonProperty("codigoDeAcesso.")
    @NotBlank(message = "Codigo de acesso nao pode estar me branco.")
    @NotNull(message = "Codigo de acesso nao pode ser null.")
    @NotEmpty(message = "Codigo de acesso nao pode ser vazio.")
    @Size(min=6, message = "Codigo de acesso deve ter tamanho minimo de 6 digitos")
    private String codigoDeAcesso;

    @JsonProperty("idCliente")
    @NotNull(message = "A id do cliente nao deve ser nula.")
    @Positive(message = "A id do cliente deve ser maior que zero.")
    private Long idCLiente;

}
