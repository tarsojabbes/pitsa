package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborAlterarService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborCriarService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborExcluirService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborListarService;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SaborServiceTests {
    @Autowired
    SaborAlterarService saborAlterarService;

    @Autowired
    SaborCriarService saborCriarService;

    @Autowired
    SaborListarService saborListarService;

    @Autowired
    SaborExcluirService saborExcluirService;

    @Autowired
    SaborRepository saborRepository;

    @Nested
    public class SaborAlterarServiceTests {

        Sabor sabor;

        @BeforeEach
        public void setup() {
            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Alteração de atributo de um sabor existente")
        public void testAlteraAtributoSaborExistente() {

            SaborPostPutRequestDTO saborPostPutRequestDTO = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa Acebolada")
                    .build();

            Sabor saborAtualizado = saborAlterarService.alterar(sabor.getId(), saborPostPutRequestDTO);

            assertEquals("Calabresa Acebolada", saborAtualizado.getNomeSabor());
        }

        @Test
        @DisplayName("Alteração de atributo de um sabor inexistente")
        public void testAlteraAtributoSaborInexistente() {

            SaborPostPutRequestDTO saborPostPutRequestDTO = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Atum")
                    .build();

            assertThrows(MercadoFacilException.class, () ->  saborAlterarService.alterar(sabor.getId() + 1, saborPostPutRequestDTO));
        }
    }

    @Nested
    public class SaborCriarServiceTests {

        Sabor sabor;

        @BeforeEach
        public void setup() {
            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Criação do primeiro sabor no banco")
        public void testCriaSaborBancoVazio() {

            saborRepository.deleteAll();

            SaborPostPutRequestDTO novoSabor = SaborPostPutRequestDTO.builder()
                .nomeSabor("Atum")
                .tipoSabor("Salgado")
                .precoMedio(55.00)
                .precoGrande(65.00)
            .build();

            Sabor saborCriado = saborCriarService.criar(novoSabor);

            assertEquals(1, saborRepository.findAll().size());
            assertEquals(novoSabor.getNomeSabor(),saborCriado.getNomeSabor());
            assertEquals(novoSabor.getTipoSabor(),saborCriado.getTipoSabor());
            assertEquals(novoSabor.getPrecoMedio(),saborCriado.getPrecoMedio());
            assertEquals(novoSabor.getPrecoGrande(),saborCriado.getPrecoGrande());
        }

        @Test
        @DisplayName("Criação de um sabor em banco já populado")
        public void testCriaSaborBancoNaoVazio() {

            SaborPostPutRequestDTO novoSaborBuild = SaborPostPutRequestDTO.builder()
                .nomeSabor("Atum")
                .tipoSabor("Salgado")
                .precoMedio(55.00)
                .precoGrande(65.00)
            .build();

            Sabor novoSabor = saborCriarService.criar(novoSaborBuild);

            List<Sabor> lista = saborRepository.findAll();

            assertEquals(2, lista.size());

            assertEquals(sabor,lista.get(0));
            assertEquals(sabor.getNomeSabor(),lista.get(0).getNomeSabor());
            assertEquals(sabor.getTipoSabor(),lista.get(0).getTipoSabor());
            assertEquals(sabor.getPrecoMedio(),lista.get(0).getPrecoMedio());
            assertEquals(sabor.getPrecoGrande(),lista.get(0).getPrecoGrande());

            assertEquals(novoSabor,lista.get(1));
            assertEquals(novoSabor.getNomeSabor(),lista.get(1).getNomeSabor());
            assertEquals(novoSabor.getTipoSabor(),lista.get(1).getTipoSabor());
            assertEquals(novoSabor.getPrecoMedio(),lista.get(1).getPrecoMedio());
            assertEquals(novoSabor.getPrecoGrande(),lista.get(1).getPrecoGrande());
        }
    }

    @Nested
    public class SaborExcluirServiceTests {

        Sabor sabor;

        @BeforeEach
        public void setup() {
            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Repositório/BD contem mais de um sabor salvo, e um é excluído")
        public void testSaborExcluidoDeRepositorioComMultiplosSaboresSalvos() {

            Sabor saborSalvo = saborRepository.save(Sabor
                .builder()
                    .nomeSabor("Margherita")
                    .tipoSabor("Salgado")
                    .precoMedio(45.00)
                    .precoGrande(55.00)
                .build());

            assertEquals(2, saborRepository.findAll().size());

            saborExcluirService.excluir(saborSalvo.getId());

            assertEquals(1, saborRepository.findAll().size());
            assertEquals(sabor, saborRepository.findAll().get(0));
            assertEquals(sabor.getNomeSabor(), saborRepository.findAll().get(0).getNomeSabor());
            assertEquals(sabor.getTipoSabor(), saborRepository.findAll().get(0).getTipoSabor());
            assertEquals(sabor.getPrecoMedio(), saborRepository.findAll().get(0).getPrecoMedio());
            assertEquals(sabor.getPrecoGrande(), saborRepository.findAll().get(0).getPrecoGrande());
        }

        @Test
        @DisplayName("O único sabor salvo no BD é excluído")
        public void testExclusaoDoUnicoSaborSalvo() {
            
            saborExcluirService.excluir(sabor.getId());

            assertEquals(0, saborRepository.findAll().size());
        }

        @Test
        @DisplayName("Tentativa de exclusão de um sabor inexistente")
        public void testTentaExcluirSaborInexistente() {

            assertEquals(1, saborRepository.findAll().size());
            assertThrows(MercadoFacilException.class, () -> saborExcluirService.excluir(sabor.getId() + 1L));
            assertEquals(1, saborRepository.findAll().size());

        }
    }

    @Nested
    public class SaborListarServiceTests {

        Sabor sabor;

        @BeforeEach
        public void setup() {
            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Listagem de todos os sabores registrados no BD")
        public void testListaTodosSabores() {

            List<Sabor> saborList = saborListarService.listar(null);
            assertEquals(2, saborList.size());

            Sabor novoSabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Margherita")
                .tipoSabor("Salgado")
                .precoMedio(45.00)
                .precoGrande(55.00)
            .build());

            saborList = saborListarService.listar(null);

            assertEquals(2, saborList.size());
            assertEquals(sabor, saborList.get(0));
            assertEquals(novoSabor, saborList.get(1));
        }

        @Test
        @DisplayName("Lista sabor existente por ID")
        public void testListaSaborExistentePorID() {

            Sabor novoSabor = saborRepository.save(Sabor.builder()
            .nomeSabor("Margherita")
            .tipoSabor("Salgado")
            .precoMedio(45.00)
            .precoGrande(55.00)
        .build());

            List<Sabor> saborList = saborListarService.listar(novoSabor.getId());

            assertEquals("Margherita", saborList.get(0).getNomeSabor());
            assertEquals("Salgada", saborList.get(0).getTipoSabor());
            assertEquals(45.00, saborList.get(0).getPrecoMedio());
            assertEquals(55.00, saborList.get(0).getPrecoGrande());
        }

        @Test
        @DisplayName("Tenta listar um sabor inexistente por ID")
        public void testListaInexistentePorID() {

            assertThrows(MercadoFacilException.class, () -> saborListarService.listar(sabor.getId() + 1L));

        }
    }
}