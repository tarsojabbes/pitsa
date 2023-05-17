package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
public class ProdutoPostPutRequestDTO {
    @JsonProperty("nome")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    @JsonProperty("preco")
    @Positive(message = "Preço deve ser maior ou igual a zero")
    private Double preco;
    @JsonProperty("codigoDeBarras")
    @Size(min = 13, max = 13, message = "A propriedade deve ter exatamente 13 caracteres")
    private String codigoDeBarras;
    @JsonProperty("fabricante")
    @NotBlank(message = "Fabricante é obrigatório")
    private String fabricante;
}
