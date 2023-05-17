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
public class Pedido {

    @JsonProperty("listaPizzas")
    private List<Pizza> pizzasPedido;

    @JsonProperty("precoPedido")
    private Double precoPedido;

    @JsonProperty("cliente")
    private Cliente cliente;

    public Double getPrecoPedido(){

        return calculaPrecoPedido();

    }

    private Double calculaPrecoPedido(){

        Double total = 0.00;

        for (Pizza pizza : pizzasPedido){
            total += pizza.getPrecoPizza();
        }

        precoPedido = total;
        return total;
    }
    
}
