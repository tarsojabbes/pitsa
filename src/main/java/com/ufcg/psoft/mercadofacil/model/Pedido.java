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
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("listaPizzas")
    @OneToMany(mappedBy = "pedido")
    private List<Pizza> pizzasPedido;

    @JsonProperty("cliente")
    @ManyToOne()
    @JoinColumn(name="id_cliente", nullable = false)
    private Cliente cliente;

    @JsonProperty("precoPedido")
    private Double precoPedido;

    @JsonProperty
    private String meioDePagamento;

    @JsonProperty
    private String endereco;

    public Pedido(Cliente cliente, List<Pizza> pizzas, String endereco){

        this.cliente = cliente;
        this.pizzasPedido = pizzas;
        this.id = cliente.getId();
        this.precoPedido = calculaPrecoPedido();
        if (endereco == null || endereco.isEmpty() || endereco.isBlank()){
            endereco = cliente.getEndereco();
        } else {
            this.endereco = endereco;
        }
    }

    public void setPrecoPedido(){

        this.precoPedido = calculaPrecoPedido();
    }

    public Double getPrecoPedido(){

        return calculaPrecoPedido();

    }

    private Double calculaPrecoPedido(){

        Double preco = 0.00;

        if (pizzasPedido.isEmpty()){
            return preco;
        }

        for (Pizza p:pizzasPedido){
            preco += p.getPrecoPizza() * p.getQuantidade();
        }

        return preco;
    }

}
