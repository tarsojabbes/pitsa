package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.PedidoGetResponseDTO;
import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.service.associacao.AssociacaoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoBuscarPadraoService implements PedidoBuscarService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Pedido buscaPedido(Long idPedido, String codigoDeAcessoCliente) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(PedidoNaoExisteException::new);

        if (pedido.getCliente().getCodigoDeAcesso().equals(codigoDeAcessoCliente)) {
            return pedido;
        } else {
            throw new PedidoClienteNaoAutorizadoException();
        }
    }
}
