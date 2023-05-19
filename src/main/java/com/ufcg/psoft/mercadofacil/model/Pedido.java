package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    private Long id;

    @JsonProperty("listaPizzas")
    private Map<Pizza,Integer> pizzasPedido;

    @JsonProperty("cliente")
    private Cliente cliente;

    @JsonProperty
    private String endereco;

    public Pedido(Cliente cliente, Map<Pizza,Integer> pizzas, String endereco){
        
        this.cliente = cliente;
        this.pizzasPedido = pizzas;
        this.id = cliente.getId();

        if (endereco == null || endereco.isEmpty() || endereco.isBlank()){
            endereco = cliente.getEndereco();
        } else {
            this.endereco = endereco;
        }
    }

    public Double getPrecoPedido(){

        return calculaPrecoPedido();

    }

    private Double calculaPrecoPedido(){

        Double total = 0.00;

        if (pizzasPedido.isEmpty()){
            return total;
        }

        List<Pizza> listagensInvalidas = new ArrayList<>();
        for (Map.Entry<Pizza,Integer> listagem : pizzasPedido.entrySet()){
            if (listagem.getKey().getClass().equals(Pizza.class) && listagem.getValue() > 0){
                total += listagem.getKey().getPrecoPizza() * listagem.getValue();
            } else {
                listagensInvalidas.add(listagem.getKey());
            }
        }

        if (!listagensInvalidas.isEmpty()){
            for (Pizza p: listagensInvalidas){
                pizzasPedido.remove(p);
            }
        }

        return total;
    }
    
}
