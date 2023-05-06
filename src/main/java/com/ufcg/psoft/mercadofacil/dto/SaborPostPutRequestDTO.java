package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class SaborPostPutRequestDTO {
    
    @JsonProperty("nomeSabor")
    @NotBlank(message = "Nome do sabor não pode estar em branco.")
    @NotNull(message = "Nome do sabor não pode ser null.")
    @NotEmpty(message = "Nome do sabor não pode ser vazio.")
    private String nomeSabor;

    @JsonProperty("tipoSabor")
    @NotBlank(message = "Tipo de sabor não pode estar em branco.")
    @NotNull(message = "Tipo de sabor não pode ser null.")
    @NotEmpty(message = "Tipo de sabor não pode ser vazio.")
    private String tipoSabor;

    @JsonProperty("precoMedio")
    @NotNull(message = "Preço não pode ser null.")
    @Positive(message = "Preço deve ser maior que zero.")
    private Double precoMedio;

    @JsonProperty("precoGrande")
    @NotNull(message = "Preço não pode ser null.")
    @Positive(message = "Preço deve ser maior que zero.")
    private Double precoGrande;
    
}
