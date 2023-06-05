package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    Cliente clienteA;

    // Partes necessárias à criação do pedido
    Pedido pedidoClienteA;

    Pedido pedidoClienteA2;

    Pedido pedidoClienteA3;
    List<Pizza> pizzas;
    Estabelecimento estabelecimento;

    @BeforeEach
    public void setUp() {
        clienteA = clienteRepository.save(Cliente.builder()
                .nome("Cliente A")
                .codigoDeAcesso("123456")
                .endereco("Rua A, 123")
                .build());

        this.montarPedidoClienteA();
    }

    private void montarPedidoClienteA() {
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


        pedidoClienteA = pedidoRepository.save(Pedido.builder()
                .cliente(clienteA)
                .pizzas(pizzas)
                .estabelecimento(estabelecimento)
                .endereco("abc")
                .build());
    }

    @AfterEach
    public void tearDown() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        saborRepository.deleteAll();
    }


        // Utilidades para testar semelhança entre pedidos
        public void comparaPedido(Pedido pedidoEsperado, Pedido pedidoResposta) {
            comparaPizzas(pedidoEsperado.getPizzas().toArray(new Pizza[0]), pedidoResposta.getPizzas().toArray(new Pizza[0]));
        }

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


    @Nested
    public class getPedidoClientesTests {


        @Test
        @Transactional
        @DisplayName("Quando um cliente quer visualizar um pedido específico")
        void testVisualizarPedidoCliente() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes/"+ clienteA.getId() + "/getPedido/" + pedidoClienteA.getId()
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoGetResponse = objectMapper.readValue(responseJsonString, Pedido.class);

            comparaPedido(pedidoClienteA, pedidoGetResponse);
        }

        @Test
        @Transactional
        @DisplayName("Quando um cliente quer visualizar um pedido específico com código inválido")
        void testVisualizarPedidoCodigoErrado() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes/"+ clienteA.getId() + "/getPedido/" + pedidoClienteA.getId()
                            + "?codigoDeAcessoCliente=" + "codigoDeOutroCliente")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido", error.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando um cliente quer visualizar um pedido específico de outro cliente")
        void testVisualizarPedidoDeOutroCliente() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes/"+ (clienteA.getId() + 3) + "/getPedido/" + pedidoClienteA.getId()
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente nao possui permissao para acessar o pedido de outro cliente",
                    error.getMessage()
            );
        }
    }

    @Nested
    public class getHistoricoPedidoClientesTests {

        @BeforeEach
        public void setUp() {
            this.montarPedidoClienteA2();
            this.montarPedidoClienteA3();
        }

        private void montarPedidoClienteA2() {
            // A montagem de pedido requer a criação de diversas outras entidades no bando de dados.
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .nome("Rival do Jipao")
                    .codigoDeAcesso("123")
                    .associacoes(new ArrayList<>())
                    .build());

            Sabor sabor = saborRepository.save(Sabor.builder()
                    .nomeSabor("Frango Com Catupiry")
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


            pedidoClienteA2 = pedidoRepository.save(Pedido.builder()
                    .cliente(clienteA)
                    .pizzas(pizzas)
                    .estabelecimento(estabelecimento)
                    .endereco("abc")
                    .build());
        }

        private void montarPedidoClienteA3() {
            // A montagem de pedido requer a criação de diversas outras entidades no bando de dados.
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .nome("Tio neutro")
                    .codigoDeAcesso("321")
                    .associacoes(new ArrayList<>())
                    .build());

            Sabor sabor = saborRepository.save(Sabor.builder()
                    .nomeSabor("Pizza Romeu-e-Julieta")
                    .tipoSabor("doce")
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


            pedidoClienteA3 = pedidoRepository.save(Pedido.builder()
                    .cliente(clienteA)
                    .pizzas(pizzas)
                    .estabelecimento(estabelecimento)
                    .endereco("abc")
                    .build());
        }

        @Test
        @Transactional
        @DisplayName("Quando um cliente quer visualizar seu histórico de pedidos")
        void testVisualizarHistoricoPedidos() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes/" +  clienteA.getId() +"/getHistoricoPedidos"
                                                         + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            TypeReference<List<Pedido>> tipoPedidoList = new TypeReference<List<Pedido>>() {};
            List<Pedido> pedidos = objectMapper.readValue(responseJsonString, tipoPedidoList);

            assertEquals(pedidos.size(),3);

            // Deve estar nessa ordem (mais pro menos recente).
            comparaPedido(pedidoClienteA3, pedidos.get(0));
            comparaPedido(pedidoClienteA2, pedidos.get(1));
            comparaPedido(pedidoClienteA, pedidos.get(2));
        }

        @Test
        @Transactional
        @DisplayName("Quando os pedidos tem estados diferentes")
        void testVisualizarHistoricoPedidosEmDiferentesEstados() throws Exception {
            Pedido pedido3 = pedidoRepository.findById(pedidoClienteA3.getId()).get();
            pedido3.setAcompanhamento(Acompanhamento.PEDIDO_ENTREGUE);
            pedidoRepository.save(pedido3);

            Pedido pedido2 = pedidoRepository.findById(pedidoClienteA2.getId()).get();
            pedido2.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
            pedidoRepository.save(pedido2);

            Pedido pedido = pedidoRepository.findById(pedidoClienteA.getId()).get();
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(get("/v1/clientes/" + clienteA.getId() + "/getHistoricoPedidos"
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            TypeReference<List<Pedido>> tipoPedidoList = new TypeReference<List<Pedido>>() {
            };
            List<Pedido> pedidos = objectMapper.readValue(responseJsonString, tipoPedidoList);



            assertEquals(pedidos.size(), 3);

            // A3 é mais recente que A2,  que é mais recente que A.
            // Mas A3 já foi entregue, logo é mostrado no final.
            comparaPedido(pedidoClienteA2, pedidos.get(0));
            comparaPedido(pedidoClienteA, pedidos.get(1));
            comparaPedido(pedidoClienteA3, pedidos.get(2));
        }

        @Test
        @Transactional
        @DisplayName("Quando os pedidos tem estados diferentes, e filtramos por estado")
        void testVisualizarHistoricoPedidosFiltrandoEstados() throws Exception {
            // Arrange
            Pedido pedido3 = pedidoRepository.findById(pedidoClienteA3.getId()).get();
            pedido3.setAcompanhamento(Acompanhamento.PEDIDO_ENTREGUE);
            pedidoRepository.save(pedido3);

            Pedido pedido2 = pedidoRepository.findById(pedidoClienteA2.getId()).get();
            pedido2.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
            pedidoRepository.save(pedido2);

            Pedido pedido = pedidoRepository.findById(pedidoClienteA.getId()).get();
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
            pedidoRepository.save(pedido);

            TypeReference<List<Pedido>> tipoPedidoList = new TypeReference<List<Pedido>>() { };

            // Act
            Acompanhamento filtro1 = Acompanhamento.PEDIDO_ENTREGUE;

            String responseJsonStringPedidoEntregue = driver.perform(get("/v1/clientes/" + clienteA.getId() + "/getHistoricoPedidos"
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso() +
                            "&filtroDeAcompanhamento=" + filtro1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<Pedido> pedidosEntregues = objectMapper.readValue(responseJsonStringPedidoEntregue, tipoPedidoList);

            Acompanhamento filtro2 = Acompanhamento.PEDIDO_EM_ROTA;

            String responseJsonStringPedidoEmRota = driver.perform(get("/v1/clientes/" + clienteA.getId() + "/getHistoricoPedidos"
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso() +
                            "&filtroDeAcompanhamento=" + filtro2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<Pedido> pedidosEmRota = objectMapper.readValue(responseJsonStringPedidoEmRota, tipoPedidoList);

            Acompanhamento filtro3 = Acompanhamento.PEDIDO_PRONTO;

            String responseJsonStringPedidoPronto = driver.perform(get("/v1/clientes/" + clienteA.getId() + "/getHistoricoPedidos"
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso() +
                            "&filtroDeAcompanhamento=" + filtro3)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<Pedido> pedidosProntos = objectMapper.readValue(responseJsonStringPedidoPronto, tipoPedidoList);

            Acompanhamento filtro4 = Acompanhamento.PEDIDO_RECEBIDO;

            String responseJsonStringPedidoRecebido = driver.perform(get("/v1/clientes/" + clienteA.getId() + "/getHistoricoPedidos"
                            + "?codigoDeAcessoCliente=" + clienteA.getCodigoDeAcesso() +
                            "&filtroDeAcompanhamento=" + filtro4)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<Pedido> pedidosRecebidos = objectMapper.readValue(responseJsonStringPedidoRecebido, tipoPedidoList);

            // Assert

            assertEquals(pedidosEntregues.size(), 1);
            assertEquals(pedidosEmRota.size(), 1);
            assertEquals(pedidosProntos.size(), 1);
            assertEquals(pedidosRecebidos.size(), 0);

            comparaPedido(pedidoClienteA3, pedidosEntregues.get(0));
            comparaPedido(pedidoClienteA2, pedidosEmRota.get(0));
            comparaPedido(pedidoClienteA, pedidosProntos.get(0));
        }
    }

}
