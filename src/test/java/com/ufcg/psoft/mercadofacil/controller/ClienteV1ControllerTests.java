package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
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

import java.util.ArrayList;
import java.util.List;

import static com.ufcg.psoft.mercadofacil.model.MeioDePagamento.PIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para camada de controlador de Cliente")
public class ClienteV1ControllerTests {

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

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Cliente cliente;

    @BeforeEach
    public void setUp() {
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente A")
                .codigoDeAcesso("123456")
                .endereco("Rua A, 123")
                .build());
    }

    @AfterEach
    public void tearDown() {
        clienteRepository.deleteAll();
    }

    @Nested
    public class GetClienteTests {
        @Test
        @Transactional
        @DisplayName("Quando busco todos os clientes salvos")
        public void test01() throws Exception {
            clienteRepository.save(Cliente.builder()
                    .nome("Cliente B")
                    .endereco("Rua B, 234")
                    .codigoDeAcesso("654321")
                    .build());

            String responseJsonString = driver.perform(get("/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<ClienteGetResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<ClienteGetResponseDTO>>() {
            });

            assertEquals(2, resultado.size());
        }

        @Test
        @Transactional
        @DisplayName("Quando busco um cliente existente pelo ID")
        public void test02() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes" + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteGetResponseDTO clienteGetResponseDTO = objectMapper.readValue(responseJsonString, ClienteGetResponseDTO.class);

            assertEquals(cliente.getNome(), clienteGetResponseDTO.getNome());
            assertEquals(cliente.getEndereco(), clienteGetResponseDTO.getEndereco());
        }

        @Test
        @Transactional
        @DisplayName("Quando busco um cliente inexistente pelo ID")
        public void test03() throws Exception {
            String responseJsonString = driver.perform(get("/v1/clientes" + "/" + cliente.getId() + 99)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente consultado nao existe!", error.getMessage());
        }
    }

    @Nested
    public class PostClienteTests {
        @Test
        @Transactional
        @DisplayName("Quando crio um cliente com dados válidos")
        public void test01() throws Exception {
            clienteRepository.deleteAll();

            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente C")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso("0987654321")
                    .build();

            String responseJsonString = driver.perform(post("/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente cliente1 = objectMapper.readValue(responseJsonString, Cliente.class);

            Cliente clienteSalvo = clienteRepository.findById(cliente1.getId()).orElse(null);

            assertNotNull(clienteSalvo);
            assertEquals(clientePostPutRequestDTO.getNome(), clienteSalvo.getNome());
            assertEquals(clientePostPutRequestDTO.getEndereco(), clienteSalvo.getEndereco());
            assertEquals(clientePostPutRequestDTO.getCodigoDeAcesso(), clienteSalvo.getCodigoDeAcesso());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um cliente com nome inválido")
        public void test02() throws Exception {
            clienteRepository.deleteAll();

            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso("0987654321")
                    .build();

            String responseJsonString = driver.perform(post("/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Nome do cliente nao pode ser vazio"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um cliente com endereço inválido")
        public void test03() throws Exception {
            clienteRepository.deleteAll();

            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente C")
                    .endereco("")
                    .codigoDeAcesso("0987654321")
                    .build();

            String responseJsonString = driver.perform(post("/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Endereco do cliente nao pode ser vazio"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um cliente com código de acesso inválido")
        public void test04() throws Exception {
            clienteRepository.deleteAll();

            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente C")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso("098")
                    .build();

            String responseJsonString = driver.perform(post("/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Codigo de acesso deve ter tamanho minimo de 6 digitos"));
            assertFalse(error.getErrors().contains("Codigo de acesso nao pode ser vazio"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um cliente com código de acesso nulo")
        public void test05() throws Exception {
            clienteRepository.deleteAll();

            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente C")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso(null)
                    .build();

            String responseJsonString = driver.perform(post("/v1/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertFalse(error.getErrors().contains("Codigo de acesso deve ter tamanho minimo de 6 digitos"));
            assertTrue(error.getErrors().contains("Codigo de acesso do cliente nao pode ser vazio"));
        }
    }

    @Nested
    public class PutClienteTests {
        @Test
        @Transactional
        @DisplayName("Quando atualizo algum dado válido do cliente")
        public void test01() throws Exception {
            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente A")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso("123456")
                    .build();

            driver.perform(put("/v1/clientes" + "/" + cliente.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente clienteSalvo = clienteRepository.findById(cliente.getId()).orElse(null);

            assertNotNull(clienteSalvo);
            assertEquals(clientePostPutRequestDTO.getNome(), clienteSalvo.getNome());
            assertEquals(clientePostPutRequestDTO.getEndereco(), clienteSalvo.getEndereco());
            assertEquals(clientePostPutRequestDTO.getCodigoDeAcesso(), clienteSalvo.getCodigoDeAcesso());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar algum dado inválido do cliente")
        public void test02() throws Exception {
            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente A")
                    .endereco("")
                    .codigoDeAcesso("123456")
                    .build();

            String responseJsonString = driver.perform(put("/v1/clientes" + "/" + cliente.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Rua A, 123", clienteRepository.findById(cliente.getId()).orElse(null).getEndereco());
            assertEquals("Endereco do cliente nao pode ser vazio", error.getErrors().get(0));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar um dado de um cliente que não existe")
        public void test03() throws Exception {
            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente A")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso("123456")
                    .build();

            String responseJsonString = driver.perform(put("/v1/clientes" + "/" + (cliente.getId() + 99) + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente consultado nao existe!", error.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar um dado passando credenciais erradas")
        public void test04() throws Exception {
            ClientePostPutRequestDTO clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente A")
                    .endereco("Rua C, 345")
                    .codigoDeAcesso("123456")
                    .build();

            String responseJsonString = driver.perform(put("/v1/clientes" + "/" + cliente.getId() + "?codigoDeAcesso=codigoIncorreto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente nao possui permissao para alterar dados de outro cliente", error.getMessage());
        }
    }

    @Nested
    public class DeleteClienteTests {
        @Test
        @Transactional
        @DisplayName("Quando excluo um cliente existente no banco com credenciais corretas")
        public void test01() throws Exception {
            driver.perform(delete("/v1/clientes" + "/" + cliente.getId() + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, clienteRepository.findAll().size());
            assertFalse(clienteRepository.findAll().contains(cliente));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento excluir um cliente que não existe no banco")
        public void test02() throws Exception {
            String responseJsonString = driver.perform(delete("/v1/clientes" + "/" + (cliente.getId() + 99) + "?codigoDeAcesso=" + cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente consultado nao existe!", error.getMessage());
            assertEquals(1, clienteRepository.findAll().size());
            assertTrue(clienteRepository.findAll().contains(cliente));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento excluir um cliente existente com credenciais erradas")
        public void test03() throws Exception {
            String responseJsonString = driver.perform(delete("/v1/clientes" + "/" + cliente.getId() + "?codigoDeAcesso=codigoIncorreto")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente nao possui permissao para alterar dados de outro cliente", error.getMessage());
            assertEquals(1, clienteRepository.findAll().size());
            assertTrue(clienteRepository.findAll().contains(cliente));
        }
    }

    @Nested
    public class DemonstrarInteressePorPizza {

        Estabelecimento estabelecimento;
        Cliente cliente;
        Sabor sabor;

        @BeforeEach
        void setup() {
            cliente = clienteRepository.save(Cliente.builder()
                    .codigoDeAcesso("12345")
                    .nome("Sabrina Barbosa")
                    .endereco("Avenida Ipiranga com Avenida São João")
                    .build());

            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("123456")
                    .nome("Estabelecimento Zero")
                    .build());

            sabor = saborRepository.save(
                    Sabor.builder()
                            .nomeSabor("Sabor Zero")
                            .tipoSabor("Salgado")
                            .precoMedio(20.00)
                            .precoGrande(40.00)
                            .estabelecimento(estabelecimento)
                            .build());
        }

        @AfterEach
        public void tearDown() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
            clienteRepository.deleteAll();
        }

        @Test
        @Transactional
        @DisplayName("Quando tento demonstrar interesse por um sabor com credenciais erradas")
        public void testDemonstrarInteressePorSabor_CredenciaisErradas() throws Exception {
            Long clienteId = cliente.getId();
            Long saborId = sabor.getId();
            String codigoDeAcesso = "senhaerrada";

            driver.perform(post("/v1/clientes/cliente/{clienteId}/sabor/{saborId}/interesse", clienteId, saborId)
                            .param("codigoDeAcesso", codigoDeAcesso))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("O cliente nao possui permissao para alterar dados de outro cliente"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento demonstrar interesse por um sabor com Cliente não existente")
        public void testDemonstrarInteressePorSabor_ClienteNaoExiste() throws Exception {
            Long clienteId = 100L;
            Long saborId = sabor.getId();

            driver.perform(post("/v1/clientes/cliente/{clienteId}/sabor/{saborId}/interesse", clienteId, saborId)
                            .param("codigoDeAcesso", "12345"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("O cliente consultado nao existe!"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento demonstrar interesse por um sabor com Sabor não existente")
        public void testDemonstrarInteressePorSabor_SaborNaoExiste() throws Exception {
            Long clienteId = cliente.getId();
            Long saborId = 100L;

            driver.perform(post("/v1/clientes/cliente/{clienteId}/sabor/{saborId}/interesse", clienteId, saborId)
                            .param("codigoDeAcesso", "12345"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("O sabor de pizza consultado nao existe."));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento demonstrar interesse por um sabor com Sabor disponível")
        public void testDemonstrarInteressePorSabor_SaborDisponivel() throws Exception {
            Long clienteId = cliente.getId();
            Long saborId = sabor.getId();

            driver.perform(post("/v1/clientes/cliente/{clienteId}/sabor/{saborId}/interesse", clienteId, saborId)
                            .param("codigoDeAcesso", "12345"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("O sabor de pizza consultado esta disponivel."));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento demonstrar interesse por um sabor com Sabor indisponível")
        public void testDemonstrarInteressePorSabor_SaborIndisponivel() throws Exception {
            Long clienteId = cliente.getId();
            Long saborId = sabor.getId();

            sabor.setDisponivel(false);
            saborRepository.save(sabor);

            driver.perform(post("/v1/clientes/cliente/{clienteId}/sabor/{saborId}/interesse", clienteId, saborId)
                            .param("codigoDeAcesso", "12345"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Interesse registrado com sucesso."));
        }

    }

    @Nested
    public class ConfirmarEntregaDePedido {

        private List<Pizza> duasCalabresasGrandesCreator() {

            Sabor sabor = saborRepository.save(Sabor.builder().nomeSabor("Calabresa").tipoSabor("Salgada").precoGrande(60.00).precoMedio(50.00).estabelecimento(estabelecimento).build());
            List<Sabor> sabores = new ArrayList<>();
            sabores.add(sabor);

            Pizza duasCalabresasGrandes = new Pizza(sabores, false, true, 2);
            List<Pizza> novoPedido = new ArrayList<Pizza>();
            novoPedido.add(duasCalabresasGrandes);

            return novoPedido;
        }
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

        @Test
        @DisplayName("Quando confirmo a entrega do pedido com sucesso")
        @Transactional
        public void test01() throws Exception{
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);

            String respostaJson3 = driver.perform(patch("/v1/clientes/" + pedido.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido pedidoEntregue = objectMapper.readValue(respostaJson3, Pedido.class);

            System.out.println(pedidoEntregue.getAcompanhamento());
            assertEquals(Acompanhamento.PEDIDO_ENTREGUE, pedidoEntregue.getAcompanhamento());
        }

        @Test
        @DisplayName("Quando confirmo a entrega do pedido sem sucesso, ou seja, o status atual do pedido nao era PEDIDO_EM_ROTA")
        @Transactional
        public void test02() throws Exception{

            String respostaJson1 = driver.perform(patch("/v1/clientes/" + pedido.getId() + "/confirmar-entrega")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CustomErrorType error = objectMapper.readValue(respostaJson1, CustomErrorType.class);
            assertEquals(error.getMessage(), "A operacao de mudanca de status nao pode ser realizada.");
        }

    }

}
