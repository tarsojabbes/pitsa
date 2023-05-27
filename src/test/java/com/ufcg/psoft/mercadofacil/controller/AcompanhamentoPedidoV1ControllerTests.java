package com.ufcg.psoft.mercadofacil.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.AcompanhamentoPedidoDTO;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de acompanhamento de Pedidos")
public class AcompanhamentoPedidoV1ControllerTests {

    @Autowired
    MockMvc driver;
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    AcompanhamentoPedidoV1Controller acompanhamentoPedidoV1Controller;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Pedido pedido;

    Estabelecimento estabelecimento;

    Cliente cliente;

    List<Pizza> pizzas;

    @BeforeEach
    void setup(){

        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("Jipao")
                .codigoDeAcesso("123456")
                .associacoes(new ArrayList<>())
                .build());

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Joao")
                .endereco("Rua 1")
                .codigoDeAcesso("123456")
                .build());

        Sabor sabor = saborRepository.save(Sabor.builder()
        .nomeSabor("Calabresa")
        .tipoSabor("salgado")
        .precoGrande(60.00)
        .precoMedio(50.00)
        .estabelecimento(estabelecimento)
        .build());
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);
        Pizza duasCalabresasGrandes = new Pizza(sabores, false, true, 2);
        List<Pizza> novoPedido = new ArrayList<>();
        novoPedido.add(duasCalabresasGrandes);
        pizzas = novoPedido;

        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .pizzas(pizzas)
                .build());

    }

    @AfterEach
    void tearDown(){

        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        saborRepository.deleteAll();

    }

    private Estabelecimento novoEstabelecimento(){

        Estabelecimento novoEstabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("Joab 2")
                .codigoDeAcesso("123456")
                .associacoes(new ArrayList<>())
                .build());

        return novoEstabelecimento;

    }

    private Cliente novoCliente(){

        Cliente novoCliente = clienteRepository.save(Cliente.builder()
            .nome("Maria")
            .endereco("Rua 2")
            .codigoDeAcesso("123456")
            .build());

        return novoCliente;

    }

    private Pedido novoPedido(){

        Sabor sabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Pomodoro")
                .tipoSabor("salgado")
                .precoGrande(60.00)
                .precoMedio(50.00)
                .estabelecimento(novoEstabelecimento())
                .build());
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);
        Pizza pomodoro = new Pizza(sabores, false, true, 1);
        List<Pizza> listaPizzas = new ArrayList<>();
        listaPizzas.add(pomodoro);

        Pedido novoPedido = pedidoRepository.save(Pedido.builder()
                .cliente(novoCliente())
                .pizzas(pizzas)
                .build());

        return novoPedido;
        
    }

    @Nested
    class AcompanhamentoPedidoPutTests{

        @Test
        @Transactional
        @DisplayName("")
        void test(){

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();
            //TODO
        }

    }

    @Nested
    class AcompanhamentoPedidoGetTests{

        @Test
        @Transactional
        @DisplayName("")
        void testBuscaPedidoValido(){

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();
        }

        @Test
        @Transactional
        @DisplayName("")
        void testBuscaPedidoInvalido(){

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();
        }

    }

    @Nested
    class AcompanhamentoPedidoDeleteTests{

        @Test
        @Transactional
        @DisplayName("")
        void testDeletaPedidoNaoPronto(){

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();
        }

        @Test
        @Transactional
        @DisplayName("")
        void testDeletaPedidoPronto(){

            //TODO

        }

        @Test
        @Transactional
        @DisplayName("")
        void testDeletaPedidoErrado(){

            //TODO

        }

    }
    
}
