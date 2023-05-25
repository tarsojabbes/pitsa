package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private List<Pizza> pizzas;

    @JsonProperty("cliente")
    @ManyToOne()
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @JsonProperty("precoPedido")
    private Double precoPedido;

    @JsonProperty("meioDePagamento")
    private MeioDePagamento meioDePagamento;

    @JsonProperty("endereco")
    private String endereco;

    public Pedido(Cliente cliente, List<Pizza> pizzas, String endereco) {

        this.cliente = cliente;
        this.pizzas = pizzas;
        this.precoPedido = calculaPrecoPedido();
        if (endereco == null || endereco.isEmpty() || endereco.isBlank()) {
            this.endereco = cliente.getEndereco();
        } else {
            this.endereco = endereco;
        }
        this.meioDePagamento = null;
    }

    public void setPrecoPedido(Double preco) {
        this.precoPedido = calculaPrecoPedido();
    }

    public Double getPrecoPedido() {
        return calculaPrecoPedido();
    }

    private double calculaPrecoComDesconto(Double preco, MeioDePagamento meioDePagamento) {
        switch (meioDePagamento) {
            case PIX:
                return preco * 0.95;
            case DEBITO:
                return preco * 0.975;
            case CREDITO:
                return preco;
        }
        return preco;
    }

    private Double calculaPrecoPedido() {
        Double preco = 0.00;

        for (Pizza p : pizzas) {
            preco += p.getPrecoPizza() * p.getQuantidade();
        }

        if (meioDePagamento != null) {
            return calculaPrecoComDesconto(preco, meioDePagamento);
        } else {
            return preco;
        }
    }

    public Long getId() {
        return this.id;
    }

    public String getEndereco() {
        return this.endereco;
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public void setPizzas(List<Pizza> pizzas) {
        this.pizzas = pizzas;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MeioDePagamento getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(MeioDePagamento meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public void setEndereco(String endereco) {
        if (endereco == null || endereco.isEmpty() || endereco.isBlank()) {
            this.endereco = this.cliente.getEndereco();
        } else {
            this.endereco = endereco;
        }
    }

}
