package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

                total += (sabor1.getPrecoGrande() / 2) + (sabor2.getPrecoGrande() / 2);
            } else {
                // Caso seja pizza média.
                total += sabor1.getPrecoGrande();
            }
        }

        return total;

    }
}
