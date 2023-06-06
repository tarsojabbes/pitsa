package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.*;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteCancelarPedidoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClienteCancelarPedidoServiceTests {

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

    @Autowired
    ClienteCancelarPedidoService clienteCancelarPedidoService;

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
    @DisplayName("Quando cancelo um pedido com sucesso")
    void test01() {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_RECEBIDO);
        pedidoRepository.save(pedido);
        clienteCancelarPedidoService.cancelarPedido(pedido.getId(), cliente.getCodigoDeAcesso());
        assertFalse(pedidoRepository.findAll().contains(pedido));
    }

    @Test
    @DisplayName("Quando tento cancelar um pedido que não existe")
    void test02() {
        assertThrows(PedidoNaoExisteException.class,
                () -> clienteCancelarPedidoService.cancelarPedido(pedido.getId() + 99, cliente.getCodigoDeAcesso()));
    }

    @Test
    @DisplayName("Quando tento cancelar um pedido com código de acesso inválido")
    void test03() {
        assertThrows(ClienteNaoAutorizadoException.class,
                () -> clienteCancelarPedidoService.cancelarPedido(pedido.getId(), "codigoInvalido"));
    }

    @Test
    @DisplayName("Quando tento cancelar um pedido que já está pronto")
    void test04() {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
        pedidoRepository.save(pedido);

        assertThrows(MudancaDeStatusInvalidaException.class,
                () -> clienteCancelarPedidoService.cancelarPedido(pedido.getId(), cliente.getCodigoDeAcesso()));
    }

    @Test
    @DisplayName("Quando tento cancelar um pedido que está em rota")
    void test05() {
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
        pedidoRepository.save(pedido);
        pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
        pedido.setEntregador(entregador);
        pedidoRepository.save(pedido);

        assertThrows(MudancaDeStatusInvalidaException.class,
                () -> clienteCancelarPedidoService.cancelarPedido(pedido.getId(), cliente.getCodigoDeAcesso()));
    }
}
