package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.ATIVO;
import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.DESCANSO;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para sistema de automação de atribuição de pedidos a entregadores")
public class PedidoAutomacaoAtribuicaoEntregadorControllerTests {
    @Autowired
    MockMvc driver;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    ModelMapper modelMapper;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Entregador entregador;

    Estabelecimento estabelecimento;

    Associacao associacao;

    Pedido pedido;

    Cliente cliente;

    List<Pizza> pizzas;

    @BeforeEach
    public void setUp() {
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
                .disponibilidadeEntregador(DESCANSO)
                .build());

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Joao")
                .endereco("Rua 1")
                .codigoDeAcesso("123456")
                .pedidos(new ArrayList<>())
                .build());

        Sabor sabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Frango")
                .tipoSabor("salgado")
                .estabelecimento(estabelecimento)
                .precoGrande(59.90)
                .precoMedio(39.90)
                .build());

        List<Sabor> sabores = new ArrayList<Sabor>();
        sabores.add(sabor);

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
        associacaoRepository.deleteAll();
        pedidoRepository.deleteAll();
        entregadorRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }


   @Test
   @Transactional
   @DisplayName("Teste de atribuição automática quando não há entregador disponível")
   void atribuicaoAutomaticaSemEntregadorDisponivelTest() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        String respostaJson = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto?codigoDeAcesso=" + pedido.getEstabelecimento().getCodigoDeAcesso())
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andDo(print())
               .andReturn().getResponse().getContentAsString();

       Pedido pedidoPronto = objectMapper.readValue(respostaJson, Pedido.class);

       assertEquals(pedidoPronto.getAcompanhamento(), Acompanhamento.PEDIDO_PRONTO);
   }

    @Test
    @Transactional
    @DisplayName("Teste de atribuição automática com entregador disponível")
    void atribuicaoAutomaticaComEntregadorDisponivelTest() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        driver.perform(patch("/v1/entregadores/definir-disponibilidade/" + entregador.getId() + "/" + ATIVO + "?associacaoId=" + associacao.getId() + "&" + "codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        String respostaJson = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto?codigoDeAcesso=" + pedido.getEstabelecimento().getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        Pedido pedidoEmRota = objectMapper.readValue(respostaJson, Pedido.class);

        assertEquals(pedidoEmRota.getAcompanhamento(), Acompanhamento.PEDIDO_EM_ROTA);
        assertEquals(estabelecimento.getEntregadoresDisponiveis().size(), 0);
    }

    @Test
    @Transactional
    @DisplayName("Teste de atribuição automática com entregador disponível")
    void atribuicaoAutomaticaEsperandoEntregadorDisponivelTest() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_PREPARO);
        pedidoRepository.save(pedido);

        driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto?codigoDeAcesso=" + pedido.getEstabelecimento().getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        driver.perform(patch("/v1/entregadores/definir-disponibilidade/" + entregador.getId() + "/" + ATIVO + "?associacaoId=" + associacao.getId() + "&" + "codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

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

        driver.perform(patch("/v1/entregadores/definir-disponibilidade/" + entregador2.getId() + "/" + ATIVO + "?associacaoId=" + associacao2.getId() + "&" + "codigoDeAcesso=" + entregador2.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        driver.perform(patch("/v1/entregadores/definir-disponibilidade/" + entregador.getId() + "/" + ATIVO + "?associacaoId=" + associacao.getId() + "&" + "codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        String respostaJson = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto?codigoDeAcesso=" + pedido.getEstabelecimento().getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        Pedido pedidoEmRota = objectMapper.readValue(respostaJson, Pedido.class);

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
        String respostaJson = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto?codigoDeAcesso=" + "CodigoIncorreto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType erro =  objectMapper.readValue(respostaJson, CustomErrorType.class);

        assertEquals("O estabelecimento nao possui permissao para alterar dados de outro estabelecimento", erro.getMessage());
    }
}
