package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Estabelecimentos")
public class EstabelecimentoV1ControllerTests {
    @Autowired
    MockMvc driver;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Estabelecimento estabelecimento;

    @BeforeEach
    public void setup() {
        estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder().codigoDeAcesso("1234567").nome("Estabelecimento A").build()
        );
    }

    @BeforeEach
    public void tearDown() {
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    public class GetEstabelecimentoTests {

        @Test
        @DisplayName("Quando busco todos os estabelecimentos salvos")
        public void test01() throws Exception {
            estabelecimentoRepository.save(
                    Estabelecimento.builder()
                            .nome("Estabelecimento 2")
                            .codigoDeAcesso("123")
                            .build());

            String responseJsonString = driver.perform(get("/v1/estabelecimentos")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Estabelecimento> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Estabelecimento>>() {});

            assertEquals(2, resultado.size());

        }

        @Test
        @DisplayName("Quando busco um estabelecimento existente pelo ID")
        public void test02() throws Exception {
            Estabelecimento estabelecimento = estabelecimentoRepository.save(
                    Estabelecimento.builder()
                            .nome("Estabelecimento 2")
                            .codigoDeAcesso("123")
                            .build());

            String responseJsonString = driver.perform(get("/v1/estabelecimentos/{id}", estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Estabelecimento resultado = objectMapper.readValue(responseJsonString, Estabelecimento.class);

            assertEquals(estabelecimento.getId(), resultado.getId());
            assertEquals(estabelecimento.getNome(), resultado.getNome());
            assertEquals(estabelecimento.getCodigoDeAcesso(), resultado.getCodigoDeAcesso());
        }

        @Test
        @DisplayName("Quando busco um estabelecimento inexistente pelo ID")
        public void test03() throws Exception {
            Long idInexistente = 4L;

            // Fazendo a requisição GET para o endpoint de um estabelecimento que não existe
            MvcResult result = driver.perform(get("/v1/estabelecimentos/" + idInexistente)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertTrue(result.getResolvedException() instanceof MercadoFacilException);

        }
    }

    @Nested
    public class PostEstabelecimentoTests {
        // Criar um estabelecimento com dados válidos
        @Test
        @DisplayName("Quando crio um estabelecimento com dados válidos")
        public void test01() throws Exception {
            EstabelecimentoPostPutRequestDTO requestDto = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoDeAcesso("456")
                    .nome("Estabelecimento 3")
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(requestDto);

            String responseJsonString = driver.perform(post("/v1/estabelecimentos")
                            .content(requestJsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Estabelecimento response = objectMapper.readValue(responseJsonString, Estabelecimento.class);

            Estabelecimento estabelecimentoSalvo = estabelecimentoRepository.findById(response.getId()).orElse(null);

            assertNotNull(estabelecimentoSalvo);
            assertEquals(requestDto.getCodigoDeAcesso(), estabelecimentoSalvo.getCodigoDeAcesso());
            assertEquals(requestDto.getNome(), estabelecimentoSalvo.getNome());
        }

        @Test
        @DisplayName("Quando tento criar um estabelecimento com nome vazio")
        public void test02() throws Exception {
            EstabelecimentoPostPutRequestDTO requestDto = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoDeAcesso("123")
                    .nome("")
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(requestDto);

            String responseJsonString = driver.perform(post("/v1/estabelecimentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validação encontrados", error.getMessage());
            assertEquals("Nome não pode ser vazio", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Quando tento criar um estabelecimento com código de acesso vazio")
        public void test03() throws Exception {
            EstabelecimentoPostPutRequestDTO requestDto = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoDeAcesso("")
                    .nome("nome")
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(requestDto);

            String responseJsonString = driver.perform(post("/v1/estabelecimentos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validação encontrados", error.getMessage());
            assertEquals("Código de acesso não pode ser vazio", error.getErrors().get(0));

        }
    }

    @Nested
    public class PutEstabelecimentoTests {
        @Test
        @DisplayName("Quando atualizo um estabelecimento com dados válidos")
        public void test01() throws Exception {
            EstabelecimentoPostPutRequestDTO requestDto = EstabelecimentoPostPutRequestDTO.builder()
                    .nome("Novo nome")
                    .codigoDeAcesso("123456")
                    .build();

            String responseJsonString = driver.perform(put("/v1/estabelecimentos/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Estabelecimento response = objectMapper.readValue(responseJsonString, Estabelecimento.class);
            assertEquals("Novo nome", response.getNome());
            assertEquals("123456", response.getCodigoDeAcesso());
        }

        @Test
        @DisplayName("Quando atualizo um estabelecimento com dados inválidos")
        public void test02() throws Exception {
            EstabelecimentoPostPutRequestDTO requestDto = EstabelecimentoPostPutRequestDTO.builder()
                    .nome("Nome novo")
                    .codigoDeAcesso("")
                    .build();

            String responseJsonString = driver.perform(put("/v1/estabelecimentos/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("Erros de validação encontrados", error.getMessage());
            assertEquals("Código de acesso não pode ser vazio", error.getErrors().get(0));
        }

    }

    @Nested
    public class DeleteEstabelecimentoTests {
        // Excluir estabelecimento existente pelo ID
        @Test
        @DisplayName("Quando excluo um estabelecimento com ID válido e existente no banco")
        public void test01() throws Exception {
            String responseJsonString = driver.perform(delete("/v1/estabelecimentos/" + estabelecimento.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, estabelecimentoRepository.findAll().size());
        }
        // Excluir estabelecimento inexistente pelo ID
        @Test
        @DisplayName("Quando excluo um estabelecimento não existente no banco pelo ID")
        public void test02() throws Exception {
            String responseJsonString = driver.perform(delete("/v1/estabelecimentos/" + estabelecimento.getId() + 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O estalecimento consultado não existe!", error.getMessage());
        }
    }
}
