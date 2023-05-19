package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.ufcg.psoft.mercadofacil.exception.PizzaInvalidaException;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
public class PizzaGrandeDoisSabores extends Pizza{

    @JsonProperty
    private Sabor sabor2;

    public PizzaGrandeDoisSabores(Sabor sabor1, Sabor sabor2){

        super(sabor1, (sabor1.getPrecoGrande() + sabor2.getPrecoGrande()) / 2);
        
        if (sabor1.getEstabelecimento().equals(sabor2.getEstabelecimento())){
            this.sabor2 = sabor2;
        } else {
            throw new PizzaInvalidaException();
        }

    }
    
}
