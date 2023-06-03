package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.*;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteConfirmarEntregaService;
import com.ufcg.psoft.mercadofacil.service.pedido.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
    PedidoAtribuirEntregadorService pedidoAtribuirEntregadorService;

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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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
                    .idEstabelecimento(estabelecimento.getId())
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

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            PrintStream originalSystemOut = System.out;

            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .pizzas(pizzas)
                    .estabelecimento(estabelecimento)
                    .build());

            pedido2.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
            assertEquals(Acompanhamento.PEDIDO_EM_ROTA, pedido2.getAcompanhamento());

            System.setOut(printStream);
            clienteConfirmarEntregaService.confirmarPedidoEntregue(pedido2.getId());
            assertEquals(Acompanhamento.PEDIDO_ENTREGUE, pedido2.getAcompanhamento());

            try {

                String resultadoPrint = outputStream.toString();

                String regex = "Hibernate: .*";

                String resultadoFiltrado = resultadoPrint.replaceAll(regex, "").trim();

                String notificacaoEsperada = "Jipao, o pedido de número " + pedido2.getId() + " foi entregue.";
                assertEquals(notificacaoEsperada, resultadoFiltrado);

            } finally {
                System.setOut(originalSystemOut);
            }

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

    @Nested
    public class NotificarMudancaDeStatusTest {

        @Test
        @DisplayName("Quando mudo o status de um pedido de pedido pronto para pedido em rota e notifico o cliente")
        @Transactional
        public void test01() {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);

            PrintStream originalSystemOut = System.out;

            try {
                System.setOut(printStream);

                pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
                pedidoAtribuirEntregadorService.atribuirEntregador(pedido.getId(), entregador.getId());

                String resultadoPrint = outputStream.toString();

                String regex = "Hibernate: .*";

                String resultadoFiltrado = resultadoPrint.replaceAll(regex, "").trim();

                String notificacaoEsperada = "Joao, seu pedido está em rota de entrega\n" +
                        "--Informações do entregador--:\n" +
                        "Nome: Jose da Silva\n" +
                        "Tipo de Veiculo: MOTO\n" +
                        "Cor do Veiculo: Branco\n" +
                        "Placa do Veiculo: 123456";
                assertEquals(resultadoFiltrado, notificacaoEsperada);

            } finally {
                System.setOut(originalSystemOut);
            }
        }

    }

    @Nested
    public class PedidoAtribuirEntregadorServiceTests {
        @Test
        @DisplayName("Quando atribuo um entregador existente a um pedido existente")
        @Transactional
        public void test01() {

            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);

            Pedido pedidoComEntregadorAssociado = pedidoAtribuirEntregadorService.atribuirEntregador(pedido.getId(), entregador.getId());

            assertEquals(pedidoComEntregadorAssociado.getAcompanhamento(), Acompanhamento.PEDIDO_EM_ROTA);

        }

        @Test
        @DisplayName("Quando tento atribuir um pedido existente a um entregador inexistente")
        @Transactional
        public void test02() {
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
            assertThrows(EntregadorNaoExisteException.class,
                    () -> pedidoAtribuirEntregadorService.atribuirEntregador(pedido.getId(), entregador.getId() + 99));
        }

        @Test
        @DisplayName("Quando tento atribuir um pedido inexistente a um entregador existente")
        @Transactional
        public void test03() {
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
            assertThrows(PedidoNaoExisteException.class,
                    () -> pedidoAtribuirEntregadorService.atribuirEntregador(pedido.getId() + 99, entregador.getId()));
        }

        @Test
        @DisplayName("Quando tento atribuir um entregador existente a um pedido existente mas o pedido não está pronto")
        @Transactional
        public void test04() {
            assertThrows(MudancaDeStatusInvalidaException.class,
                    () -> pedidoAtribuirEntregadorService.atribuirEntregador(pedido.getId(), entregador.getId()));
        }

        @Test
        @DisplayName("Quando tento atribuir um pedido existente a um entregador existente mas não há associação")
        @Transactional
        public void test05() {
            Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Joao")
                    .corDoVeiculo("Preto")
                    .placaDoVeiculo("123456")
                    .tipoDoVeiculo(TipoDoVeiculo.CARRO)
                    .codigoDeAcesso("12345678").build());

            assertThrows(AssociacaoNaoExisteException.class,
                    () -> pedidoAtribuirEntregadorService.atribuirEntregador(pedido.getId(), entregador2.getId()));
        }
    }

}
