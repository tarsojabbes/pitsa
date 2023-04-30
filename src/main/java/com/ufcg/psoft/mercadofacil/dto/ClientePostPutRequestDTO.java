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
@AllArgsConstructor
@NoArgsConstructor
public class ClientePostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome do cliente nao pode ser vazio")
    private String nome;

    @JsonProperty("endereco")
    @NotBlank(message = "Endereco do cliente nao pode ser vazio")
    private String endereco;

    @JsonProperty("codigoDeAcesso")
    @NotBlank(message = "Codigo de acesso do cliente nao pode ser vazio")
    @Size(min=6, message = "Codigo de acesso deve ter tamanho minimo de 6 digitos")
    private String codigoDeAcesso;
}
