package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
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

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    Estabelecimento estabelecimento;

    Sabor sabor;

    @BeforeEach
    public void setup() {
        estabelecimento = estabelecimentoRepository.save(
                        Estabelecimento.builder()
                            .nome("Jipao")
                            .codigoDeAcesso("12345")
                        .build());

        sabor = saborRepository.save(
            Sabor.builder().nomeSabor("Calabresa")
            .tipoSabor("Salgado")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .estabelecimento(estabelecimento)
        .build());
    }

    @AfterEach
    public void tearDown() {
        saborRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
    }

    @Test
    @DisplayName("Requisição de cardápio completo")
    public void testCardapioCompleto() {
        
        List<Sabor> cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());

        assertEquals(1, cardapio.size());
        assertEquals(sabor, cardapio.get(0));

        Sabor margherita = saborRepository.save(Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());

        assertEquals(2, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));

        Sabor cartola = saborRepository.save(Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());

        assertEquals(3, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));
        assertEquals(cartola, cardapio.get(2));

        Estabelecimento e2 = estabelecimentoRepository.save(
                            Estabelecimento.builder()
                                .nome("Ppiizzaa")
                                .codigoDeAcesso("11111")
                            .build());

        Sabor cartola2 = saborRepository.save(Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .estabelecimento(e2)
            .build());

        cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());

        assertEquals(3, cardapio.size());
        assertEquals(sabor, cardapio.get(0));
        assertEquals(margherita, cardapio.get(1));
        assertEquals(cartola, cardapio.get(2));
        assertEquals(true,saborRepository.findAll().contains(cartola2));
    }

    @Test
    @DisplayName("Requisição de cardápio de sabores salgados de um estabelecimento")
    public void testCardapioSalgados(){

        List<Sabor> cardapioSalgado = cardapioService.cardapioSaboresSalgados(estabelecimento.getId());

        assertEquals(1, cardapioSalgado.size());
        assertEquals(sabor, cardapioSalgado.get(0));

        Sabor margherita = saborRepository.save(Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapioSalgado = cardapioService.cardapioSaboresSalgados(estabelecimento.getId());

        assertEquals(2, cardapioSalgado.size());
        assertEquals(sabor, cardapioSalgado.get(0));
        assertEquals(margherita, cardapioSalgado.get(1));

        Sabor cartola = Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .estabelecimento(estabelecimento)
            .build();

        saborRepository.save(cartola);
        cardapioSalgado = cardapioService.cardapioSaboresSalgados(estabelecimento.getId());

        assertEquals(2, cardapioSalgado.size());
        assertEquals(sabor, cardapioSalgado.get(0));
        assertEquals(margherita, cardapioSalgado.get(1));
        assertEquals(3,saborRepository.findAll().size());

    }

    @Test
    @DisplayName("Requisição de cardápio de sabores doces")
    public void testCardapioDoces(){

        List<Sabor> cardapioDoce = cardapioService.cardapioSaboresDoces(estabelecimento.getId());

        assertEquals(0, cardapioDoce.size());

        Sabor margherita = saborRepository.save(Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapioDoce = cardapioService.cardapioSaboresDoces(estabelecimento.getId());

        assertEquals(0, cardapioDoce.size());

        Sabor cartola = saborRepository.save(Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapioDoce = cardapioService.cardapioSaboresDoces(estabelecimento.getId());

        assertEquals(1, cardapioDoce.size());
        assertEquals(cartola, cardapioDoce.get(0));
        assertEquals(3,saborRepository.findAll().size());

    }
    
}