package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoCriarPadraoService implements PedidoCriarService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public Pedido criar(String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {

        if (codigoDeAcesso == null || codigoDeAcesso.isEmpty() || codigoDeAcesso.isBlank() || pedidoPostPutRequestDTO == null) {
            throw new IllegalArgumentException();
        }

        Cliente cliente = clienteRepository.findById(pedidoPostPutRequestDTO.getIdCliente()).orElseThrow(ClienteNaoExisteException::new);

        if (cliente.getCodigoDeAcesso().equals(codigoDeAcesso)) {

            List<Pizza> inicioPedido = pedidoPostPutRequestDTO.getPizzas();

            Pedido pedido = Pedido.builder()
                    .pizzas(inicioPedido)
                    .cliente(cliente)
                    .endereco(pedidoPostPutRequestDTO.getEnderecoAlternativo())
                    .build();

            // Colocando o endereço do pedido
            // Se o endereço de entrega não for informado,
            // o pedido deverá ser entregue no endereço principal do(a) cliente que fez o pedido.

            String enderecoAlternativo = pedidoPostPutRequestDTO.getEnderecoAlternativo();

            if (enderecoAlternativo != null) {
                pedido.setEndereco(enderecoAlternativo);
            } else {
                pedido.setEndereco(cliente.getEndereco());
            }

            return pedidoRepository.save(pedido);
        } else {
            throw new ClienteNaoAutorizadoException();
        }

    }

}
