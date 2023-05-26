package com.ufcg.psoft.mercadofacil.service.pedido;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

@Service
public class PedidoCalcularPrecoPadraoService implements PedidoCalcularPrecoService{
    
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Override
    public double calcular(PedidoPostPutRequestDTO pedido) {
        List<Pizza> pizzas = pedido.getPizzas();

        double total = 0;
        for (Pizza pizza : pizzas) {
            Sabor sabor1 = saborRepository.findById(pizza.getSabor1().getId())
                    .orElseThrow(SaborNaoExisteException::new);

            if (pizza.getSabor2() != null) {
                Sabor sabor2 = saborRepository.findById(pizza.getSabor2().getId())
                                            .orElseThrow(SaborNaoExisteException::new);

                total += ((sabor1.getPrecoGrande() / 2) + (sabor2.getPrecoGrande() / 2))*pizza.getQuantidade();
            } else if (pizza.getEhGrande()) {
                total += (sabor1.getPrecoGrande())*pizza.getQuantidade();
            }
            else {
                // Caso seja pizza média.
                total += sabor1.getPrecoMedio()*pizza.getQuantidade();
            }
        }

        return switch (pedido.getMeioDePagamento()) {
            case PIX -> total * 0.95;
            case DEBITO -> total * 0.975;
            case CREDITO -> total;
        };
    }
}
