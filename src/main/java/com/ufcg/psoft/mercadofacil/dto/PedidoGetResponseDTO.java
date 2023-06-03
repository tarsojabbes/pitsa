package com.ufcg.psoft.mercadofacil.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.mercadofacil.model.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoGetResponseDTO {


    @JsonProperty("pizzas")
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Pizza> pizzas;

    @JsonProperty("entregador")
    @ManyToOne()
    @JoinColumn(name = "id_entregador")
    private Entregador entregador;

    @JsonProperty("estabelecimento")
    @ManyToOne()
    @JoinColumn(name = "id_estabelecimento", nullable = true)
    private Estabelecimento estabelecimento;

    @JsonProperty("precoPedido")
    private Double precoPedido;

    @JsonProperty("meioDePagamento")
    private MeioDePagamento meioDePagamento;

    @JsonProperty("endereco")
    private String endereco;

    @JsonProperty("acompanhamento")
    @Enumerated(EnumType.STRING)
    private Acompanhamento acompanhamento;
}
