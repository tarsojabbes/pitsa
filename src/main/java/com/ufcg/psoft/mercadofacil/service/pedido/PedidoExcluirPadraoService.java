package com.ufcg.psoft.mercadofacil.service.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
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
    public void excluir(Long id, String codigoDeAcesso) {
        
        if (id == null || id <= 0L || codigoDeAcesso == null || codigoDeAcesso.isEmpty() || codigoDeAcesso.isBlank()){
            throw new IllegalArgumentException();
        }
        
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId()).orElseThrow(ClienteNaoExisteException::new);

        if (cliente.getCodigoDeAcesso().equals(codigoDeAcesso)){
            pedidoRepository.delete(pedido);
        } else {
            throw new ClienteNaoAutorizadoException();
        }
    }
    
}
