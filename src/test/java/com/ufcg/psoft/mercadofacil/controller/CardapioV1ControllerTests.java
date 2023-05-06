package com.ufcg.psoft.mercadofacil.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Sabors")
public class CardapioV1ControllerTests {

    @Autowired
    MockMvc driver;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    CardapioV1Controller cardapioV1Controller;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Sabor sabor;

    @BeforeEach
    public void setup() {
        sabor = saborRepository.save(
                Sabor.builder().nomeSabor("Calabresa").tipoSabor("Salgado")
                .precoMedio(50.00).precoGrande(60.00)
                .build());
    }

    @BeforeEach
    public void tearDown() {
        saborRepository.deleteAll();
    }

    
    @Test
    @DisplayName("Testa o cardápio completo")
    public void testCardapioCompleto() throws Exception {

        saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Margherita")
                    .tipoSabor("Salgado")
                    .precoMedio(45.00)
                    .precoGrande(55.00)
                    .build());

        String responseJsonString = driver.perform(get("/v1/cardapioCompleto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(2, resultado.size());
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
                .build());

        saborRepository.save(
            Sabor.builder()
                .nomeSabor("Cartola")
                .tipoSabor("Doce")
                .precoMedio(50.00)
                .precoGrande(60.00)
                .build());

        String responseJsonString = driver.perform(get("/v1/cardapioSalgado")
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
                .build());

        saborRepository.save(
            Sabor.builder()
                .nomeSabor("Cartola")
                .tipoSabor("Doce")
                .precoMedio(50.00)
                .precoGrande(60.00)
                .build());

            String responseJsonString = driver.perform(get("/v1/cardapioDoces")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn().getResponse().getContentAsString();

        List<Sabor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<List<Sabor>>() {});

        assertEquals(1, resultado.size());
    }
}
