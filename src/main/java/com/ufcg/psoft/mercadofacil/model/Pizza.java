package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("pedido")
    @ManyToOne()
    @JoinColumn(name="id_pedido")
    private Pedido pedido;

    @JsonProperty("sabor")
    @ManyToOne()
    @JoinColumn(name = "id_sabor")
    private Sabor sabor;

    @JsonProperty("precoPizza")
    private Double precoPizza;

    Pizza(Sabor sabor1, Double precoPizza){
        this.sabor = sabor1;
        this.precoPizza = precoPizza;
    }
    
}
