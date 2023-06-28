package com.ufcg.psoft.mercadofacil.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "entregadores")
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("placaDoVeiculo")
    private String placaDoVeiculo;

    @JsonProperty("tipoDoVeiculo")
    @Enumerated(EnumType.STRING)
    private TipoDoVeiculo tipoDoVeiculo;

    @JsonProperty("corDoVeiculo")
    private String corDoVeiculo;

    @JsonProperty("codigoDeAcesso")
    private String codigoDeAcesso;

    @OneToMany(mappedBy = "entregador")
    private List<Associacao> associacoes;

    @OneToMany(mappedBy = "entregador")
    private List<Pedido> pedidos;
}
