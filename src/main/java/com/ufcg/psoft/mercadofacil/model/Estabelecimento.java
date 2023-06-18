package com.ufcg.psoft.mercadofacil.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "estabelecimentos")
public class Estabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("codigoDeAcesso")
    private String codigoDeAcesso;

    @JsonProperty("nome")
    private String nome;

    @OneToMany(mappedBy = "estabelecimento")
    private List<Associacao> associacoes;

    @OneToMany(mappedBy = "estabelecimento")
    private List<Pedido> pedidos;

    @JsonProperty("entregadoresDisponiveis")
    @Builder.Default
    private List<Long> entregadoresDisponiveis = new ArrayList<>();

    @JsonProperty("pedidosEmEspera")
    @Builder.Default
    private List<Long> pedidosEmEspera = new ArrayList<>();

    public void notificarPedidoEntregue(Long id) {
        System.out.println(this.getNome() + ", o pedido de número " + id
                + " foi entregue.\n");
    }

}
