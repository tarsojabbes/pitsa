package com.ufcg.psoft.mercadofacil.service.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;

@Service
public class PedidoExcluirPadraoService implements PedidoExcluirService{

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public void excluir(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        pedidoRepository.delete(pedido);
    }
    
}
