package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.SaborAlterarDisponivelDTO;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.cardapio.CardapioService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborAlterarDisponivelService;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardapioServiceTests {

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    CardapioService cardapioService;
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborAlterarDisponivelService saborAlterarDisponivelService;

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
        assertEquals(sabor.getNomeSabor(), cardapio.get(0).getNomeSabor());
        assertEquals(sabor.getTipoSabor(), cardapio.get(0).getTipoSabor());
        assertEquals(sabor.getPrecoGrande(), cardapio.get(0).getPrecoGrande());
        assertEquals(sabor.getPrecoMedio(), cardapio.get(0).getPrecoMedio());


        Sabor margherita = saborRepository.save(Sabor.builder()

            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());


        assertEquals(2, cardapio.size());
        assertEquals(margherita.getNomeSabor(), cardapio.get(1).getNomeSabor());
        assertEquals(margherita.getTipoSabor(), cardapio.get(1).getTipoSabor());
        assertEquals(margherita.getPrecoGrande(), cardapio.get(1).getPrecoGrande());
        assertEquals(margherita.getPrecoMedio(), cardapio.get(1).getPrecoMedio());


        Sabor cartola = saborRepository.save(Sabor.builder()
            .nomeSabor("Cartola")
            .tipoSabor("Doce")
            .precoMedio(50.00)
            .precoGrande(60.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());


        assertEquals(3, cardapio.size());
        assertEquals(cartola.getNomeSabor(), cardapio.get(2).getNomeSabor());
        assertEquals(cartola.getTipoSabor(), cardapio.get(2).getTipoSabor());
        assertEquals(cartola.getPrecoGrande(), cardapio.get(2).getPrecoGrande());
        assertEquals(cartola.getPrecoMedio(), cardapio.get(2).getPrecoMedio());

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
    }

    @Test
    @DisplayName("Requisição de cardápio de sabores salgados de um estabelecimento")
    public void testCardapioSalgados(){

        List<Sabor> cardapioSalgado = cardapioService.cardapioSaboresSalgados(estabelecimento.getId());

        assertEquals(1, cardapioSalgado.size());
        assertEquals(sabor.getNomeSabor(), cardapioSalgado.get(0).getNomeSabor());
        assertEquals(sabor.getTipoSabor(), cardapioSalgado.get(0).getTipoSabor());
        assertEquals(sabor.getPrecoGrande(), cardapioSalgado.get(0).getPrecoGrande());
        assertEquals(sabor.getPrecoMedio(), cardapioSalgado.get(0).getPrecoMedio());

        Sabor margherita = saborRepository.save(Sabor.builder()

            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
            .estabelecimento(estabelecimento)
            .build());

        cardapioSalgado = cardapioService.cardapioSaboresSalgados(estabelecimento.getId());

        assertEquals(2, cardapioSalgado.size());
        assertEquals(margherita.getNomeSabor(), cardapioSalgado.get(1).getNomeSabor());
        assertEquals(margherita.getTipoSabor(), cardapioSalgado.get(1).getTipoSabor());
        assertEquals(margherita.getPrecoGrande(), cardapioSalgado.get(1).getPrecoGrande());
        assertEquals(margherita.getPrecoMedio(), cardapioSalgado.get(1).getPrecoMedio());

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
        assertEquals(cartola.getNomeSabor(), cardapioDoce.get(0).getNomeSabor());
        assertEquals(cartola.getTipoSabor(), cardapioDoce.get(0).getTipoSabor());
        assertEquals(cartola.getPrecoGrande(), cardapioDoce.get(0).getPrecoGrande());
        assertEquals(cartola.getPrecoMedio(), cardapioDoce.get(0).getPrecoMedio());
        assertEquals(3,saborRepository.findAll().size());

    }

    @Test
    @DisplayName("Quando busco um cardapio que contem um sabor indisponivel")
    public void cardapioSaborIndisponivel() {
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

        // O primeiro sabor cadastrado dever ser o último na listagem do cardápio
        saborAlterarDisponivelService.alterar(sabor.getId(), estabelecimento.getCodigoDeAcesso(), new SaborAlterarDisponivelDTO(false));

        List<Sabor> cardapio = cardapioService.cardapioCompleto(estabelecimento.getId());
        for (Sabor s : cardapio) {
            System.out.println(s.getNomeSabor());
        }

        assertEquals(3, cardapio.size());
        assertEquals(sabor.getNomeSabor(), cardapio.get(2).getNomeSabor());
        assertEquals(sabor.getTipoSabor(), cardapio.get(2).getTipoSabor());
        assertFalse(cardapio.get(2).getDisponivel());


    }
}