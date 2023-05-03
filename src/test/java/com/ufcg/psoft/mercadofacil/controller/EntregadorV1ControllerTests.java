package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.EntregadorGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo.CARRO;
import static com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo.MOTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes para camada de controlador de Entregador")
public class EntregadorV1ControllerTests {
    @Autowired
    MockMvc driver;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    ModelMapper modelMapper;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Entregador entregador;

    @BeforeEach
    public void setUp() {
        entregador = entregadorRepository.save(Entregador.builder()
                .codigoDeAcesso("123456")
                .nome("Steve Jobs")
                .corDoVeiculo("preto")
                .placaDoVeiculo("ABC1234")
                .tipoDoVeiculo(MOTO)
                .build());
    }

    @AfterEach
    public void tearDown() {
        entregadorRepository.deleteAll();
    }

    @Nested
    public class GetEntregadorTests {
        @Test
        @Transactional
        @DisplayName("Quando busco todos os entregadores salvos")
        public void test01() throws Exception {
            EntregadorGetResponseDTO entregador1 = modelMapper.map(entregador, EntregadorGetResponseDTO.class);
            EntregadorGetResponseDTO entregador2 = modelMapper.map(entregadorRepository.save(Entregador.builder()
                            .codigoDeAcesso("123456")
                            .nome("Bill Gates")
                            .corDoVeiculo("vermelho")
                            .placaDoVeiculo("DEF1234")
                            .tipoDoVeiculo(CARRO)
                            .build()),
                    EntregadorGetResponseDTO.class);

            String responseJsonString = driver.perform(get("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<EntregadorGetResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

            assertEquals(2, resultado.size());
            assertTrue(resultado.contains(entregador1));
            assertTrue(resultado.contains(entregador2));
        }

        @Test
        @Transactional
        @DisplayName("Quando busco um entregador existente pelo ID")
        public void test02() throws Exception {
            String responseJsonString = driver.perform(get("/v1/entregadores" + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorGetResponseDTO entregadorGetResponseDTO = objectMapper.readValue(responseJsonString, EntregadorGetResponseDTO.class);

            assertEquals(entregador.getNome(), entregadorGetResponseDTO.getNome());
            assertEquals(entregador.getCorDoVeiculo(), entregadorGetResponseDTO.getCorDoVeiculo());
            assertEquals(entregador.getPlacaDoVeiculo(), entregadorGetResponseDTO.getPlacaDoVeiculo());
            assertEquals(entregador.getTipoDoVeiculo(), entregadorGetResponseDTO.getTipoDoVeiculo());
        }

        @Test
        @Transactional
        @DisplayName("Quando busco um entregador inexistente pelo ID")
        public void test03() throws Exception {
            String responseJsonString = driver.perform(get("/v1/entregadores" + "/" + entregador.getId() + 99)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O entregador consultado nao existe!", error.getMessage());
        }
    }

    @Nested
    public class PostEntregadorTests {
        @Test
        @Transactional
        @DisplayName("Quando crio um entregador com dados válidos")
        public void test01() throws Exception {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String responseJsonString = driver.perform(post("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador entregador1 = objectMapper.readValue(responseJsonString, Entregador.class);

            Entregador entregadorSalvo = entregadorRepository.findById(entregador1.getId()).orElse(null);

            assertNotNull(entregadorSalvo);
            assertEquals(entregadorPostPutRequestDTO.getNome(), entregadorSalvo.getNome());
            assertEquals(entregadorPostPutRequestDTO.getCorDoVeiculo(), entregadorSalvo.getCorDoVeiculo());
            assertEquals(entregadorPostPutRequestDTO.getPlacaDoVeiculo(), entregadorSalvo.getPlacaDoVeiculo());
            assertEquals(entregadorPostPutRequestDTO.getTipoDoVeiculo(), entregadorSalvo.getTipoDoVeiculo());
            assertEquals(entregadorPostPutRequestDTO.getCodigoDeAcesso(), entregadorSalvo.getCodigoDeAcesso());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um entregador com nome inválido")
        public void test02() throws Exception {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String responseJsonString = driver.perform(post("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Nome do entregador nao pode ser vazio"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um entregador com cor do veículo inválido")
        public void test03() throws Exception {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String responseJsonString = driver.perform(post("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Cor do veiculo nao pode ser vazio"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um entregador com placa do veículo inválido")
        public void test04() throws Exception {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String responseJsonString = driver.perform(post("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Placa do veiculo nao pode ser vazio"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um entregador com tipo do veículo inválido")
        public void test05() throws Exception {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(null)
                    .build();

            String responseJsonString = driver.perform(post("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Tipo de veiculo nao pode ser nulo"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento criar um entregador com código de acesso inválido")
        public void test06() throws Exception {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String responseJsonString = driver.perform(post("/v1/entregadores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Codigo de acesso deve ter tamanho minimo de 6 digitos"));
            assertTrue(error.getErrors().contains("Codigo de acesso do entregador nao pode ser vazio"));
        }
    }

    @Nested
    public class PutEntregadorTests {
        @Test
        @Transactional
        @DisplayName("Quando atualizo algum dado válido do entregador")
        public void test01() throws Exception {
            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Steve Jobs Sousa")
                    .corDoVeiculo("verde")
                    .placaDoVeiculo("GHI1234")
                    .tipoDoVeiculo(CARRO)
                    .build();

            driver.perform(put("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador entregadorSalvo = entregadorRepository.findById(entregador.getId()).orElse(null);

            assertNotNull(entregadorSalvo);
            assertEquals(entregadorPostPutRequestDTO.getNome(), entregadorSalvo.getNome());
            assertEquals(entregadorPostPutRequestDTO.getCorDoVeiculo(), entregadorSalvo.getCorDoVeiculo());
            assertEquals(entregadorPostPutRequestDTO.getPlacaDoVeiculo(), entregadorSalvo.getPlacaDoVeiculo());
            assertEquals(entregadorPostPutRequestDTO.getTipoDoVeiculo(), entregadorSalvo.getTipoDoVeiculo());
            assertEquals(entregadorPostPutRequestDTO.getCodigoDeAcesso(), entregadorSalvo.getCodigoDeAcesso());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar algum dado inválido do entregador")
        public void test02() throws Exception {
            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("")
                    .corDoVeiculo("verde")
                    .placaDoVeiculo("GHI1234")
                    .tipoDoVeiculo(null)
                    .build();

            String responseJsonString = driver.perform(put("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Steve Jobs", entregadorRepository.findById(entregador.getId()).orElse(null).getNome());
            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertTrue(error.getErrors().contains("Nome do entregador nao pode ser vazio"));
            assertTrue(error.getErrors().contains("Tipo de veiculo nao pode ser nulo"));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar um dado de um entregador que não existe")
        public void test03() throws Exception {
            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Steve Jobs Sousa")
                    .corDoVeiculo("preto")
                    .placaDoVeiculo("ADG1234")
                    .tipoDoVeiculo(CARRO)
                    .build();

            String responseJsonString = driver.perform(put("/v1/entregadores" + "/" + (entregador.getId() + 99) + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O entregador consultado nao existe!", error.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando tento atualizar um dado passando credenciais erradas")
        public void test04() throws Exception {
            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Steve Jobs Sousa")
                    .corDoVeiculo("verde")
                    .placaDoVeiculo("GHI1234")
                    .tipoDoVeiculo(CARRO)
                    .build();

            String responseJsonString = driver.perform(put("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=codigoIncorreto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O entregador nao possui permissao para alterar dados de outro entregador", error.getMessage());
        }

        @Test
        @Transactional
        @DisplayName("Quando atualizo um entregador com código de acesso menor que 6 caracteres")
        public void test05() throws Exception {
            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("12345")
                    .nome("Steve Jobs")
                    .corDoVeiculo("verde")
                    .placaDoVeiculo("GHI1234")
                    .tipoDoVeiculo(null)
                    .build();

            String responseJsonString = driver.perform(put("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
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
        @DisplayName("Quando atualizo um entregador com código de acesso nulo")
        public void test06() throws Exception {
            EntregadorPostPutRequestDTO entregadorPostPutRequestDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso(null)
                    .nome("Steve Jobs")
                    .corDoVeiculo("verde")
                    .placaDoVeiculo("GHI1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String responseJsonString = driver.perform(put("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", error.getMessage());
            assertFalse(error.getErrors().contains("Codigo de acesso deve ter tamanho minimo de 6 digitos"));
            assertTrue(error.getErrors().contains("Codigo de acesso do entregador nao pode ser vazio"));
        }
    }

    @Nested
    public class DeleteEntregadorTests {
        @Test
        @Transactional
        @DisplayName("Quando excluo um entregador existente no banco com credenciais corretas")
        public void test01() throws Exception {
            driver.perform(delete("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, entregadorRepository.findAll().size());
            assertFalse(entregadorRepository.findAll().contains(entregador));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento excluir um entregador que não existe no banco")
        public void test02() throws Exception {
            String responseJsonString = driver.perform(delete("/v1/entregadores" + "/" + (entregador.getId() + 99) + "?codigoDeAcesso=" + entregador.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O entregador consultado nao existe!", error.getMessage());
            assertEquals(1, entregadorRepository.findAll().size());
            assertTrue(entregadorRepository.findAll().contains(entregador));
        }

        @Test
        @Transactional
        @DisplayName("Quando tento excluir um entregador existente com credenciais erradas")
        public void test03() throws Exception {
            String responseJsonString = driver.perform(delete("/v1/entregadores" + "/" + entregador.getId() + "?codigoDeAcesso=codigoIncorreto")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O entregador nao possui permissao para alterar dados de outro entregador", error.getMessage());
            assertEquals(1, entregadorRepository.findAll().size());
            assertTrue(entregadorRepository.findAll().contains(entregador));
        }
    }

}
