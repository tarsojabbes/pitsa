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

    @JsonProperty("id")
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

}
