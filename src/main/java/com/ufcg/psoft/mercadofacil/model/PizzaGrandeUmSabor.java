package com.ufcg.psoft.mercadofacil.model;

import jakarta.persistence.*;

@Entity
public class PizzaGrandeUmSabor extends Pizza{

    public PizzaGrandeUmSabor(Sabor sabor1){

        super(sabor1, sabor1.getPrecoGrande());

    }
    
}
