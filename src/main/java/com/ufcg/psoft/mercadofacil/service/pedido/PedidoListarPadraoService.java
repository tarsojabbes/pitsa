package com.ufcg.psoft.mercadofacil.service.pedido;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.springframework.stereotype.Service;

@Service
public class PedidoListarPadraoService implements PedidoListarService{

    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public List<Pedido> listar(Long id, String codigoDeAcesso) {
        if (id == null) {
            return pedidoRepository.findAll();
        }

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        if (!codigoDeAcesso.equals(pedido.getCliente().getCodigoDeAcesso())) {
            throw new ClienteNaoAutorizadoException();
        }

        return pedidoRepository.findAllByCliente(pedido.getCliente());
    }
}
