package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("sabor1")
    protected Sabor sabor1;

    @JsonProperty("precoPizza")
    private Double precoPizza;

    public Pizza(Sabor sabor1, Double precoPizza){
        this.sabor1 = sabor1;
        this.precoPizza = precoPizza;
    }
    
}
