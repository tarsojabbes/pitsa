package com.ufcg.psoft.mercadofacil.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteBuscarPedidoService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteListarHistoricoPedidoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes para camada de controlador de Cliente")
public class ClienteHistoricoServiceTests {

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

    @Autowired
    ClienteBuscarPedidoService pedidoBuscarService;

    @Autowired
    ClienteListarHistoricoPedidoService pedidoListarHistoricoService;

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
    private void comparaPedido(Pedido pedidoEsperado, Pedido pedidoResposta) {
        comparaPizzas(pedidoEsperado.getPizzas().toArray(new Pizza[0]), pedidoResposta.getPizzas().toArray(new Pizza[0]));
        assertEquals(pedidoEsperado.getEntregador(), pedidoResposta.getEntregador());
        assertEquals(pedidoEsperado.getEndereco(), pedidoResposta.getEndereco());
        assertEquals(pedidoEsperado.getHorarioDoPedido(), pedidoResposta.getHorarioDoPedido());
        assertEquals(pedidoEsperado.getPrecoPedido(), pedidoResposta.getPrecoPedido());
        assertEquals(pedidoEsperado.getCliente(), pedidoResposta.getCliente());
        assertEquals(pedidoEsperado.getAcompanhamento(), pedidoResposta.getAcompanhamento());
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
        @DisplayName("Quando um cliente quer visualizar um pedido específico")
        void testVisualizarPedidoCliente() throws Exception {
            Pedido pedidoGetResponse = pedidoBuscarService.buscaPedido(
                    clienteA.getId(),
                    pedidoClienteA.getId(),
                    clienteA.getCodigoDeAcesso()
            );

            comparaPedido(pedidoClienteA, pedidoGetResponse);
        }

        @Test
        @DisplayName("Quando um cliente quer visualizar um pedido específico com código inválido")
        void testVisualizarPedidoCodigoErrado() throws Exception {
            assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
                    pedidoBuscarService.buscaPedido(
                    clienteA.getId(),
                    pedidoClienteA.getId(),
                    "CódigoInválido"
                    );
                }
            );
        }

        @Test
        @DisplayName("Quando um cliente quer visualizar um pedido específico que não existe")
        void testVisualizarPedidoInexistente() throws Exception {
            assertThrows(PedidoNaoExisteException.class, () -> {
                        pedidoBuscarService.buscaPedido(
                                clienteA.getId(),
                                9999L,
                                clienteA.getCodigoDeAcesso()
                        );
                    }
            );
        }

        @Test
        @DisplayName("Quando um cliente quer visualizar um pedido específico de outro cliente")
        void testVisualizarPedidoDeOutroCliente() throws Exception {
            assertThrows(PedidoClienteNaoAutorizadoException.class, () -> {
                        pedidoBuscarService.buscaPedido(
                                9999L,
                                pedidoClienteA.getId(),
                                clienteA.getCodigoDeAcesso()
                        );
                    }
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
        @DisplayName("Quando um cliente quer visualizar seu histórico de pedidos")
        void testVisualizarHistoricoPedidos() throws Exception {

            List<Pedido> pedidos = pedidoListarHistoricoService.listarHistorico(clienteA.getId(),
                                                                                clienteA.getCodigoDeAcesso(),
                                                            null
                                                                                );

            assertEquals(pedidos.size(),3);

            // Deve estar nessa ordem (mais pro menos recente).
            comparaPedido(pedidoClienteA3, pedidos.get(0));
            comparaPedido(pedidoClienteA2, pedidos.get(1));
            comparaPedido(pedidoClienteA, pedidos.get(2));
        }

        @Test
        @DisplayName("Quando um cliente sem pedidos quer visualizar o histórico")
        void testVisualizarHistoricoPedidosVazio() throws Exception {

            List<Pedido> pedidos = pedidoListarHistoricoService.listarHistorico(
                    9999L,
                    clienteA.getCodigoDeAcesso(),
                    null
            );


            assertEquals(pedidos.size(),0);
        }

        @Test
        @DisplayName("Quando um cliente quer visualizar seu histórico com código inválido")
        void testVisualizarHistoricoPedidosCodigoInvalido() throws Exception {
            assertThrows(CodigoDeAcessoInvalidoException.class, () -> {
                pedidoListarHistoricoService.listarHistorico(
                        clienteA.getId(),
                        "CódigoInválido",
                        null
                    );
                }
            );
        }

        @Test
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

            List<Pedido> pedidosEntregues = pedidoListarHistoricoService.listarHistorico(
                    clienteA.getId(),
                    clienteA.getCodigoDeAcesso(),
                    Acompanhamento.PEDIDO_ENTREGUE
            );

            List<Pedido> pedidosEmRota = pedidoListarHistoricoService.listarHistorico(
                    clienteA.getId(),
                    clienteA.getCodigoDeAcesso(),
                    Acompanhamento.PEDIDO_EM_ROTA
            );

            List<Pedido> pedidosProntos = pedidoListarHistoricoService.listarHistorico(
                    clienteA.getId(),
                    clienteA.getCodigoDeAcesso(),
                    Acompanhamento.PEDIDO_PRONTO
            );

            List<Pedido> pedidosRecebidos = pedidoListarHistoricoService.listarHistorico(
                    clienteA.getId(),
                    clienteA.getCodigoDeAcesso(),
                    Acompanhamento.PEDIDO_RECEBIDO
            );

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
