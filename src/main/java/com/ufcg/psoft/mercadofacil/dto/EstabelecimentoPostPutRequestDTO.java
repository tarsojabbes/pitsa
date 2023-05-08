package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Codigo de acesso nao pode ser vazio")
    @Size(min=6, message = "Codigo de acesso deve ter tamanho minimo de 6 digitos")
    private String codigoDeAcesso;

    @JsonProperty("nome")
    @NotBlank(message = "Nome nao pode ser vazio")
    private String nome;
}
