package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @JsonProperty("listaPizzas")
    private Map<Pizza,Integer> pizzasPedido;

    @JsonProperty("cliente")
    private Cliente cliente;

    public Pedido(Cliente cliente, Map<Pizza,Integer> pizzas){
        this.cliente = cliente;
        this.pizzasPedido = pizzas;
    }

    public Double getPrecoPedido(){

        return calculaPrecoPedido();

    }

    private Double calculaPrecoPedido(){

        Double total = 0.00;

        for (Map.Entry<Pizza,Integer> listagem : pizzasPedido.entrySet()){
            total += listagem.getKey().getPrecoPizza() * listagem.getValue();
        }

        return total;
    }
    
}
