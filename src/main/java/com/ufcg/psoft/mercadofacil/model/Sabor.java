package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne()
    @JoinColumn(name = "id_estabelecimento", nullable = false)
    private Estabelecimento estabelecimento;

}