package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo;
import com.ufcg.psoft.mercadofacil.model.TipoDoVeiculoValidator;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class EntregadorPostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome do entregador nao pode ser vazio")
    private String nome;

    @JsonProperty("placaDoVeiculo")
    @NotBlank(message = "Placa do veiculo nao pode ser vazio")
    private String placaDoVeiculo;

    @JsonProperty("tipoDoVeiculo")
    @TipoDoVeiculoValidator(regexp = "MOTO|CARRO")
    @Enumerated(EnumType.STRING)
    private TipoDoVeiculo tipoDoVeiculo;

    @JsonProperty("corDoVeiculo")
    @NotBlank(message = "Cor do veiculo nao pode ser vazio")
    private String corDoVeiculo;

    @JsonProperty("codigoDeAcesso")
    @NotBlank(message = "Codigo de acesso do entregador nao pode ser vazio")
    @Size(min = 6, message = "Codigo de acesso deve ter tamanho minimo de 6 digitos")
    private String codigoDeAcesso;

}
