package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoConfirmarPadraoService implements PedidoConfirmarService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public Pedido confirmar(Long id, String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {

        if (id == null || id <= 0L || codigoDeAcesso == null || codigoDeAcesso.isEmpty() || codigoDeAcesso.isBlank() || pedidoPostPutRequestDTO == null) {
            throw new IllegalArgumentException();
        }

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId()).orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new ClienteNaoAutorizadoException();
        } else {
            modelMapper.map(pedidoPostPutRequestDTO, pedido);
            return pedidoRepository.save(pedido);
        }
    }
}
