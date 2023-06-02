package com.ufcg.psoft.mercadofacil.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Entity
@Table(name="clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("endereco")
    private String endereco;

    @JsonProperty("codigoDeAcesso")
    private String codigoDeAcesso;

    @JsonProperty("pedidos")
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;



    public void notificarPedidoEmRota(Entregador entregador){
        System.out.println(this.getNome() + ", seu pedido está em rota de entrega\n" +
                "--Informações do entregador--:\n" +
                "Nome: " + entregador.getNome() + "\n" +
                "Tipo de Veiculo: " + entregador.getTipoDoVeiculo()  + "\n" +
                "Cor do Veiculo: " + entregador.getCorDoVeiculo() + "\n" +
                "Placa do Veiculo: " + entregador.getPlacaDoVeiculo());
    }
}

