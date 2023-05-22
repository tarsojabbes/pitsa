package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.hibernate.annotations.DialectOverride;
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

    private double calculaPrecoComDesconto(Double preco, MeioDePagamento meioDePagamento) {
        switch (meioDePagamento) {
            case PIX:
                return preco * 0.95;
            case DEBITO:
                return preco * 0.975;
            case CREDITO:
                return preco;
        }
        return preco;
    }
    @Override
    public double calcular(PedidoPostPutRequestDTO pedido) {
        List<Pizza> pizzas = pedido.getPizzas();
        double preco = 0.00;

        for (Pizza p : pizzas) {
            preco += p.getPrecoPizza() * p.getQuantidade();
        }

        if (pedido.getMeioDePagamento() != null) {
            return calculaPrecoComDesconto(preco, pedido.getMeioDePagamento());
        } else {
            return preco;
        }
    }
}
