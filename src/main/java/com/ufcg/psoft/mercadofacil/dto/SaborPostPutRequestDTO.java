package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class SaborPostPutRequestDTO {
    
    @JsonProperty("nomeSabor")
    @NotBlank(message = "Nome do sabor nao pode estar em branco.")
    @NotNull(message = "Nome do sabor nao pode ser null.")
    @NotEmpty(message = "Nome do sabor nao pode ser vazio.")
    private String nomeSabor;

    @JsonProperty("idEstabelecimento")
    @NotNull(message = "Id do estabelecimento nao pode ser nulo")
    private Long idEstabelecimento;

    @JsonProperty("tipoSabor")
    @NotBlank(message = "Tipo de sabor nao pode estar em branco.")
    @NotNull(message = "Tipo de sabor nao pode ser null.")
    @NotEmpty(message = "Tipo de sabor nao pode ser vazio.")
    private String tipoSabor;

    @JsonProperty("precoMedio")
    @NotNull(message = "Preco nao pode ser null.")
    @Positive(message = "Preco deve ser maior que zero.")
    private Double precoMedio;

    @JsonProperty("precoGrande")
    @NotNull(message = "Preco nao pode ser null.")
    @Positive(message = "Preco deve ser maior que zero.")
    private Double precoGrande;

    @JsonProperty("estabelecimento")
    private Estabelecimento estabelecimento;

}
