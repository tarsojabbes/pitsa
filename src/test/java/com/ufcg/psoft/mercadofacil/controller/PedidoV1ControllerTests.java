package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
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

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Pedido pedido;
    List<Pizza> pizzas;
    Estabelecimento estabelecimento;
    Cliente cliente;

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
                .endereco("abc")
                .build());
    }

    @AfterEach
    void tearDown() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    private List<Pizza> duasCalabresasGrandesCreator() {
        Sabor sabor = saborRepository.save(new Sabor(1L, "Calabresa", "Salgada", 50.00, 60.00, estabelecimento));
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);

        Pizza duasCalabresasGrandes = new Pizza(sabores, false, sabor.getPrecoGrande(), 2);
        List<Pizza> novoPedido = new ArrayList<Pizza>();
        novoPedido.add(duasCalabresasGrandes);

        return novoPedido;
    }

    private List<Pizza> pizzasAtumPacoca() {
        Sabor atum = new Sabor(2L, "Atum", "Salgada", 60.00, 70.00, estabelecimento);
        Sabor pacoca = new Sabor(4L, "Pacoca", "Doce", 50.00, 60.00, estabelecimento);
        List<Sabor> sabores = new ArrayList<Sabor>();
        sabores.add(atum);
        sabores.add(pacoca);

        List<Pizza> pizzaMaldita = new ArrayList<>();
        Pizza p = new Pizza(sabores, true, (atum.getPrecoGrande() + pacoca.getPrecoGrande()) / 2, 1);
        pizzaMaldita.add(p);

        return pizzaMaldita;
    }

    private List<Pizza> pizzasUmSabor(){
        Sabor atum = new Sabor(2L,"Atum","Salgada",60.00,70.00,estabelecimento);
        Sabor pacoca = new Sabor(4L,"Pacoca","Doce",50.00,60.00,estabelecimento);
        List<Sabor> sabor1 = new ArrayList<Sabor>();
        sabor1.add(atum);
        sabor1.add(atum);
        List<Sabor> sabor2 = new ArrayList<Sabor>();
        sabor2.add(pacoca);
        sabor2.add(pacoca);

        List<Pizza> pizzaMaldita = new ArrayList<>();
        Pizza p1 = new Pizza(sabor1, true, atum.getPrecoGrande(),1);
        Pizza p2 = new Pizza(sabor2, true, pacoca.getPrecoGrande(),1);
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
            assertEquals(pedidoDTO.getIdCliente(), pedidoSalvo.getCliente().getId());
            assertEquals(pedidoDTO.getPizzas().get(0).getQuantidade(), pedidoSalvo.getPizzas().get(0).getQuantidade());
            assertEquals(2, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Teste de preço com pizzas com dois sabores")
        public void testPrecoPedidoComPizzasComDoisSabores() throws Exception{

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
                    .enderecoAlternativo("")
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals(pedidoSalvo.getPrecoPedido(), 65);
            assertEquals(pedidoDTO.getIdCliente(), pedidoSalvo.getCliente().getId());
            assertEquals(pedidoDTO.getPizzas().get(0).getQuantidade(), pedidoSalvo.getPizzas().get(0).getQuantidade());
            assertEquals(2, pedidoRepository.findAll().size());

        }

        @Test
        @Transactional
        @DisplayName("Teste de preço com pizzas com um sabor")
        public void testPrecoPedidoComPizzasComUmSabor() throws Exception{

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Jose Lesinho")
                    .endereco("Rua Sem Nome S/N")
                    .codigoDeAcesso("admin123")
                    .pedidos(new ArrayList<Pedido>())
                    .build());

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCliente(cliente2.getId())
                    .pizzas(pizzasUmSabor())
                    .enderecoAlternativo("")
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals(pedidoSalvo.getPrecoPedido(), 130);
            assertEquals(pedidoDTO.getIdCliente(), pedidoSalvo.getCliente().getId());
            assertEquals(pedidoDTO.getPizzas().get(0).getQuantidade(), pedidoSalvo.getPizzas().get(0).getQuantidade());
            assertEquals(2, pedidoRepository.findAll().size());

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

            driver.perform(delete("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
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

            driver.perform(delete("/v1/pedidos/" + (pedido.getId() + 1L) + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(1, pedidoRepository.findAll().size());
        }

    }

}
