package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.ufcg.psoft.mercadofacil.dto.SaborAlterarDisponivelDTO;
import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Sabores")
public class SaborV1ControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Estabelecimento estabelecimento;

    Sabor sabor;

    @BeforeEach
    public void setup() {

        estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder()
                        .codigoDeAcesso("123456")
                        .nome("Jipao")
                        .build()
        );

        sabor = saborRepository.save(
                Sabor.builder()
                        .nomeSabor("Calabresa")
                        .tipoSabor("Salgado")
                        .precoMedio(50.00)
                        .precoGrande(60.00)
                        .estabelecimento(estabelecimento)
                        .build()
        );
    }

    @BeforeEach
    public void tearDown() {
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    @Nested
    class SaborGetTests {

        @Test
        @DisplayName("Busca por todos os sabores registrados.")
        public void testFindAll() throws Exception {

            saborRepository.save(
                    Sabor.builder()
                            .nomeSabor("Margherita")
                            .tipoSabor("Salgado")
                            .precoMedio(45.00)
                            .precoGrande(55.00)
                            .estabelecimento(estabelecimento)
                            .build());

            String responseJsonString = driver.perform(get("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

            assertEquals(2, resultado.size());

        }

        @Test
        @DisplayName("Busca de um sabor existente por ID válida")
        public void testIdValida() throws Exception {

            Sabor sabor = saborRepository.save(
                    Sabor.builder()
                            .nomeSabor("Margherita")
                            .tipoSabor("Salgado")
                            .precoMedio(45.00)
                            .precoGrande(55.00)
                            .estabelecimento(estabelecimento)
                            .build());

            String responseJsonString = driver.perform(get("/v1/sabores/{id}", sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor resultado = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals(sabor.getId(), resultado.getId());
            assertEquals(sabor.getNomeSabor(), resultado.getNomeSabor());
            assertEquals(sabor.getTipoSabor(), resultado.getTipoSabor());
            assertEquals(sabor.getPrecoMedio(), resultado.getPrecoMedio());
            assertEquals(sabor.getPrecoGrande(), resultado.getPrecoGrande());
        }

        @Test
        @DisplayName("Busca por ID de sabor inexistente")
        public void testBuscaSaborInexistente() throws Exception {

            Long inexistente = 56L;

            MvcResult result = driver.perform(get("/v1/sabores/" + inexistente)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertTrue(result.getResolvedException() instanceof MercadoFacilException);

        }
    }

    @Nested
    class SaborPostTests {

        @Test
        @DisplayName("Criacao de sabor com dados válidos")
        public void testCriacaoSaborValido() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita")
                    .tipoSabor("Salgado")
                    .precoMedio(45.00)
                    .precoGrande(55.00)
                    .idEstabelecimento(estabelecimento.getId())
                    .estabelecimento(estabelecimento)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores" + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .content(requestJsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            Sabor saborSalvo = saborRepository.findById(response.getId()).orElse(null);

            assertNotNull(saborSalvo);
            assertEquals(saborPostDto.getNomeSabor(), saborSalvo.getNomeSabor());
            assertEquals(saborPostDto.getTipoSabor(), saborSalvo.getTipoSabor());
            assertEquals(saborPostDto.getPrecoMedio(), saborSalvo.getPrecoMedio());
            assertEquals(saborPostDto.getPrecoGrande(), saborSalvo.getPrecoGrande());
        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com nome inválido (null)")
        public void testNomeSaborInvalidoNull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor(null)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Nome do sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Nome do sabor nao pode ser null.") ||
                    error.getErrors().contains("Nome do sabor nao pode estar em branco.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com nome inválido (vazio)")
        public void testNomeSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("")
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Nome do sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Nome do sabor nao pode ser null.") ||
                    error.getErrors().contains("Nome do sabor nao pode estar em branco.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com nome inválido (em branco)")
        public void testNomeSaborInvalidoEmBranco() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("   ")
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Nome do sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Nome do sabor nao pode ser null.") ||
                    error.getErrors().contains("Nome do sabor nao pode estar em branco.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com tipo de sabor inválido (null)")
        public void testTipoSaborInvalidonull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .tipoSabor(null)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Tipo de sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Tipo de sabor nao pode ser null.") ||
                    error.getErrors().contains("Tipo de sabor nao pode estar em branco.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com tipo de sabor inválido (vazio)")
        public void testTipoSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(45.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Tipo de sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Tipo de sabor nao pode ser null.") ||
                    error.getErrors().contains("Tipo de sabor nao pode estar em branco.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com tipo de sabor inválido (em branco)")
        public void testTipoSaborInvalidoEmBranco() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(45.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Tipo de sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Tipo de sabor nao pode ser null.") ||
                    error.getErrors().contains("Tipo de sabor nao pode estar em branco.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com preco de sabor médio inválido (null)")
        public void testPrecoMedioInvalidoNull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(null)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com preco de sabor médio inválido (zero)")
        public void testPrecoMedioInvalidoZero() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(0.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com preco de sabor médio inválido (valor negativo)")
        public void testPrecoMedioInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(-45.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com preco de sabor grande inválido (null)")
        public void testSaborGrandeInvalidoNull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(null)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com preco de sabor grande inválido (zero)")
        public void testSaborGrandeInvalidoZero() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(0.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);

        }

        @Test
        @DisplayName("Tentativa de criacao de Sabor com preco de sabor grande inválido (valor negativo)")
        public void testSaborGrandeInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(-55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);

        }

    }

    @Nested
    class SaborPutTests {

        @Test
        @DisplayName("Atualizacao de um sabor com dados válidos (nome do sabor)")
        public void testAtualizaDadosValidosNome() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa Acebolada")
                    .tipoSabor("Salgado")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .idEstabelecimento(estabelecimento.getId())
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId() + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);


            assertEquals("Calabresa Acebolada", response.getNomeSabor());
        }

        @Test
        @DisplayName("Atualizacao de um sabor com dados válidos (tipo de sabor)")
        public void testAtualizaDadosValidosTipo() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado 2: Electric Boogaloo")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .idEstabelecimento(estabelecimento.getId())
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId() + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals("Salgado 2: Electric Boogaloo", response.getTipoSabor());
        }

        @Test
        @DisplayName("Atualizacao de um sabor com dados válidos (preco do tamanho médio)")
        public void testAtualizaDadosValidosPrecoMedio() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado 2: Electric Boogaloo")
                    .precoMedio(45.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .idEstabelecimento(estabelecimento.getId())
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId() + "?codigoDeAcesso=" + estabelecimento.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals(45.00, response.getPrecoMedio());
        }

        @Test
        @DisplayName("Atualizacao de um sabor com dados válidos (preco do sabor grande)")
        public void testAtualizaDadosValidosPrecoGrande() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado 2: Electric Boogaloo")
                    .precoMedio(50.00)
                    .precoGrande(55.00)
                    .estabelecimento(estabelecimento)
                    .idEstabelecimento(estabelecimento.getId())
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId() + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals(55.00, response.getPrecoGrande());
        }

        @Test
        @DisplayName("Atualizacao do nome do sabor com dados inválidos (null)")
        public void testNomeSaborInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor(null)
                    .tipoSabor("Salgado")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Nome do sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Nome do sabor nao pode ser null.") ||
                    error.getErrors().contains("Nome do sabor nao pode estar em branco.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do nome do sabor com dados inválidos (vazio)")
        public void testNomeSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("")
                    .tipoSabor("Salgado")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Nome do sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Nome do sabor nao pode ser null.") ||
                    error.getErrors().contains("Nome do sabor nao pode estar em branco.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do nome do sabor com dados inválidos (em branco)")
        public void testNomeSaborInvalidoEmBranco() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("    ")
                    .tipoSabor("Salgado")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Nome do sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Nome do sabor nao pode ser null.") ||
                    error.getErrors().contains("Nome do sabor nao pode estar em branco.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do tipo de sabor com dados inválidos (null)")
        public void testTipoSaborInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor(null)
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Tipo de sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Tipo de sabor nao pode ser null.") ||
                    error.getErrors().contains("Tipo de sabor nao pode estar em branco.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do tipo de sabor com dados inválidos (vazio)")
        public void testTipoSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Tipo de sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Tipo de sabor nao pode ser null.") ||
                    error.getErrors().contains("Tipo de sabor nao pode estar em branco.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do tipo de sabor com dados inválidos (em branco)")
        public void testTipoSaborInvalidoEmBranco() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor(" ")
                    .precoMedio(50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Tipo de sabor nao pode ser vazio.") ||
                    error.getErrors().contains("Tipo de sabor nao pode ser null.") ||
                    error.getErrors().contains("Tipo de sabor nao pode estar em branco.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do preco do sabor médio com dados inválidos (null)")
        public void testPrecoMedioInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado")
                    .precoMedio(null)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do preco do sabor médio com dados inválidos (zero)")
        public void testPrecoMedioInvalidoZero() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado")
                    .precoMedio(0.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do preco do sabor médio com dados inválidos (valor negativo)")
        public void testPrecoMedioInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado")
                    .precoMedio(-50.00)
                    .precoGrande(60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do preco do sabor grande com dados inválidos (null)")
        public void testPrecoGrandeInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa")
                    .tipoSabor("Salgado")
                    .precoMedio(50.00)
                    .precoGrande(null)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do preco do sabor grande com dados inválidos (zero)")
        public void testPrecoGrandeInvalidoZero() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(0.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualizacao do preco do sabor grande com dados inválidos (valor negativo)")
        public void testPrecoGrandeInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(-60.00)
                    .estabelecimento(estabelecimento)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Preco nao pode ser null.") ||
                    error.getErrors().contains("Preco deve ser maior que zero.");

            assertTrue(errorStringTester);
        }

    }

    @Nested
    class SaborDeleteTests {

        @Test
        @DisplayName("Exclusao por ID de um sabor existente")
        public void testExclusaoPorIDValida() throws Exception {

            driver.perform(delete("/v1/sabores/" + sabor.getId() + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, saborRepository.findAll().size());
        }

        @Test
        @DisplayName("Exclusao por ID de um sabor inexistente")
        public void testExclusaoPorIDInvalida() throws Exception {

            driver.perform(delete("/v1/sabores/" + (sabor.getId() + 1L))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(1, saborRepository.findAll().size());
        }
    }

    @Nested
    class SaborAlterarDisponibilidadeTests {
        @Test
        @DisplayName("Quando altero a disponibilidade de um sabor com sucesso")
        public void test01() throws Exception {
            SaborAlterarDisponivelDTO saborAlterarDisponivelDTO = SaborAlterarDisponivelDTO.builder().disponivel(false).build();

            String responseJsonString = driver.perform(patch("/v1/sabores/" + sabor.getId() + "/disponibilidade" + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .content(objectMapper.writeValueAsString(saborAlterarDisponivelDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor saborAlterado = objectMapper.readValue(responseJsonString, Sabor.class);

            assertFalse(saborAlterado.getDisponivel());
        }

        @Test
        @DisplayName("Quando tento alterar disponibilidade de sabor inexistente")
        public void test02() throws Exception {
            SaborAlterarDisponivelDTO saborAlterarDisponivelDTO = SaborAlterarDisponivelDTO.builder().disponivel(false).build();

            String responseJsonString = driver.perform(patch("/v1/sabores/" + (sabor.getId() + 99) + "/disponibilidade" + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .content(objectMapper.writeValueAsString(saborAlterarDisponivelDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O sabor de pizza consultado nao existe.", error.getMessage());

        }

        @Test
        @DisplayName("Quando tento alterar a disponibilidade de sabor com código de acesso inválido")
        public void test03() throws Exception {
            SaborAlterarDisponivelDTO saborAlterarDisponivelDTO = SaborAlterarDisponivelDTO.builder().disponivel(false).build();

            String responseJsonString = driver.perform(patch("/v1/sabores/" + sabor.getId() + "/disponibilidade" + "?codigoDeAcesso=codigoInvalido")
                            .content(objectMapper.writeValueAsString(saborAlterarDisponivelDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O estabelecimento nao possui permissao para alterar dados de outro estabelecimento", error.getMessage());
        }

        @Test
        @DisplayName("Quando busco disponibilidade de um sabor existente")
        public void test04() throws Exception {
            String responseJsonString = driver.perform(get("/v1/sabores/" + sabor.getId() + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(Boolean.parseBoolean(responseJsonString));
        }

        @Test
        @DisplayName("Quando busco disponibilidade de um sabor inexistente")
        public void test05() throws Exception {
            String responseJsonString = driver.perform(get("/v1/sabores/" + (sabor.getId() + 99) + "/disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O sabor de pizza consultado nao existe.", error.getMessage());

        }


        @Test
        @DisplayName("Quando altero para sabor disponivel e notifico clientes interessados")
        public void test06() throws Exception {
            List<Integer> interessados = new ArrayList<Integer>();
            interessados.add(4);
            interessados.add(3);
            interessados.add(15);

            Sabor sabor1 = saborRepository.save(
                    Sabor.builder()
                            .nomeSabor("Frango com catupiry")
                            .tipoSabor("salgado")
                            .estabelecimento(estabelecimento)
                            .precoGrande(49.90)
                            .precoMedio(29.90)
                            .interessados(interessados)
                            .build());

            SaborAlterarDisponivelDTO saborAlterarDisponivelDTO = SaborAlterarDisponivelDTO.builder().disponivel(false).build();
            driver.perform(patch("/v1/sabores/" + sabor1.getId() + "/disponibilidade" + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .content(objectMapper.writeValueAsString(saborAlterarDisponivelDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            saborAlterarDisponivelDTO.setDisponivel(true);
            String responseJsonString = driver.perform(patch("/v1/sabores/" + sabor1.getId() + "/disponibilidade" + "?codigoDeAcesso="+estabelecimento.getCodigoDeAcesso())
                            .content(objectMapper.writeValueAsString(saborAlterarDisponivelDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor saborAlterado = objectMapper.readValue(responseJsonString, Sabor.class);

            assertTrue(saborAlterado.getInteressados().isEmpty());


        }
    }
}