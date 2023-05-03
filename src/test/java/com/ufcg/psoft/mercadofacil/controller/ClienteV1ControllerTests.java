package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para camada de controlador de Cliente")
public class ClienteV1ControllerTests {
    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

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

            List<ClienteGetResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<ClienteGetResponseDTO>>(){});

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

}
