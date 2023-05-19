package com.ufcg.psoft.mercadofacil.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
public class PizzaGrandeUmSabor extends Pizza{

    public PizzaGrandeUmSabor(Sabor sabor1){

        super(sabor1, sabor1.getPrecoGrande());

    }
    
}
