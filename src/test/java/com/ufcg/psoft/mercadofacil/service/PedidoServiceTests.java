package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
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
    PedidoConfirmarService pedidoConfirmarService;

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

    private List<Pizza> duasCalabresasGrandesCreator() {
        Sabor sabor = saborRepository.save(new Sabor(1L, "Calabresa", "Salgada", 50.00, 60.00, estabelecimento));
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);

        Pizza duasCalabresasGrandes = new Pizza(sabores, false, sabor.getPrecoGrande(), 2);
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
    public class PedidoConfirmarTests {

        @Test
        @Transactional
        @DisplayName("Confirma um pedido válido passando PIX como meio de pagamento")
        void testConfirmaPedidoValidoPIX() {
            // Valor antes de confirmar o pedido com o meio de pagamento
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());

            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(PIX)
                    .build();

            pedidoConfirmarService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(PIX, pedidoRepository.findById(pedido.getId()).get().getMeioDePagamento());
            // Novo valor com desconto aplicado (caso exista)
            assertEquals(114, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirma um pedido válido passando CREDITO como meio de pagamento")
        void testConfirmaPedidoValidoCREDITO() {
            // Valor antes de confirmar o pedido com o meio de pagamento
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());

            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(CREDITO)
                    .build();

            pedidoConfirmarService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(CREDITO, pedidoRepository.findById(pedido.getId()).get().getMeioDePagamento());
            // Novo valor com desconto aplicado (caso exista)
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirma um pedido válido passando DEBITO como meio de pagamento")
        void testConfirmaPedidoValidoDEBITO() {
            // Valor antes de confirmar o pedido com o meio de pagamento
            assertEquals(120, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());

            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .meioDePagamento(DEBITO)
                    .build();

            pedidoConfirmarService.confirmar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoConfirmado);

            assertEquals(DEBITO, pedidoRepository.findById(pedido.getId()).get().getMeioDePagamento());
            // Novo valor com desconto aplicado (caso exista)
            assertEquals(117, pedidoRepository.findById(pedido.getId()).get().getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Tenta confirmar um pedido inexistente passando um meio de pagamento")
        void testConfirmaPedidoInexistente() {
            PedidoPostPutRequestDTO pedidoConfirmado = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .meioDePagamento(PIX)
                    .build();

            assertThrows(MercadoFacilException.class, () -> pedidoConfirmarService.confirmar(pedido.getId() + 2L, cliente.getCodigoDeAcesso(), pedidoConfirmado));
        }

    }

}
