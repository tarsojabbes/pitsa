package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.dto.PedidoGetResponseDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para camada de controlador de Cliente")
public class ClienteHistoricoPedidoV1ControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Cliente cliente;

    // Partes necessárias à criação do pedido
    Pedido pedido;
    List<Pizza> pizzas;
    Estabelecimento estabelecimento;

    @BeforeEach
    public void setUp() {
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente A")
                .codigoDeAcesso("123456")
                .endereco("Rua A, 123")
                .build());

        this.montarPedido();
    }

    private void montarPedido() {
        // A montagem de pedido requer a criação de diversas outras entidades no bando de dados.
        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("Jipao")
                .codigoDeAcesso("123456")
                .associacoes(new ArrayList<>())
                .build());

        Sabor sabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Frango")
                .tipoSabor("salgado")
                .estabelecimento(estabelecimento)
                .precoGrande(59.90)
                .precoMedio(39.90)
                .build());

        Pizza pizza1 = Pizza.builder()
                .precoPizza(sabor.getPrecoGrande())
                .sabor1(sabor)
                .sabor2(null)
                .quantidade(1)
                .build();

        pizzas = new ArrayList<>();
        pizzas.add(pizza1);


        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .pizzas(pizzas)
                .estabelecimento(estabelecimento)
                .endereco("abc")
                .build());
    }

    @AfterEach
    public void tearDown() {
        clienteRepository.deleteAll();
    }

    @Nested
    public class getPedidoClientesTests {

        private void comparaPizzas(Pizza[] pizzasEsperadas, Pizza[] pizzasResposta) {

            assertEquals(pizzasEsperadas.length, pizzasResposta.length);

            for (int i = 0; i < pizzasEsperadas.length; i++) {
                comparaPizza(pizzasEsperadas[i], pizzasResposta[i]);
            }
        }

        private void comparaPizza(Pizza pizzaEsperada, Pizza pizzaResposta) {
            comparaSabor(pizzaEsperada.getSabor1(), pizzaResposta.getSabor1());
            comparaSabor(pizzaEsperada.getSabor2(), pizzaResposta.getSabor2());
            assertEquals(pizzaEsperada.getQuantidade(), pizzaResposta.getQuantidade());
        }

        private void comparaSabor(Sabor saborEsperado, Sabor saborResposta) {
            if (saborEsperado != null || saborResposta != null) {
                assertEquals(saborEsperado.getNomeSabor(), saborResposta.getNomeSabor());
                assertEquals(saborEsperado.getTipoSabor(), saborResposta.getTipoSabor());
                assertEquals(saborEsperado.getPrecoGrande(), saborResposta.getPrecoGrande());
                assertEquals(saborEsperado.getPrecoMedio(), saborResposta.getPrecoMedio());
                assertEquals(saborEsperado.getDisponivel(), saborResposta.getDisponivel());
            }
        }

        @Test
        @Transactional
        @DisplayName("Quando um cliente quer visualizar um pedido específico")
        void testVisualizarPedidoCliente() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes/getPedido/" + pedido.getId()
                            + "?codigoDeAcessoCliente=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoGetResponse = objectMapper.readValue(responseJsonString, Pedido.class);

            comparaPizzas(pedido.getPizzas().toArray(new Pizza[0]), pedidoGetResponse.getPizzas().toArray(new Pizza[0]));
        }

        @Test
        @Transactional
        @DisplayName("Quando um cliente quer visualizar um pedido específico de outro cliente")
        void testVisualizarPedidoDeOutroCliente() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes/getPedido/" + pedido.getId()
                            + "?codigoDeAcessoCliente=" + "codigoDeOutroCliente")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente nao possui permissao para acessar o pedido de outro cliente",
                            error.getMessage()
            );
        }
    }

}
