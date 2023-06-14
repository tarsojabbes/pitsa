package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.mercadofacil.exception.PedidoClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.PedidoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteBuscarPedidoPadraoService implements ClienteBuscarPedidoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Pedido buscaPedido(Long idCliente, Long idPedido, String codigoDeAcessoCliente) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(PedidoNaoExisteException::new);

        if (pedido.getCliente().getCodigoDeAcesso().equals(codigoDeAcessoCliente)
            && pedido.getCliente().getId().equals(idCliente)) {
            return pedido;
        } else if (pedido.getCliente().getId().equals(idCliente)){
            throw new CodigoDeAcessoInvalidoException();
        } else {
            throw new PedidoClienteNaoAutorizadoException();
        }
    }
}
