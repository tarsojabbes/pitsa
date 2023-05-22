package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
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


    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    public class SaborAlterarServiceTests {

        Sabor sabor;

        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {

            estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder()
                    .nome("Jipao")
                    .codigoDeAcesso("12345")
                .build());


            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Alteração de atributo de um sabor existente")
        public void testAlteraAtributoSaborExistente() {

            SaborPostPutRequestDTO saborPostPutRequestDTO = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Calabresa Acebolada")
                    .build();
            Sabor saborAtualizado = saborAlterarService.alterar(sabor.getId(), estabelecimento.getCodigoDeAcesso(), saborPostPutRequestDTO);

            assertEquals("Calabresa Acebolada", saborAtualizado.getNomeSabor());
        }

        @Test
        @DisplayName("Alteração de atributo de um sabor inexistente")
        public void testAlteraAtributoSaborInexistente() {

            SaborPostPutRequestDTO saborPostPutRequestDTO = SaborPostPutRequestDTO.builder()
                    .nomeSabor("Atum")
                    .build();

  assertThrows(MercadoFacilException.class, () ->  saborAlterarService.alterar(sabor.getId() + 1, estabelecimento.getCodigoDeAcesso(), saborPostPutRequestDTO));

        }
    }

    @Nested
    public class SaborCriarServiceTests {

        Sabor sabor;
        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {

            estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder()
                    .nome("Jipao")
                    .codigoDeAcesso("12345")
                .build());

            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Criação do primeiro sabor no banco")
        public void testCriaSaborBancoVazio() {

            saborRepository.deleteAll();

            SaborPostPutRequestDTO novoSabor = SaborPostPutRequestDTO.builder()
                .nomeSabor("Atum")
                .tipoSabor("Salgado")
                .precoMedio(55.00)
                .precoGrande(65.00)
                    .idEstabelecimento(estabelecimento.getId())
                .estabelecimento(estabelecimento)
                .build();

            Sabor saborCriado = saborCriarService.criar(estabelecimento.getCodigoDeAcesso(), novoSabor);

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
                    .idEstabelecimento(estabelecimento.getId())
                .estabelecimento(estabelecimento)
            .build();

            Sabor novoSabor = saborRepository.save(saborCriarService.criar(estabelecimento.getCodigoDeAcesso(), novoSaborBuild));

            List<Sabor> lista = saborRepository.findAll();

            assertEquals(2, lista.size());

            assertEquals(sabor.getNomeSabor(),lista.get(0).getNomeSabor());
            assertEquals(sabor.getTipoSabor(),lista.get(0).getTipoSabor());
            assertEquals(sabor.getPrecoMedio(),lista.get(0).getPrecoMedio());
            assertEquals(sabor.getPrecoGrande(),lista.get(0).getPrecoGrande());

            assertEquals(novoSabor.getNomeSabor(),lista.get(1).getNomeSabor());
            assertEquals(novoSabor.getTipoSabor(),lista.get(1).getTipoSabor());
            assertEquals(novoSabor.getPrecoMedio(),lista.get(1).getPrecoMedio());
            assertEquals(novoSabor.getPrecoGrande(),lista.get(1).getPrecoGrande());
        }
    }

    @Nested
    public class SaborExcluirServiceTests {

        Sabor sabor;

        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {

            estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder()
                    .nome("Jipao")
                    .codigoDeAcesso("12345")
                .build());

            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Repositório/BD contem mais de um sabor salvo, e um é excluído")
        public void testSaborExcluidoDeRepositorioComMultiplosSaboresSalvos() {


            Sabor saborSalvo = saborRepository.save(Sabor.builder()
                    .nomeSabor("Margherita")
                    .tipoSabor("Salgado")
                    .precoMedio(45.00)
                    .precoGrande(55.00)
                    .estabelecimento(estabelecimento)
                .build());

            assertEquals(2, saborRepository.findAll().size());

            saborExcluirService.excluir(saborSalvo.getId(), estabelecimento.getCodigoDeAcesso());

            assertEquals(1, saborRepository.findAll().size());
            assertEquals(sabor.getNomeSabor(), saborRepository.findAll().get(0).getNomeSabor());
            assertEquals(sabor.getTipoSabor(), saborRepository.findAll().get(0).getTipoSabor());
            assertEquals(sabor.getPrecoMedio(), saborRepository.findAll().get(0).getPrecoMedio());
            assertEquals(sabor.getPrecoGrande(), saborRepository.findAll().get(0).getPrecoGrande());
        }

        @Test
        @DisplayName("O único sabor salvo no BD é excluído")
        public void testExclusaoDoUnicoSaborSalvo() {

            saborExcluirService.excluir(sabor.getId(), estabelecimento.getCodigoDeAcesso());

            assertEquals(0, saborRepository.findAll().size());
        }

        @Test
        @DisplayName("Tentativa de exclusão de um sabor inexistente")
        public void testTentaExcluirSaborInexistente() {

            assertEquals(1, saborRepository.findAll().size());

            assertThrows(MercadoFacilException.class, () -> saborExcluirService.excluir(sabor.getId() + 1L, estabelecimento.getCodigoDeAcesso()));

            assertEquals(1, saborRepository.findAll().size());

        }
    }

    @Nested
    public class SaborListarServiceTests {

        Sabor sabor;

        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {

            estabelecimento = estabelecimentoRepository.save(
                Estabelecimento.builder()
                    .nome("Jipao")
                    .codigoDeAcesso("12345")
                .build());

            sabor = saborRepository.save(
                Sabor.builder()
                    .nomeSabor("Calabresa")
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
        @DisplayName("Listagem de todos os sabores registrados no BD")
        public void testListaTodosSabores() {

            List<Sabor> saborList = saborListarService.listar(null, estabelecimento.getId());
            assertEquals(1, saborList.size());

            Sabor novoSabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Margherita")
                .tipoSabor("Salgado")
                .precoMedio(45.00)
                .precoGrande(55.00)
                .estabelecimento(estabelecimento)
            .build());

            saborList = saborListarService.listar(null, estabelecimento.getId());

            assertEquals(2, saborList.size());
            assertEquals(sabor.getNomeSabor(), saborList.get(0).getNomeSabor());
            assertEquals(sabor.getTipoSabor(), saborList.get(0).getTipoSabor());
            assertEquals(sabor.getPrecoGrande(), saborList.get(0).getPrecoGrande());
            assertEquals(sabor.getPrecoMedio(), saborList.get(0).getPrecoMedio());

            assertEquals(novoSabor.getNomeSabor(), saborList.get(1).getNomeSabor());
            assertEquals(novoSabor.getTipoSabor(), saborList.get(1).getTipoSabor());
            assertEquals(novoSabor.getPrecoGrande(), saborList.get(1).getPrecoGrande());
            assertEquals(novoSabor.getPrecoMedio(), saborList.get(1).getPrecoMedio());
        }

        @Test
        @DisplayName("Lista sabor existente por ID")
        public void testListaSaborExistentePorID() {



            Sabor novoSabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Margherita")
                .tipoSabor("Salgado")
                .precoMedio(45.00)
                .precoGrande(55.00)
                .estabelecimento(estabelecimento)
            .build());

            List<Sabor> saborList = saborListarService.listar(novoSabor.getId(), null);

            assertEquals("Margherita", saborList.get(0).getNomeSabor());
            assertEquals("Salgado", saborList.get(0).getTipoSabor());
            assertEquals(45.00, saborList.get(0).getPrecoMedio());
            assertEquals(55.00, saborList.get(0).getPrecoGrande());
        }

        @Test
        @DisplayName("Tenta listar um sabor inexistente por ID")
        public void testListaInexistentePorID() {


            assertThrows(MercadoFacilException.class, () -> saborListarService.listar(null, sabor.getId() + 1L));

        }
    }
}