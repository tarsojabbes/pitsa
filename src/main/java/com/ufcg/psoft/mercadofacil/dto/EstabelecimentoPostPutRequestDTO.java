package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstabelecimentoPostPutRequestDTO {

    @JsonProperty("codigoDeAcesso")
    @NotBlank(message = "Código de acesso não pode ser vazio")
    private String codigoDeAcesso;

    @JsonProperty("nome")
    @NotBlank(message = "Nome não pode ser vazio")
    private String nome;
}
