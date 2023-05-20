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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("listaPizzas")
    @OneToMany(mappedBy = "pedido")
    private List<Pizza> pizzasPedido;

    @JsonProperty("cliente")
    @ManyToOne()
    @JoinColumn(name="id_cliente", nullable = false)
    private Cliente cliente;

    @JsonProperty
    private String endereco;

    public Pedido(Cliente cliente, List<Pizza> pizzas, String endereco){
        
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

    public void setEndereco(String novoEndereco){
        if (novoEndereco == null || novoEndereco.isEmpty() || novoEndereco.isBlank()){
            this.endereco = this.cliente.getEndereco();
        } else {
            this.endereco  = novoEndereco;
        }
    }

    private Double calculaPrecoPedido(){

        Double total = 0.00;

        if (pizzasPedido.isEmpty()){
            return total;
        }

        for (Pizza listagem : pizzasPedido){
            total += listagem.getPrecoPizza();
        }

        return total;
    }
    
}
