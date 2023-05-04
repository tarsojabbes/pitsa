package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private List<Associacao> associacoes = new ArrayList<>();
}
