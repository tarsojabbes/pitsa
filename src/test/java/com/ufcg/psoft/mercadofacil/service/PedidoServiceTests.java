package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteConfirmarEntregaService;
import com.ufcg.psoft.mercadofacil.service.pedido.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ufcg.psoft.mercadofacil.model.MeioDePagamento.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PedidoServiceTests {

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
    PedidoIndicarProntoService pedidoIndicarProntoService;

    @Autowired
    ClienteConfirmarEntregaService clienteConfirmarEntregaService;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    Pedido pedido;

    List<Pizza> pizzas;

    Cliente cliente;

    Estabelecimento estabelecimento;

    @BeforeEach
    void setup() {
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

        pizzas = duasCalabresasGrandesCreator();

        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .pizzas(pizzas)
                .build());
    }

    @AfterEach
    void tearDown() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
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

    @Nested
    public class PedidoCriarServiceTests {

        @Test
        @Transactional
        @DisplayName("Criacao de Pedido valido em BD vazio")
        void testPedidoValidoBDVazio() {

            pedidoRepository.deleteAll();
            assertEquals(0, pedidoRepository.findAll().size());

            PedidoPostPutRequestDTO novoPedido = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .pizzas(pizzas)
                    .meioDePagamento(PIX)
                    .enderecoAlternativo("")
                    .build();

            pedidoCriarService.criar(cliente.getCodigoDeAcesso(), novoPedido);

            assertEquals(1, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Criacao de Pedido valido em BD ja populado")
        void testPedidoValidoBDPopulado() {

            assertEquals(1, pedidoRepository.findAll().size());

            PedidoPostPutRequestDTO novoPedido = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .meioDePagamento(CREDITO)
                    .pizzas(pizzas)
                    .build();

            pedidoCriarService.criar(novoPedido.getCodigoDeAcesso(), novoPedido);

            assertEquals(2, pedidoRepository.findAll().size());
        }

    }

    @Nested
    public class PedidoAlterarTests {

        @Test
        @Transactional
        @DisplayName("Modifica um pedido válido")
        void testModificaPedido() {
            String novaRua = "Rua 2";
            PedidoPostPutRequestDTO pedidoModificado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .enderecoAlternativo(novaRua)
                    .build();

            pedidoAlterarService.alterar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoModificado);

            Optional<Pedido> pedidoAlterado = pedidoRepository.findById(pedido.getId());

            assertEquals(novaRua, pedidoAlterado.get().getEndereco());
        }

        @Test
        @Transactional
        @DisplayName("Tenta modificar um pedido inexistente")
        void testRemovePizzaValida() {
            PedidoPostPutRequestDTO pedidoModificado = PedidoPostPutRequestDTO.builder()
                    .enderecoAlternativo("Rua 2")
                    .build();

            assertThrows(MercadoFacilException.class, () -> pedidoAlterarService.alterar(pedido.getId() + 6L, cliente.getCodigoDeAcesso(), pedidoModificado).getClass());
        }

    }

    @Nested
    public class PedidoListarTests {

        @Test
        @Transactional
        @DisplayName("Lista um pedido de um cliente válido")
        void testListaPedidoClienteValido() {
            List<Pedido> pedidos = pedidoListarService.listar(pedido.getId(), cliente.getCodigoDeAcesso());
            Pedido pedido1 = pedidos.get(0);

            assertEquals(pedido.getMeioDePagamento(), pedido1.getMeioDePagamento());
            assertEquals(pedido.getPizzas(), pedido1.getPizzas());
            assertEquals(pedido.getEndereco(), pedido1.getEndereco());
        }

        @Test
        @Transactional
        @DisplayName("Tenta listar um pedido de cliente inválido")
        void testListaPedidoClienteInvalido() {
            assertThrows(MercadoFacilException.class, () -> pedidoListarService.listar(cliente.getId() + 1L, cliente.getCodigoDeAcesso()));

            assertThrows(MercadoFacilException.class, () -> pedidoListarService.listar(cliente.getId(), cliente.getCodigoDeAcesso() + "789"));
        }

    }

    @Nested
    public class PedidoExcluirTests {

        @Test
        @Transactional
        @DisplayName("Exclui um pedido corretamente a partir da id do cliente")
        void testExclusaoValidaPedido() {
            pedidoExcluirService.excluir(pedido.getId(), cliente.getCodigoDeAcesso());

            assertEquals(0, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Tenta excluir um pedido invalido")
        void testExclusaoInvalidaPedido() {
            assertEquals(1, pedidoRepository.findAll().size());

            assertThrows(MercadoFacilException.class, () -> pedidoExcluirService.excluir(pedido.getId() + 2L, cliente.getCodigoDeAcesso()));

            assertEquals(1, pedidoRepository.findAll().size());
        }

    }

    @Nested
    public class PedidoConfirmarPagamentoTests {

        @Test
        @Transactional
        @DisplayName("Confirma o pagamento de um pedido válido passando PIX como meio de pagamento")
        void testConfirmaPagamentoPedidoValidoPIX() {
            // Valor antes de confirmar o pedido com o meio de pagamento
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());

            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(PIX)
                    .build();

            pedidoConfirmarPagamentoService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(PIX, pedidoRepository.findById(pedido.getId()).get().getMeioDePagamento());
            // Novo valor com desconto aplicado (caso exista)
            assertEquals(114, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirma o pagamento de um pedido válido passando CREDITO como meio de pagamento")
        void testConfirmaPagamentoPedidoValidoCREDITO() {
            // Valor antes de confirmar o pedido com o meio de pagamento
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());

            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(CREDITO)
                    .build();

            pedidoConfirmarPagamentoService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(CREDITO, pedidoRepository.findById(pedido.getId()).get().getMeioDePagamento());
            // Novo valor com desconto aplicado (caso exista)
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirma o pagamento de um pedido válido passando DEBITO como meio de pagamento")
        void testConfirmaPagamentoPedidoValidoDEBITO() {
            // Valor antes de confirmar o pedido com o meio de pagamento
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());

            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(DEBITO)
                    .build();

            pedidoConfirmarPagamentoService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(DEBITO, pedidoRepository.findById(pedido.getId()).get().getMeioDePagamento());
            // Novo valor com desconto aplicado (caso exista)
            assertEquals(117, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Tenta confirmar o pagamento de um pedido inexistente passando um meio de pagamento")
        void testConfirmaPagamentoPedidoInexistente() {
            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .meioDePagamento(PIX)
                    .build();

            assertThrows(MercadoFacilException.class, () -> pedidoConfirmarPagamentoService.confirmar(pedido.getId() + 2L, cliente.getCodigoDeAcesso(), pedidoConfirmado));
        }

    }

    @Nested
    public class PedidoAlterarAcompanhamentoTests {
        @Test
        @DisplayName("Quando crio um pedido e checo se o acompanhamento é PEDIDO_RECEBIDO")
        @Transactional
        public void test01() {
            assertEquals(Acompanhamento.PEDIDO_RECEBIDO, pedido.getAcompanhamento());
        }

        @Test
        @DisplayName("Quando confirmo o pagamento de um pedido e checo se o acompanhamento é PEDIDO_EM_PREPARO")
        @Transactional
        public void test02() {
            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(DEBITO)
                    .build();

            Pedido pedido2 = pedidoConfirmarPagamentoService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(Acompanhamento.PEDIDO_EM_PREPARO, pedido2.getAcompanhamento());
        }

        @Test
        @DisplayName("Quando estabelecimento indica que o pedido está pronto")
        @Transactional
        public void test03() {
            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(DEBITO)
                    .build();

            Pedido pedido2 = pedidoConfirmarPagamentoService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            Pedido pedidoPronto = pedidoIndicarProntoService.indicarPedidoPronto(pedido2.getId());

            assertEquals(Acompanhamento.PEDIDO_PRONTO, pedidoPronto.getAcompanhamento());

        }

        @Test
        @DisplayName("Quando tento atualizar status para PEDIDO_PRONTO mas pagamento não foi confirmado")
        @Transactional
        public void test04() {
            assertThrows(MudancaDeStatusInvalidaException.class, () -> pedidoIndicarProntoService.indicarPedidoPronto(pedido.getId()));
        }

        @Test
        @DisplayName("Quando confirmo um pedido entregue que estava em rota de entrega")
        @Transactional
        public void test05() {
            pedidoRepository.deleteAll();
            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .pizzas(pizzas)
                    .build());

            pedido2.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
            assertEquals(Acompanhamento.PEDIDO_EM_ROTA, pedido2.getAcompanhamento());

            clienteConfirmarEntregaService.confirmarPedidoEntregue(pedido2.getId());
            assertEquals(Acompanhamento.PEDIDO_ENTREGUE, pedido2.getAcompanhamento());
        }

        @Test
        @DisplayName("Quando tento confirmar que um pedido foi entregue, mas ele não estava em rota de entrega")
        @Transactional
        public void test06() {
            pedidoRepository.deleteAll();
            Pedido pedido3 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .pizzas(pizzas)
                    .build());
            assertThrows(MudancaDeStatusInvalidaException.class, () -> clienteConfirmarEntregaService.confirmarPedidoEntregue(pedido3.getId()));
        }
    }

}
