package com.ufcg.psoft.mercadofacil.service.pedido;

import javax.management.InvalidAttributeValueException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ufcg.psoft.mercadofacil.dto.AcompanhamentoPedidoDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;

public class AcompanhamentoPedidoPadraoService implements AcompanhamentoPedidoService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;
    
    @Override
    public Acompanhamento alteraAcompanhamento(Long id,
                                     String codigoDeAcesso,
                                     AcompanhamentoPedidoDTO acompanhamentoPedidoDTO,
                                     int andamento) throws InvalidAttributeValueException {

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        Cliente cliente = clienteRepository.findById(acompanhamentoPedidoDTO.getIdCliente()).orElseThrow(ClienteNaoExisteException::new);
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(acompanhamentoPedidoDTO.getIdEstabelecimento()).orElseThrow(EstabelecimentoNaoExisteException::new);

        if (!codigoDeAcesso.equals(cliente.getCodigoDeAcesso())){
            throw new ClienteNaoAutorizadoException();
        }

        if (!codigoDeAcesso.equals(estabelecimento.getCodigoDeAcesso())){
            throw new EstabelecimentoNaoAutorizadoException();
        }

        if (andamento < 0 || andamento > 4){
            throw new IllegalArgumentException();
        }

        switch (andamento){
            case(0):
                if (!acompanhamentoPedidoDTO.isStatusPedido() && pedido.getAcompanhamento().isPedidoPronto()){
                    throw new MudancaDeStatusInvalidaException();
                }else {
                    pedido.modificaAcompanhamento(acompanhamentoPedidoDTO.isStatusPedido(), andamento);
                }
                break;

            case(1):
                pedido.modificaAcompanhamento(acompanhamentoPedidoDTO.isStatusPedido(), andamento);
                break;

            case(2):
                pedido.modificaAcompanhamento(acompanhamentoPedidoDTO.isStatusPedido(), andamento);
                break;

            case(3):
                pedido.modificaAcompanhamento(acompanhamentoPedidoDTO.isStatusPedido(), andamento);
                break;

            case(4):
                pedido.modificaAcompanhamento(acompanhamentoPedidoDTO.isStatusPedido(), andamento);
                break;

            default:
                throw new IllegalArgumentException();
        }

        return pedido.getAcompanhamento();
    }
}
