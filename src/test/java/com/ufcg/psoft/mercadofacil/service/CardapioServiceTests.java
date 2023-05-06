package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.cardapio.CardapioService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CardapioServiceTests {

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    CardapioService cardapioService;

    Sabor sabor;

    @BeforeEach
    public void setup() {
        sabor = saborRepository.save(
            Sabor.builder().nomeSabor("Calabresa")
            .tipoSabor("Salgado")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .build());
    }

    @AfterEach
    public void tearDown() {
        saborRepository.deleteAll();
    }

    @Test
    @DisplayName("Requisição de cardápio completo")
    public void testCardapioCompleto() {
        
        List<Sabor> cardapio = cardapioService.cardapioCompleto();

        assertEquals(1, cardapio.size());
        assertEquals(sabor, cardapio.get(0));

        Sabor margherita = Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .build();

        saborRepository.save(margherita);
        cardapio = cardapioService.cardapioCompleto();

        assertEquals(2, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));

        Sabor cartola = Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .build();

        saborRepository.save(cartola);
        cardapio = cardapioService.cardapioCompleto();

        assertEquals(3, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));
        assertEquals(cartola, cardapio.get(2));
    }

    @Test
    @DisplayName("Requisição de cardápio de sabores salgados")
    public void testCardapioSalgados(){

        List<Sabor> cardapio = cardapioService.cardapioSaboresSalgados();

        assertEquals(1, cardapio.size());
        assertEquals(sabor, cardapio.get(0));

        Sabor margherita = Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .build();

        saborRepository.save(margherita);
        cardapio = cardapioService.cardapioSaboresSalgados();

        assertEquals(2, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));

        Sabor cartola = Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .build();

        saborRepository.save(cartola);
        cardapio = cardapioService.cardapioSaboresSalgados();

        assertEquals(2, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));
        assertEquals(3,saborRepository.findAll().size());

    }

    @Test
    @DisplayName("Requisição de cardápio de sabores doces")
    public void testCardapioDoces(){

        List<Sabor> cardapio = cardapioService.cardapioSaboresDoces();

        assertEquals(0, cardapio.size());

        Sabor margherita = Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .build();

        saborRepository.save(margherita);
        cardapio = cardapioService.cardapioSaboresDoces();

        assertEquals(0, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));

        Sabor cartola = Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .build();

        saborRepository.save(cartola);
        cardapio = cardapioService.cardapioSaboresDoces();

        assertEquals(1, cardapio.size());
        assertEquals(cartola, cardapio.get(2));
        assertEquals(3,saborRepository.findAll().size());

    }
    
}