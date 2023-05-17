package com.ufcg.psoft.mercadofacil.service.pedido;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;

public class PedidoListarPadraoService implements PedidoListarService{

    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public List<Pedido> listar(Long id, String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {
        
        if (id == null || id <= 0L || codigoDeAcesso == null || codigoDeAcesso.isEmpty() || codigoDeAcesso.isBlank()){
            throw new IllegalArgumentException();
        }

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        Cliente cliente = clienteRepository.findById(pedidoPostPutRequestDTO.getIdCLiente()).orElseThrow(ClienteNaoExisteException::new);

        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedido);

        if (cliente.getCodigoDeAcesso().equals(codigoDeAcesso)){
            return pedidos;
        } else {
            throw new ClienteNaoAutorizadoException();
        }
    }
}
