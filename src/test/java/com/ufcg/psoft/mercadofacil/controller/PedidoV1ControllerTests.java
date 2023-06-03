package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.*;
import com.ufcg.psoft.mercadofacil.repository.*;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoConfirmarPagamentoService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoIndicarProntoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static com.ufcg.psoft.mercadofacil.model.MeioDePagamento.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Pedidos")
public class PedidoV1ControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    PedidoIndicarProntoService pedidoIndicarProntoService;

    @Autowired
    PedidoConfirmarPagamentoService pedidoConfirmarPagamentoService;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Pedido pedido;
    List<Pizza> pizzas;
    Estabelecimento estabelecimento;
    Cliente cliente;

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
    void tearDown() {
        associacaoRepository.deleteAll();
        pedidoRepository.deleteAll();
        entregadorRepository.deleteAll();
        clienteRepository.deleteAll();
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    private List<Pizza> duasCalabresasGrandesCreator() {

        Sabor sabor = saborRepository.save(Sabor.builder().nomeSabor("Calabresa").tipoSabor("Salgada").precoGrande(60.00).precoMedio(50.00).estabelecimento(estabelecimento).build());
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);

        Pizza duasCalabresasGrandes = new Pizza(sabores, false, true, 2);
        List<Pizza> novoPedido = new ArrayList<Pizza>();
        novoPedido.add(duasCalabresasGrandes);

        return novoPedido;
    }

    private List<Pizza> pizzasAtumPacoca(){
        Sabor atum =  Sabor.builder().nomeSabor("Atum").tipoSabor("Salgada").precoGrande(70.00).precoMedio(60.00).estabelecimento(estabelecimento).build();
        Sabor pacoca =  Sabor.builder().nomeSabor("Pacoca").tipoSabor("Doce").precoGrande(60.00).precoMedio(50.00).estabelecimento(estabelecimento).build();

        atum = saborRepository.save(atum);
        pacoca = saborRepository.save(pacoca);

        List<Sabor> sabores = new ArrayList<Sabor>();
        sabores.add(atum);
        sabores.add(pacoca);

        List<Pizza> pizzaMaldita = new ArrayList<>();
        Pizza p = new Pizza(sabores, true, true, 1);
        pizzaMaldita.add(p);

        return pizzaMaldita;
    }

    private List<Pizza> pizzasUmSabor(){

        Sabor atum =  Sabor.builder().nomeSabor("Atum").tipoSabor("Salgada").precoGrande(70.00).precoMedio(60.00).estabelecimento(estabelecimento).build();
        Sabor pacoca =  Sabor.builder().nomeSabor("Pacoca").tipoSabor("Doce").precoGrande(60.00).precoMedio(50.00).estabelecimento(estabelecimento).build();
        List<Sabor> sabor1 = new ArrayList<>();
        sabor1.add(atum);
        sabor1.add(atum);
        List<Sabor> sabor2 = new ArrayList<>();
        sabor2.add(pacoca);
        sabor2.add(pacoca);

        List<Pizza> pizzaMaldita = new ArrayList<>();
        Pizza p1 = new Pizza(sabor1, true, false,1);
        Pizza p2 = new Pizza(sabor2, true, true,1);
        pizzaMaldita.add(p1);
        pizzaMaldita.add(p2);

        return pizzaMaldita;
    }

    @Nested
    class PedidoPostTests {

        @Test
        @Transactional
        @DisplayName("Criação de Pedido válido")
        public void testCriaPedidoValido() throws Exception {

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Jose Lesinho")
                    .endereco("Rua Sem Nome S/N")
                    .codigoDeAcesso("admin123")
                    .pedidos(new ArrayList<>())
                    .build());

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCliente(cliente2.getId())
                    .pizzas(pizzasAtumPacoca())
                    .meioDePagamento(PIX)
                    .idEstabelecimento(estabelecimento.getId())
                    .enderecoAlternativo("")
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals(pedidoDTO.getIdCliente(),pedidoSalvo.getCliente().getId());
            assertEquals(pedidoDTO.getPizzas().get(0).getQuantidade(), pedidoSalvo.getPizzas().get(0).getQuantidade());
            assertEquals(2,pedidoRepository.findAll().size());

        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (lista de pizzas vazia)")
        public void testCriaPedidoInvalidoMapVazio() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(cliente.getId())
                    .pizzas(new ArrayList<>())
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode estar vazia.")
                    || error.getErrors().contains("A listagem de pedidos nao pode ser null.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (map de pizzas null)")
        public void testCriaPedidoInvalidoMapNull() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(cliente.getId())
                    .pizzas(null)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode estar vazia.")
                    || error.getErrors().contains("A listagem de pedidos nao pode ser null.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (idCliente inválida - null)")
        public void testCriaPedidoInvalidoIdClienteInvalidaNull() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(null)
                    .pizzas(pizzas)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A id do cliente nao deve ser nula.")
                    || error.getErrors().contains("A id do cliente deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (idCliente inválida - menor que 1L)")
        public void testCriaPedidoInvalidoIdClienteInvalidaNaoPositiva() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(-2L)
                    .pizzas(pizzas)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A id do cliente nao deve ser nula.")
                    || error.getErrors().contains("A id do cliente deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (código de acesso inválido - null)")
        public void testCriaPedidoInvalidoCANull() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(null)
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Codigo de acesso nao pode estar em branco.")
                    || error.getErrors().contains("Codigo de acesso nao pode ser null.")
                    || error.getErrors().contains("Codigo de acesso nao pode ser vazio.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (código de acesso inválido - vazio)")
        public void testCriaPedidoInvalidoCAVazio() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso("")
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Codigo de acesso nao pode estar em branco.")
                    || error.getErrors().contains("Codigo de acesso nao pode ser null.")
                    || error.getErrors().contains("Codigo de acesso nao pode ser vazio.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Tenta criar um pedido inválido (código de acesso inválido - em branco)")
        public void testCriaPedidoInvalido() throws Exception {

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso("     ")
                    .idCliente(cliente.getId())
                    .pizzas(pizzas)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Codigo de acesso nao pode estar em branco.")
                    || error.getErrors().contains("Codigo de acesso nao pode ser null.")
                    || error.getErrors().contains("Codigo de acesso nao pode ser vazio.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Criação de Pedido sem endereço alternativo")
        public void testCriaPedidoSemEndereco() throws Exception {

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Jose Lesinho")
                    .endereco("Rua Sem Nome S/N")
                    .codigoDeAcesso("admin123")
                    .pedidos(new ArrayList<Pedido>())
                    .build());

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCliente(cliente2.getId())
                    .pizzas(pizzasAtumPacoca())
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(PIX)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos" + "?codigoDeAcesso=" + cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals("Rua Sem Nome S/N", resposta.getEndereco());
            assertEquals(pedidoDTO.getIdCliente(), pedidoSalvo.getCliente().getId());
            assertEquals(2, pedidoRepository.findAll().size());
        }

    }

    @Nested
    class PedidoGetTests {

        @Test
        @Transactional
        @DisplayName("Busca um pedido de um cliente")
        public void testBuscaPedidoAtual() throws Exception {

            String responseJsonString = driver.perform(get("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso=123456")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resultado = objectMapper.readValue(responseJsonString, Pedido.class);

            assertEquals(pedido.getId(), resultado.getId());
            assertEquals(pedido.getCliente().getId(), resultado.getCliente().getId());
            assertEquals(pedido.getCliente().getNome(), resultado.getCliente().getNome());
            assertEquals(pedido.getEndereco(), resultado.getEndereco());
        }

        @Test
        @Transactional
        @DisplayName("Busca um pedido inválido")
        public void testBuscaPedidoInvalido() throws Exception {

            MvcResult resultado = driver.perform(get("/v1/pedidos/" + pedido.getId() + 1L)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertTrue(resultado.getResolvedException() instanceof Exception);
        }

    }

    @Nested
    class PedidoPutTests {

        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumentos válidos (endereço)")
        public void testAtualizacaoValidaEndereco() throws Exception {

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(cliente.getId())
                    .idEstabelecimento(estabelecimento.getId())
                    .enderecoAlternativo("Avenida Augusto dos Anjos 44")
                    .pizzas(pedido.getPizzas())
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId())
                            .queryParam("codigoDeAcesso", cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            assertEquals("Avenida Augusto dos Anjos 44", resposta.getEndereco());
        }

        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumentos válidos (pizzas do pedido)")
        public void testAtualizacaoValidaListaPizzas() throws Exception {

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(cliente.getId())
                    .idEstabelecimento(estabelecimento.getId())
                    .pizzas(duasCalabresasGrandesCreator())
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            assertEquals(duasCalabresasGrandesCreator().get(0).getQuantidade(), resposta.getPizzas().get(0).getQuantidade());
            assertEquals(60.00, resposta.getPizzas().get(0).getPrecoPizza());
        }

        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumento inválido (map de pizzas null)")
        public void testAtualizacaoInvalidaPizzasNull() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(cliente.getId())
                    .pizzas(null)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode ser null.")
                    || error.getErrors().contains("A listagem de pedidos nao pode estar vazia.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumento inválido (map de pizzas vazia)")
        public void testAtualizacaoInvalidaPizzasVazia() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idCliente(cliente.getId())
                    .pizzas(new ArrayList<>())
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode ser null.")
                    || error.getErrors().contains("A listagem de pedidos nao pode estar vazia.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Confirmação do pagamento de um pedido válido via PIX")
        public void testConfirmacaoPagamentoPIXPedidoValido() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idEstabelecimento(estabelecimento.getId())
                    .pizzas(duasCalabresasGrandesCreator())
                    .meioDePagamento(PIX)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            assertEquals(duasCalabresasGrandesCreator().get(0).getQuantidade(), resposta.getPizzas().get(0).getQuantidade());
            assertEquals(PIX, resposta.getMeioDePagamento());
            assertEquals(114.0, resposta.getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirmação do pagamento de um pedido válido via cartão de crédito")
        public void testConfirmacaoPagamentoCREDITOPedidoValido() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .pizzas(duasCalabresasGrandesCreator())
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(CREDITO)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            assertEquals(duasCalabresasGrandesCreator().get(0).getQuantidade(), resposta.getPizzas().get(0).getQuantidade());
            assertEquals(CREDITO, resposta.getMeioDePagamento());
            assertEquals(120.0, resposta.getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirmação do pagamento de um pedido válido via cartão de débito")
        public void testConfirmacaoPagamentoDEBITOPedidoValido() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .idEstabelecimento(estabelecimento.getId())
                    .pizzas(duasCalabresasGrandesCreator())
                    .meioDePagamento(DEBITO)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            assertEquals(duasCalabresasGrandesCreator().get(0).getQuantidade(), resposta.getPizzas().get(0).getQuantidade());
            assertEquals(DEBITO, resposta.getMeioDePagamento());
            assertEquals(117.0, resposta.getPrecoPedido());
        }

        @Test
        @Transactional
        @DisplayName("Confirmação do pagamento de um pedido inválido")
        public void testConfirmacaoPagamentoPedidoInvalido() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .pizzas(new ArrayList<>())
                    .meioDePagamento(DEBITO)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode ser null.")
                    || error.getErrors().contains("A listagem de pedidos nao pode estar vazia.");

            assertTrue(errorStringTester);
        }

        @Test
        @Transactional
        @DisplayName("Confirmação do pagamento de um pedido com código de acesso inválido")
        public void testConfirmacaoPagamentoPedidoCodigoAcessoInvalido() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso("234567")
                    .pizzas(duasCalabresasGrandesCreator())
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(PIX)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=234567")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            assertEquals("O cliente nao possui permissao para alterar dados de outro cliente", error.getMessage());
        }

    }

    @Nested
    class PedidoDeleteTests {

        @Test
        @Transactional
        @DisplayName("Exclusão por ID de um pedido existente")
        public void testExclusaoPorIDValida() throws Exception {

            driver.perform(delete("/v1/pedidos/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Exclusao por ID de um pedido inválido (potencial pedido de outro cliente)")
        public void testExclusaoPorIDInvalida() throws Exception {

            driver.perform(delete("/v1/pedidos/" + (pedido.getId() + 1L))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(1, pedidoRepository.findAll().size());
        }

    }

    @Nested
    @DisplayName("Testes relacionados ao preço do pedido")
    class PedidoPrecoTests {


        Sabor saborCalabresa;

        Sabor saborMussarela;

        Sabor saborBrigadeiro;

        Cliente cliente2;

        PedidoPostPutRequestDTO pedidoSimplesDTO;

        PedidoPostPutRequestDTO pedidoCompostoDTO;

        List<Pizza> pizzasSimples;

        List<Pizza> pizzasCompostas;
        @BeforeEach
        void setup() {
            cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Jose Lesinho")
                    .endereco("Rua Sem Nome S/N")
                    .codigoDeAcesso("admin123")
                    .pedidos(new ArrayList<Pedido>())
                    .build());

            criaSabores();

            pedidoSimplesDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCliente(cliente2.getId())
                    .pizzas(criaPizzasSimples())
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(PIX)
                    .build();

            pedidoCompostoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCliente(cliente2.getId())
                    .idEstabelecimento(estabelecimento.getId())
                    .pizzas(criaPizzasCompostas())
                    .meioDePagamento(PIX)
                    .build();
        }

        private void criaSabores() {
            saborCalabresa = saborRepository.save(
                    Sabor.builder()
                            .precoMedio(40.0)
                            .precoGrande(60.0)
                            .tipoSabor("Salgada")
                            .nomeSabor("Calabresa")
                            .estabelecimento(estabelecimento)
                            .build()
            );

            saborMussarela = saborRepository.save(
                    Sabor.builder()
                            .precoMedio(30.0)
                            .precoGrande(50.0)
                            .tipoSabor("Salgada")
                            .nomeSabor("Mussarela")
                            .estabelecimento(estabelecimento)
                            .build()
            );

            saborBrigadeiro = saborRepository.save(
                    Sabor.builder()
                            .precoMedio(50.0)
                            .precoGrande(70.0)
                            .tipoSabor("Doce")
                            .nomeSabor("Brigadeiro")
                            .estabelecimento(estabelecimento)
                            .build()
            );
        }

        private List<Pizza> criaPizzasSimples() {

            Pizza pizzaCalabresa;
            Pizza pizzaMussarela;
            Pizza pizzaBrigadeiro;

            pizzaCalabresa = Pizza.builder()
                    .quantidade(1)
                    .precoPizza(40.0)
                    .sabor1(saborCalabresa)
                    .ehGrande(false)
                    .build();

            pizzaMussarela = Pizza.builder()
                    .quantidade(1)
                    .precoPizza(30.0)
                    .ehGrande(false)
                    .sabor1(saborMussarela)
                    .build();

            pizzaBrigadeiro = Pizza.builder()
                    .quantidade(1)
                    .precoPizza(50.0)
                    .ehGrande(false)
                    .sabor1(saborBrigadeiro)
                    .build();

            List<Pizza> pizzas = new ArrayList<Pizza>();

            pizzas.add(pizzaBrigadeiro);
            pizzas.add(pizzaMussarela);
            pizzas.add(pizzaCalabresa);

            return pizzas;
        }

        private List<Pizza> criaPizzasCompostas() {

            Pizza pizzaMussarelaCalabresa;
            Pizza pizzaBrigadeiro;

            pizzaMussarelaCalabresa = Pizza.builder()
                    .sabor1(saborCalabresa)
                    .sabor2(saborMussarela)
                    .ehGrande(true)
                    .quantidade(1)
                    .precoPizza(55.0)
                    .build();


            pizzaBrigadeiro = Pizza.builder()
                    .sabor1(saborBrigadeiro)
                    .quantidade(1)
                    .precoPizza(70.0)
                    .ehGrande(true)
                    .build();

            List<Pizza> pizzas = new ArrayList<Pizza>();

            pizzas.add(pizzaBrigadeiro);
            pizzas.add(pizzaMussarelaCalabresa);

            return pizzas;
        }

        @Test
        @Transactional
        @DisplayName("Teste de preço com pizzas de um único sabor.")
        void testPrecoPedidoPizzaSimples() throws Exception{
            String jsonString = objectMapper.writeValueAsString(pedidoSimplesDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso=" + cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals(pedidoSalvo.getPrecoPedido(), 114);
            assertEquals(pedidoSimplesDTO.getIdCliente(), pedidoSalvo.getCliente().getId());
            assertEquals(2, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Teste de preço com pizzas de dois sabores.")
        void testPrecoPedidoPizzaCompostas() throws Exception{
            String jsonString = objectMapper.writeValueAsString(pedidoCompostoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso=" + cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals(pedidoSalvo.getPrecoPedido(), 118.75);
            assertEquals(pedidoSimplesDTO.getIdCliente(), pedidoSalvo.getCliente().getId());
            assertEquals(2, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Teste quando o preço da pizza está incoerente com o sabor.")
        void testSegurancaContraFraudeDeValorDePizza() throws Exception {
            Sabor saborAtum = saborRepository.save(
                    Sabor.builder()
                            .precoMedio(40.0)
                            .precoGrande(60.0)
                            .tipoSabor("Salgada")
                            .nomeSabor("Calabresa")
                            .estabelecimento(estabelecimento)
                            .build()
            );

            Pizza pizzaAtum = Pizza.builder()
                    .sabor1(saborAtum)
                    .quantidade(1)
                    .ehGrande(true)
                    // tentando enganar :)
                    .precoPizza(15.0)
                    .build();

            List<Pizza> pizzaErronea = new ArrayList<>();
            pizzaErronea.add(pizzaAtum);

            PedidoPostPutRequestDTO pedidoErroneoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCliente(cliente2.getId())
                    .pizzas(pizzaErronea)
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(PIX)
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoErroneoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso=" + cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            assertEquals(error.getMessage(), "O preco do pedido requisitado nao e valido. Pizzas foram instanciadas com precos erroneos");
        }
    }

    @Nested
    @DisplayName("Testes relacionados a atualização de status de pedido de andamento para pronto")
    class PedidoPatchProntoTests {
        @Test
        @Transactional
        @DisplayName("Quando atualizo para PEDIDO_PRONTO com sucesso")
        public void test01() throws Exception {
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .pizzas(duasCalabresasGrandesCreator())
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(PIX)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoAtualizado = objectMapper.readValue(respostaJson2, Pedido.class);

            assertEquals(Acompanhamento.PEDIDO_PRONTO, pedidoAtualizado.getAcompanhamento());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar pedido para PEDIDO_PRONTO mas o pagamento não foi confirmado")
        public void test02() throws Exception {
            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson2, CustomErrorType.class);
            assertEquals(error.getMessage(), "A operacao de mudanca de status nao pode ser realizada.");

        }
    }

    @Nested
    public class PedidoPatchAtribuicaoEntregadorTests {
        PedidoPostPutRequestDTO pedidoDTO;
        @BeforeEach
        public void setUpAtribuicao() throws Exception {
            pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .idCliente(cliente.getId())
                    .codigoDeAcesso(cliente.getCodigoDeAcesso())
                    .pizzas(duasCalabresasGrandesCreator())
                    .idEstabelecimento(estabelecimento.getId())
                    .meioDePagamento(PIX)
                    .build();

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/pedido-pronto")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
        }
        @Test
        @DisplayName("Quando atribuo um entregador existente a um pedido existente")
        @Transactional
        public void test01() throws Exception {
            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/atribuir-entregador?idEntregador=" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoAtualizado = objectMapper.readValue(respostaJson2, Pedido.class);

            assertEquals(pedidoAtualizado.getAcompanhamento(), Acompanhamento.PEDIDO_EM_ROTA);
            assertNotNull(pedidoAtualizado.getEntregador());
        }

        @Test
        @DisplayName("Quando tento atribuir um pedido existente a um entregador inexistente")
        @Transactional
        public void test02() throws Exception{
            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/atribuir-entregador?idEntregador=" + (entregador.getId()+99))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson2, CustomErrorType.class);

            assertEquals(error.getMessage(), "O entregador consultado nao existe!");
            assertEquals(pedidoRepository.findById(pedido.getId()).orElseThrow(PedidoNaoExisteException::new).getAcompanhamento(),
                    Acompanhamento.PEDIDO_PRONTO);
        }

        @Test
        @DisplayName("Quando tento atribuir um pedido inexistente a um entregador existente")
        @Transactional
        public void test03() throws Exception {
            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + (pedido.getId()+99) + "/atribuir-entregador?idEntregador=" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson2, CustomErrorType.class);

            assertEquals(error.getMessage(), "Pedido com id informado nao existe.");
            assertEquals(pedidoRepository.findById(pedido.getId()).orElseThrow(PedidoNaoExisteException::new).getAcompanhamento(),
                    Acompanhamento.PEDIDO_PRONTO);
        }

        @Test
        @DisplayName("Quando tento atribuir um entregador existente a um pedido existente mas o pedido não está pronto")
        @Transactional
        public void test04() throws Exception {

            String respostaJson = driver.perform(put("/v1/pedidos/" + pedido.getId() + "/confirmarPagamento?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/atribuir-entregador?idEntregador=" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson2, CustomErrorType.class);

            assertEquals(error.getMessage(), "A operacao de mudanca de status nao pode ser realizada.");
            assertEquals(pedidoRepository.findById(pedido.getId()).orElseThrow(PedidoNaoExisteException::new).getAcompanhamento(),
                    Acompanhamento.PEDIDO_EM_PREPARO);
        }

        @Test
        @DisplayName("Quando tento atribuir um pedido existente a um entregador existente mas não há associação")
        @Transactional
        public void test05() throws Exception {
            Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Joao")
                    .corDoVeiculo("Preto")
                    .placaDoVeiculo("123456")
                    .tipoDoVeiculo(TipoDoVeiculo.CARRO)
                    .codigoDeAcesso("12345678").build());

            String respostaJson2 = driver.perform(patch("/v1/pedidos/" + pedido.getId() + "/atribuir-entregador?idEntregador=" + entregador2.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson2, CustomErrorType.class);

            assertEquals(error.getMessage(), "A associacao consultada nao existe!");
            assertEquals(pedidoRepository.findById(pedido.getId()).orElseThrow(PedidoNaoExisteException::new).getAcompanhamento(),
                    Acompanhamento.PEDIDO_PRONTO);


        }
    }

}
