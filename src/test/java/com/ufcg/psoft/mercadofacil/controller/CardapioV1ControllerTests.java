package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.SaborAlterarDisponivelDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import com.ufcg.psoft.mercadofacil.service.sabor.SaborAlterarDisponivelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Cardapio")
public class CardapioV1ControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    CardapioV1Controller cardapioV1Controller;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborAlterarDisponivelService saborAlterarDisponivelService;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Sabor sabor;

    Estabelecimento estabelecimento;

    @BeforeEach
    public void setup() {

        estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder()
                        .codigoDeAcesso("12345678")
                        .nome("Jipao")
                        .build()
        );

        sabor = saborRepository.save(
                Sabor.builder().nomeSabor("Calabresa")
                        .tipoSabor("Salgado")
                        .precoMedio(50.00)
                        .precoGrande(60.00)
                        .estabelecimento(estabelecimento)
                        .build());
    }

    @BeforeEach
    public void tearDown() {
        saborRepository.deleteAll();
    }


    @Test
    @DisplayName("Testa o cardápio completo")
    public void testCardapioCompleto() throws Exception {

        Sabor novoSabor = saborRepository.save(
                Sabor.builder()
                        .nomeSabor("Margherita")
                        .tipoSabor("Salgado")
                        .precoMedio(45.00)
                        .precoGrande(55.00)
                        .estabelecimento(estabelecimento)
                        .build());

        String responseJsonString = driver.perform(get("/v1/cardapios/" + estabelecimento.getId() + "/completo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(2, resultado.size());
        assertEquals(sabor.getNomeSabor(), resultado.get(0).getNomeSabor());
        assertEquals(sabor.getTipoSabor(), resultado.get(0).getTipoSabor());
        assertEquals(sabor.getPrecoMedio(),resultado.get(0).getPrecoMedio());
        assertEquals(sabor.getPrecoGrande(),resultado.get(0).getPrecoGrande());

        assertEquals(novoSabor.getNomeSabor(),resultado.get(1).getNomeSabor());
        assertEquals(novoSabor.getTipoSabor(),resultado.get(1).getTipoSabor());
        assertEquals(novoSabor.getPrecoMedio(),resultado.get(1).getPrecoMedio());
        assertEquals(novoSabor.getPrecoGrande(),resultado.get(1).getPrecoGrande());
    }

    @Test
    @DisplayName("Produz o cardápio de pizzas salgadas")
    public void testCardapioSalgadas() throws Exception{

        saborRepository.save(
                Sabor.builder()
                        .nomeSabor("Margherita")
                        .tipoSabor("Salgado")
                        .precoMedio(45.00)
                        .precoGrande(55.00)
                        .estabelecimento(estabelecimento)
                        .build());

        saborRepository.save(
                Sabor.builder()
                        .nomeSabor("Cartola")
                        .tipoSabor("Doce")
                        .precoMedio(50.00)
                        .precoGrande(60.00)
                        .estabelecimento(estabelecimento)
                        .build());

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ estabelecimento.getId() + "/salgados")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Produz o cardápio de pizzas doces")
    public void testCardapioDoces() throws Exception{

        saborRepository.save(
                Sabor.builder()
                        .nomeSabor("Margherita")
                        .tipoSabor("Salgado")
                        .precoMedio(45.00)
                        .precoGrande(55.00)
                        .estabelecimento(estabelecimento)
                        .build());

        saborRepository.save(
                Sabor.builder()
                        .nomeSabor("Cartola")
                        .tipoSabor("Doce")
                        .precoMedio(50.00)
                        .precoGrande(60.00)
                        .estabelecimento(estabelecimento)
                        .build());

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ estabelecimento.getId() +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Teste de argumento inválido para cardápio completo (id inválida - zero)")
    public void testCardapioCompletoArgumentoInvalidoZero() throws Exception {

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ estabelecimento.getId() +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(0, resultado.size());

    }

    @Test
    @DisplayName("Teste de argumento inválido para cardápio completo (id inválida - negativa)")
    public void testCardapioCompletoArgumentoInvalidoNegativo() throws Exception {

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ -78L +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        assertEquals("O estabelecimento consultado nao existe!", error.getMessage());
    }

    @Test
    @DisplayName("Teste de argumento inválido para cardápio de pizzas salgadas (id inválida - zero)")
    public void testArgumentoInvalidoZeroCardapioSalgado() throws Exception {

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ estabelecimento.getId() +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(0, resultado.size());

    }

    @Test
    @DisplayName("Teste de argumento inválido para cardápio de pizzas salgadas (id inválida - negativa)")
    public void testArgumentoInvalidoNegativoCardapioSalgado() throws Exception {

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ -99 +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        assertEquals("O estabelecimento consultado nao existe!", error.getMessage());

    }

    @Test
    @DisplayName("Teste de argumento inválido para cardápio de pizzas doces (id inválida - zero)")
    public void testArgumentoInvalidoZeroCardapioDoce() throws Exception {

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ estabelecimento.getId() +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(0, resultado.size());

    }

    @Test
    @DisplayName("Teste de argumento inválido para cardápio de pizzas doces (id inválida - negativa)")
    public void testArgumentoInvalidonegativoCardapioDoce() throws Exception {

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ -1 +"/doces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType error = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        assertEquals("O estabelecimento consultado nao existe!", error.getMessage());

    }

    @Test
    @DisplayName("Quando listo sabores de cardápio e possuo um sabor indisponível")
    public void testListagemCardapioSaborIndisponivel() throws Exception {
        Sabor margherita = saborRepository.save(Sabor.builder()
                .nomeSabor("Margherita")
                .tipoSabor("Salgado")
                .precoMedio(45.00)
                .precoGrande(55.00)
                .estabelecimento(estabelecimento)
                .build());

        Sabor cartola = saborRepository.save(Sabor.builder()
                .nomeSabor("Cartola")
                .tipoSabor("Doce")
                .precoMedio(50.00)
                .precoGrande(60.00)
                .estabelecimento(estabelecimento)
                .build());

        SaborAlterarDisponivelDTO saborAlterarDisponivelDTO = SaborAlterarDisponivelDTO.builder().disponivel(false).build();
        saborAlterarDisponivelService.alterar(sabor.getId(), estabelecimento.getCodigoDeAcesso(), saborAlterarDisponivelDTO);

        String responseJsonString = driver.perform(get("/v1/cardapios/"+ estabelecimento.getId() +"/completo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(3, resultado.size());
        assertEquals(sabor.getNomeSabor(), resultado.get(2).getNomeSabor());
        assertFalse(resultado.get(2).getDisponivel());


    }
}