package com.ufcg.psoft.mercadofacil.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("pedido")
    @ManyToOne
    @JoinColumn(name="id_pedido")
    @JsonBackReference
    private Pedido pedido;

    @JsonProperty("ehGrande")
    private Boolean ehGrande;

    @JsonProperty("sabor1")
    @ManyToOne()
    @JoinColumn(name = "id_sabor1")
    private Sabor sabor1;

    @JsonProperty("sabor2")
    @ManyToOne()
    @JoinColumn(name = "id_sabor2")
    private Sabor sabor2;

    @JsonProperty
    private Integer quantidade;

    @JsonProperty("precoPizza")
    private Double precoPizza;

    public Pizza(List<Sabor> sabores, boolean eGrandeDoisSabores, boolean ehGrande, Integer quantidade){
        this.sabor1 = sabores.get(0);
        if (eGrandeDoisSabores){
            this.sabor2 = sabores.get(1);
        } else {
            this.sabor2 = null;
        }
        this.ehGrande = ehGrande;
        this.precoPizza = calculaPrecoPizza(eGrandeDoisSabores);
        this.quantidade = quantidade;
    }

    private Double calculaPrecoPizza(Boolean ehGrandeDoisSabores) {
        Double preco = 0.00;
        if (ehGrandeDoisSabores) {
            preco = (sabor1.getPrecoGrande() + sabor2.getPrecoGrande())/2;
        } else {
            if (ehGrande) {
                preco = sabor1.getPrecoGrande();
            } else {
                preco = sabor1.getPrecoMedio();
            }
        }
        return preco;
    }

    @Override
	public int hashCode() {
		return Objects.hash(id, pedido, precoPizza, quantidade, sabor1, sabor2);
	}
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pizza other = (Pizza) obj;
		return Objects.equals(id, other.id) && Objects.equals(pedido, other.pedido)
				&& Objects.equals(precoPizza, other.precoPizza) && Objects.equals(quantidade, other.quantidade)
				&& Objects.equals(sabor1, other.sabor1) && Objects.equals(sabor2, other.sabor2);
	}
    
}
