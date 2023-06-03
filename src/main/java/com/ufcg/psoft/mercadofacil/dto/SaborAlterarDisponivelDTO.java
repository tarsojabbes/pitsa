package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaborAlterarDisponivelDTO {
    @JsonProperty("disponivel")
    @NotNull(message = "Disponibilidade nao pode ser nula")
    private boolean disponivel;
}
