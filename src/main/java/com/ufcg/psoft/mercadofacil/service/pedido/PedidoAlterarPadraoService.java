package com.ufcg.psoft.mercadofacil.service.pedido;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;

@Service
public class PedidoAlterarPadraoService implements PedidoAlterarService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public Pedido alterar(Long id, String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        Cliente cliente = clienteRepository.findById(pedidoPostPutRequestDTO.getIdCliente()).orElseThrow(ClienteNaoExisteException::new);

        if (codigoDeAcesso.equals(cliente.getCodigoDeAcesso())) {
            modelMapper.map(pedidoPostPutRequestDTO, pedido);

            return pedidoRepository.save(pedido);

        } else {
            throw new ClienteNaoAutorizadoException();
        }
    }
}
