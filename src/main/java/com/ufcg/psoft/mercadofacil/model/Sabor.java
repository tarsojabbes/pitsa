package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Sabores de pizza")

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

}