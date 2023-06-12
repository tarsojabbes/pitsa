package com.ufcg.psoft.mercadofacil.controller;

import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para a camada de controlador de Clientes em testes de cancelamento de pedido")
public class ClienteV1ControllerCancelamentoPedidoTests {
    @Autowired
    MockMvc driver;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    ClienteRepository clienteRepository;

    Estabelecimento estabelecimento;

    Associacao associacao;

    Entregador entregador;

    Pedido pedido;
    Cliente cliente;

    List<Pizza> pizzas;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @BeforeEach
    void setUpCancelamento() {
        cliente = clienteRepository.save(Cliente.builder()
                .codigoDeAcesso("123456")
                .nome("Tarso Jabbes")
                .endereco("Rua Aprigio Veloso")
                .build());

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
        pizzas = duasCalabresasGrandesCreator();

        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .pizzas(pizzas)
                .estabelecimento(estabelecimento)
                .build());
    }

    @AfterEach
    void tearDown() {
        associacaoRepository.deleteAll();
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        entregadorRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    private List<Pizza> duasCalabresasGrandesCreator(){
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
    @DisplayName("Quando cancelo um pedido com sucesso")
    public void test01() throws Exception {
        String responseJson = driver.perform(delete("/v1/clientes/" + "cancelar-pedido/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        assertEquals("", responseJson);
        assertNull(pedidoRepository.findById(pedido.getId()).orElse(null));
    }

    @Test
    @Transactional
    @DisplayName("Quando tento cancelar um pedido que não existe")
    public void test02() throws Exception {
        driver.perform(delete("/v1/clientes/" + "cancelar-pedido/" + (pedido.getId()+99) + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Pedido com id informado nao existe."));
        assertNotNull(pedidoRepository.findById(pedido.getId()));
    }

    @Test
    @Transactional
    @DisplayName("Quando tento cancelar um pedido mas com código de acesso inválido")
    public void test03() throws Exception {
        driver.perform(delete("/v1/clientes/" + "cancelar-pedido/" + pedido.getId() + "?codigoDeAcesso=" + (cliente.getCodigoDeAcesso()+99))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O cliente nao possui permissao para alterar dados de outro cliente"));
        assertNotNull(pedidoRepository.findById(pedido.getId()));
    }

    @Test
    @Transactional
    @DisplayName("Quando tento cancelar um pedido que já está pronto")
    public void test04() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
        pedidoRepository.save(pedido);

        driver.perform(delete("/v1/clientes/" + "cancelar-pedido/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
               .andExpect(content().string("A operacao de mudanca de status nao pode ser realizada."));
        assertNotNull(pedidoRepository.findById(pedido.getId()));
    }

    @Test
    @Transactional
    @DisplayName("Quando tento cancelar um pedido que já está em rota")
    public void test05() throws Exception {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
        pedido.setEntregador(entregador);
        pedidoRepository.save(pedido);

        driver.perform(delete("/v1/clientes/" + "cancelar-pedido/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A operacao de mudanca de status nao pode ser realizada."));
        assertNotNull(pedidoRepository.findById(pedido.getId()));
    }
}
