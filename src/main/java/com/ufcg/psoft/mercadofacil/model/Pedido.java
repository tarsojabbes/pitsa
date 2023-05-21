package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("pizzas")
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Pizza> pizzasPedido;

    @JsonProperty("cliente")
    @ManyToOne()
    @JoinColumn(name="id_cliente", nullable = false)
    private Cliente cliente;

    @JsonProperty("precoPedido")
    private Double precoPedido;

    @JsonProperty("meioDePagamento")
    private String meioDePagamento;

    @JsonProperty("endereco")
    private String endereco;

    public Pedido(Cliente cliente, List<Pizza> pizzas, String endereco){

        this.cliente = cliente;
        this.pizzasPedido = pizzas;
        this.precoPedido = calculaPrecoPedido();
        if (endereco == null || endereco.isEmpty() || endereco.isBlank()){
            this.endereco = cliente.getEndereco();
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

        for (Pizza p:pizzasPedido){
            preco += p.getPrecoPizza() * p.getQuantidade();
        }

        return preco;
    }

    public Long getId() {
        return this.id;
    }

    public String getEndereco() {
        return this.endereco;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Pizza> getPizzasPedido() {
        return pizzasPedido;
    }

    public void setPizzasPedido(List<Pizza> pizzasPedido) {
        this.pizzasPedido = pizzasPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setPrecoPedido(Double precoPedido) {
        this.precoPedido = precoPedido;
    }

    public String getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(String meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
