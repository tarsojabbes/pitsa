package com.ufcg.psoft.mercadofacil.service.pedido;

import java.util.List;

import com.ufcg.psoft.mercadofacil.exception.PedidoPrecoInvalidoException;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    PedidoCalcularPrecoService pedidoCalcularPrecoService;

    @Override
    public Pedido criar(String codigoDeAcesso, PedidoPostPutRequestDTO pedidoPostPutRequestDTO) {

        if (codigoDeAcesso == null || codigoDeAcesso.isEmpty() || codigoDeAcesso.isBlank() || pedidoPostPutRequestDTO == null) {
            throw new IllegalArgumentException();
        }

        Cliente cliente = clienteRepository.findById(pedidoPostPutRequestDTO.getIdCliente()).orElseThrow(ClienteNaoExisteException::new);

        if (cliente.getCodigoDeAcesso().equals(codigoDeAcesso)) {

            List<Pizza> inicioPedido = pedidoPostPutRequestDTO.getPizzas();

            String endereco = cliente.getEndereco();
            String enderecoAlternativo = pedidoPostPutRequestDTO.getEnderecoAlternativo();

            if (enderecoAlternativo != null) {
                endereco = enderecoAlternativo;
            }

            double preco = pedidoCalcularPrecoService.calcular(pedidoPostPutRequestDTO);

            Pedido pedido = Pedido.builder()
                    .endereco(endereco)
                    .pizzas(inicioPedido)
                    .cliente(cliente)
                    .meioDePagamento(pedidoPostPutRequestDTO.getMeioDePagamento())
                    .endereco(pedidoPostPutRequestDTO.getEnderecoAlternativo())
                    .build();

            System.out.println(pedido.getPrecoPedido());
            System.out.println("Calculado "+ preco);

            // Comparando preços, para evitar fraudes.
            if (preco != pedido.getPrecoPedido()) {
                throw new PedidoPrecoInvalidoException();
            }

            return pedidoRepository.save(pedido);
        } else {
            throw new ClienteNaoAutorizadoException();
        }

    }

}
