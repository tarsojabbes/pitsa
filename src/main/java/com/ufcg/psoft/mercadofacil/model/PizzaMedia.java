package com.ufcg.psoft.mercadofacil.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Data
public class PizzaMedia extends Pizza{

    public PizzaMedia(Sabor sabor1){

        super(sabor1, sabor1.getPrecoMedio());

    }
    
}
