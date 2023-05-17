package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
public class PizzaGrandeDoisSabores extends Pizza{

    @JsonProperty
    private Sabor sabor2;

    protected PizzaGrandeDoisSabores(Sabor sabor1, Sabor sabor2){

        super(sabor1, (sabor1.getPrecoGrande() + sabor2.getPrecoGrande()) / 2);
        this.sabor2 = sabor2;

    }
    
}
