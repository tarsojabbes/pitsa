package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Sabores")
public class SaborV1ControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    SaborRepository saborRepository;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Sabor sabor;

    @BeforeEach
    public void setup() {
        sabor = saborRepository.save(
                Sabor.builder().nomeSabor("Calabresa").tipoSabor("Salgado")
                .precoMedio(50.00).precoGrande(60.00)
                .build()
        );
    }

    @BeforeEach
    public void tearDown() {
        saborRepository.deleteAll();
    }

    @Nested
    public class SaborGetTests {

        @Test
        @DisplayName("Busca por todos os sabores registrados.")
        public void testFindAll() throws Exception {

            saborRepository.save(
                    Sabor.builder()
                            .nomeSabor("Margherita").tipoSabor("Salgado")
                            .precoMedio(45.00).precoGrande(55.00)
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
                        .nomeSabor("Margherita").tipoSabor("Salgado")
                        .precoMedio(45.00).precoGrande(55.00)
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
    public class SaborPostTests {
        
        @Test
        @DisplayName("Criação de sabor com dados válidos")
        public void testCraicaoSaborValido() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
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
        @DisplayName("Tentativa de criação de Sabor com nome inválido (null)")
        public void testNomeSaborInvalidoNull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor(null).tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome do sabor não pode ser null.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com nome inválido (vazio)")
        public void testNomeSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("").tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome do sabor não pode ser vazio.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com nome inválido (em branco)")
        public void testNomeSaborInvalidoEmBranco() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("   ").tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome do sabor não pode estar em branco.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com tipo de sabor inválido (null)")
        public void testTipoSaborInvalidonull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor(null)
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Tipo de sabor não pode ser null.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com tipo de sabor inválido (vazio)")
        public void testTipoSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("")
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Tipo de sabor não pode ser vazio.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com tipo de sabor inválido (em branco)")
        public void testTipoSaborInvalidoBranco() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor(" ")
                    .precoMedio(45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Tipo de sabor não pode estar em branco.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com preço de sabor médio inválido (null)")
        public void testPrecoMedioInvalidoNull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(null).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço não pode ser null.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com preço de sabor médio inválido (zero)")
        public void testPrecoMedioInvalidoZero() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(0.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com preço de sabor médio inválido (valor negativo)")
        public void testPrecoMedioInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(-45.00).precoGrande(55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com preço de sabor grande inválido (null)")
        public void testSaborGrandeInvalidoNull() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(null)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço não pode ser null.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com preço de sabor grande inválido (zero)")
        public void testSaborGrandeInvalidoZero() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(0.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));

        }

        @Test
        @DisplayName("Tentativa de criação de Sabor com preço de sabor grande inválido (valor negativo)")
        public void testSaborgrandeInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO saborPostDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Margherita").tipoSabor("Salgado")
                    .precoMedio(45.00).precoGrande(-55.00)
                    .build();

            String requestJsonString = objectMapper.writeValueAsString(saborPostDto);

            String responseJsonString = driver.perform(post("/v1/sabores")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJsonString))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));

        }

    }

    @Nested
    public class SaborPutTests {

        @Test
        @DisplayName("Atualização de um sabor com dados válidos (nome do sabor)")
        public void testAtualizaDadosValidosNome() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Vegetariana")
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals("Vegetariana", response.getNomeSabor());
        }

        @Test
        @DisplayName("Atualização de um sabor com dados válidos (tipo de sabor)")
        public void testAtualizaDadosValidosTipo() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .tipoSabor("Doce")
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals("Doce", response.getTipoSabor());
        }

        @Test
        @DisplayName("Atualização de um sabor com dados válidos (preço do tamanho médio)")
        public void testAtualizaDadosValidosPrecoMedio() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(35.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals(35.00, response.getPrecoMedio());
        }

        @Test
        @DisplayName("Atualização de um sabor com dados válidos (preço do sabor grande)")
        public void testAtualizaDadosValidosPrecoGrande() throws Exception {

            SaborPostPutRequestDTO requestPutDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(45.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestPutDto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Sabor response = objectMapper.readValue(responseJsonString, Sabor.class);

            assertEquals(45.00, response.getPrecoGrande());
        }

        @Test
        @DisplayName("Atualização do nome do sabor com dados inválidos (null)")
        public void testNomeSaborInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor(null)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome do sabor não pode ser null.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do nome do sabor com dados inválidos (vazio)")
        public void testNomeSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("")
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome do sabor não pode ser vazio.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do nome do sabor com dados inválidos (em branco)")
        public void testNomeSaborInvalidoEmBranco() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .nomeSabor("  ")
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome do sabor não pode estar em branco.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do tipo de sabor com dados inválidos (null)")
        public void testTipoSaborInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .tipoSabor(null)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Tipo de sabor não pode ser null.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do tipo de sabor com dados inválidos (vazio)")
        public void testTipoSaborInvalidoVazio() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .tipoSabor("")
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Tipo de sabor não pode ser vazio.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do tipo de sabor com dados inválidos (em branco)")
        public void testTipoSaborInvalidoBranco() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .tipoSabor(" ")
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Tipo de sabor não pode estar em branco.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do preço do sabor médio com dados inválidos (null)")
        public void testPrecoMedioInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(null)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço não pode ser null.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do preço do sabor médio com dados inválidos (zero)")
        public void testPrecoMedioInvalidoZero() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(0.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do preço do sabor médio com dados inválidos (valor negativo)")
        public void testPrecoMedioInvalidoNegativo() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .precoMedio(-45.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do preço do sabor grande com dados inválidos (null)")
        public void testPrecoGrandeInvalidoNull() throws Exception {

            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(null)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço não pode ser null.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do preço do sabor grande com dados inválidos (zero)")
        public void testPrecoGrandeInvalidoZero() throws Exception {
            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(0.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));
        }

        @Test
        @DisplayName("Atualização do preço do sabor grande com dados inválidos (valor negativo)")
        public void testPrecoGrandeInvalidoNegativo() throws Exception {
            SaborPostPutRequestDTO requestDto = SaborPostPutRequestDTO.builder()
                    .precoGrande(-55.00)
                    .build();

            String responseJsonString = driver.perform(put("/v1/sabores/" + sabor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Preço deve ser maior que zero.", error.getErrors().get(0));
        }

    }

    @Nested
    public class SaborDeleteTests {
        
        @Test
        @DisplayName("Exclusão por ID de um sabor existente")
        public void testExclusaoPorIDValida() throws Exception {

            driver.perform(delete("/v1/sabores/" + sabor.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, saborRepository.findAll().size());
        }

        @Test
        @DisplayName("Exclusão por ID de um sabor inexistente")
        public void testExclusaoPorIDInvalida() throws Exception {

            String responseJsonString = driver.perform(delete("/v1/sabores/" + sabor.getId() + 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O sabor de pizza consultado não existe.", error.getMessage());
        }
    }
}