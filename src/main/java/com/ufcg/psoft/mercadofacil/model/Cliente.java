package com.ufcg.psoft.mercadofacil.model;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientes")
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

    public void notificarPedidoEmRota(Entregador entregador) {
        System.out.println(this.getNome() + ", seu pedido está em rota de entrega\n" +
                "--Informações do entregador--:\n" +
                "Nome: " + entregador.getNome() + "\n" +
                "Tipo de Veiculo: " + entregador.getTipoDoVeiculo() + "\n" +
                "Cor do Veiculo: " + entregador.getCorDoVeiculo() + "\n" +
                "Placa do Veiculo: " + entregador.getPlacaDoVeiculo());
    }
    
    public void notificarPedidoPronto(){

        LocalTime horario = LocalTime.now();
        String saudacao;

        if (horario.getHour() >= 6 && horario.getHour() < 12){
            saudacao = "Bom dia, ";
        } else if (horario.getHour() >= 12 && horario.getHour() < 18){
            saudacao = "Boa tarde, ";
        } else {
            saudacao = "Boa noite, ";
        }

        System.out.println(saudacao + this.nome + "! Seu pedido está pronto.");

    }

    public void notificarIndisponibilidadeEntregador() {
        System.out.println(this.nome + ", o seu pedido está pronto, mas infelizmente não há entregadores disponíveis. " +
                            "Pedimos perdão pelo inconveniente, seu pedido será entregue assim que tivermos um entregador disponível!");
    }

}
