package com.ufcg.psoft.mercadofacil.model;

import jakarta.persistence.*;

@Entity
public class PizzaMedia extends Pizza{

    protected PizzaMedia(Sabor sabor1){

        super(sabor1, sabor1.getPrecoMedio());

    }
    
}
