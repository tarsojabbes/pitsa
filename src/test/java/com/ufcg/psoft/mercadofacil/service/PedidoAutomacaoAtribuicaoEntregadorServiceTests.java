package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.*;
import com.ufcg.psoft.mercadofacil.service.associacao.AssociacaoService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteConfirmarEntregaService;
import com.ufcg.psoft.mercadofacil.service.pedido.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.ATIVO;
import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.DESCANSO;
import static com.ufcg.psoft.mercadofacil.model.MeioDePagamento.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PedidoAutomacaoAtribuicaoEntregadorServiceTests {

    @Autowired
    PedidoCriarService pedidoCriarService;

    @Autowired
    PedidoListarService pedidoListarService;

    @Autowired
    PedidoAlterarService pedidoAlterarService;

    @Autowired
    PedidoExcluirService pedidoExcluirService;

    @Autowired
    PedidoConfirmarPagamentoService pedidoConfirmarPagamentoService;

    @Autowired
    PedidoAtribuirEntregadorService pedidoAtribuirEntregadorService;

    @Autowired
    PedidoIndicarProntoService pedidoIndicarProntoService;

    @Autowired
    ClienteConfirmarEntregaService clienteConfirmarEntregaService;

    @Autowired
    AssociacaoService associacaoService;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    Pedido pedido;

    List<Pizza> pizzas;

    Cliente cliente;

    Estabelecimento estabelecimento;

    Associacao associacao;

    Entregador entregador;

    @BeforeEach
    void setup() {
        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Jose da Silva")
                .corDoVeiculo("Branco")
                .placaDoVeiculo("123456")
                .tipoDoVeiculo(TipoDoVeiculo.MOTO)
                .codigoDeAcesso("12345678").build());

        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("Jipao")
                .codigoDeAcesso("123456")
                .associacoes(new ArrayList<>())
                .build());

        associacao = associacaoRepository.save(Associacao.builder()
                .entregador(entregador)
                .estabelecimento(estabelecimento)
                .statusAssociacao(true)
                .build());

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Joao")
                .endereco("Rua 1")
                .codigoDeAcesso("123456")
                .build());

        pizzas = duasCalabresasGrandesCreator();

        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .pizzas(pizzas)
                .estabelecimento(estabelecimento)
                .build());

    }

    @AfterEach
    void tearDown() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    private List<Pizza> duasCalabresasGrandesCreator() {
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

        return novoPedido;
    }

    @Test
    @Transactional
    @DisplayName("Teste de atribuição automática quando não há entregador disponível")
    void atribuicaoAutomaticaSemEntregadorDisponivelTest() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        Pedido pedidoPronto = pedidoIndicarProntoService.indicarPedidoPronto(pedido.getId(),
                                                                            estabelecimento.getCodigoDeAcesso());

        assertEquals(pedidoPronto.getAcompanhamento(), Acompanhamento.PEDIDO_PRONTO);
    }

    @Test
    @Transactional
    @DisplayName("Teste de atribuição automática com entregador disponível")
    void atribuicaoAutomaticaComEntregadorDisponivelTest() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        associacaoService.alterarDisponibilidadeEntregador(
                entregador.getId(), ATIVO, associacao.getId(), entregador.getCodigoDeAcesso()
        );

        Pedido pedidoEmRota = pedidoIndicarProntoService.indicarPedidoPronto(pedido.getId(),
                estabelecimento.getCodigoDeAcesso());


        assertEquals(pedidoEmRota.getAcompanhamento(), Acompanhamento.PEDIDO_EM_ROTA);
        assertEquals(estabelecimento.getEntregadoresDisponiveis().size(), 0);
    }

    @Test
    @Transactional
    @DisplayName("Teste de atribuição automática com entregador disponível")
    void atribuicaoAutomaticaEsperandoEntregadorDisponivelTest() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        pedidoIndicarProntoService.indicarPedidoPronto(pedido.getId(),
                estabelecimento.getCodigoDeAcesso());

        associacaoService.alterarDisponibilidadeEntregador(
                entregador.getId(), ATIVO, associacao.getId(), entregador.getCodigoDeAcesso()
        );

        Pedido pedidoEmRota = pedidoRepository.findById(pedido.getId()).get();

        assertEquals(pedidoEmRota.getAcompanhamento(), Acompanhamento.PEDIDO_EM_ROTA);
    }

    @Test
    @Transactional
    @DisplayName("Teste priorizar entregador aguardando por mais tempo")
    void priorizaEntregadorAguardandoMaisTempo() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                .nome("Joao da Moto")
                .corDoVeiculo("Vermelho")
                .placaDoVeiculo("12345q")
                .tipoDoVeiculo(TipoDoVeiculo.MOTO)
                .codigoDeAcesso("12345678").build());

        Associacao associacao2 = associacaoRepository.save(Associacao.builder()
                .entregador(entregador2)
                .estabelecimento(estabelecimento)
                .statusAssociacao(true)
                .disponibilidadeEntregador(DESCANSO)
                .build());

        associacaoService.alterarDisponibilidadeEntregador(
                entregador2.getId(), ATIVO, associacao2.getId(), entregador2.getCodigoDeAcesso()
        );

        associacaoService.alterarDisponibilidadeEntregador(
                entregador.getId(), ATIVO, associacao.getId(), entregador.getCodigoDeAcesso()
        );

        Pedido pedidoEmRota = pedidoIndicarProntoService.indicarPedidoPronto(pedido.getId(),
                estabelecimento.getCodigoDeAcesso());

        assertEquals(pedidoEmRota.getAcompanhamento(), Acompanhamento.PEDIDO_EM_ROTA);
        assertEquals(estabelecimento.getEntregadoresDisponiveis().size(), 1);
        assertEquals(pedidoEmRota.getEntregador().getNome(), entregador2.getNome());
        assertEquals(entregadorRepository.findById(estabelecimento.getEntregadoresDisponiveis().get(0)).get().getNome(),
                entregador.getNome());
    }

    @Test
    @Transactional
    @DisplayName("Teste tentar colocar pedido pronto com código de acesso errado")
    void codigoDeAcessoIncorrecoTest() throws Exception {
        try {
            Pedido pedidoPronto = pedidoIndicarProntoService.indicarPedidoPronto(pedido.getId(),
                    "codigoInvalido");
        } catch (Exception e) {
            assertEquals("O estabelecimento nao possui permissao para alterar dados de outro estabelecimento",
                                e.getMessage());
        }
    }

}
