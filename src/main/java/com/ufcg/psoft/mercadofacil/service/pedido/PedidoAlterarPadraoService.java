package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoAlterarPadraoService implements PedidoAlterarService{
    
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public Pedido alterar(Long id, String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);
        Cliente cliente = clienteRepository.findById(pedidoPostPutRequestDTO.getIdCLiente()).orElseThrow(ClienteNaoExisteException::new);

        if (codigoDeAcesso.equals(cliente.getCodigoDeAcesso())){
            pedido.setCliente(cliente);
            pedido.setPizzasPedido(pedidoPostPutRequestDTO.getPizzas());

            if (!pedidoPostPutRequestDTO.getEnderecoAlternativo().equals("") ||
                    pedidoPostPutRequestDTO.getEnderecoAlternativo() != null) {
                pedido.setEndereco(pedidoPostPutRequestDTO.getEnderecoAlternativo());
            }

            pedido.setMeioDePagamento(pedidoPostPutRequestDTO.getMeioDePagamento());
            return pedidoRepository.save(pedido);
        } else {
            throw new ClienteNaoAutorizadoException();
        }
    }
}
