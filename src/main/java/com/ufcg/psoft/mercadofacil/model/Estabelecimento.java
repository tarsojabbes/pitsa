package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private List<Long> entregadoresDisponiveis = new ArrayList<Long>();

    @JsonProperty("pedidosEmEspera")
    private List<Long> pedidosEmEspera = new ArrayList<Long>();

    public void notificarPedidoEntregue(Long id) {
        System.out.println(this.getNome() + ", o pedido de número " + id
                + " foi entregue.\n");
    }

}
