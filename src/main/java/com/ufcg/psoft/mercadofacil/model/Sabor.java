package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sabor")
public class Sabor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nomeSabor")
    private String nomeSabor;

    @JsonProperty("tipoSabor")
    private String tipoSabor;

    @JsonProperty("precoMedio")
    private Double precoMedio;

    @JsonProperty("precoGrande")
    private Double precoGrande;

    @JsonProperty("disponivel")
    @Builder.Default
    private Boolean disponivel = true;

    @JsonProperty("interessados")
    @Builder.Default
    private List<Long> interessados = new ArrayList<Long>();

    @ManyToOne()
    @JoinColumn(name = "estabelecimento")
    private Estabelecimento estabelecimento;

}
