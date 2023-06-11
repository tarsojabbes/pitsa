package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoAlterarService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoCriarService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoExcluirService;
import com.ufcg.psoft.mercadofacil.service.estabelecimento.EstabelecimentoListarService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EstabelecimentoServiceTests {
    @Autowired
    EstabelecimentoAlterarService estabelecimentoAlterarService;

    @Autowired
    EstabelecimentoCriarService estabelecimentoCriarService;

    @Autowired
    EstabelecimentoListarService estabelecimentoListarService;

    @Autowired
    EstabelecimentoExcluirService estabelecimentoExcluirService;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Nested
    public class EstabelecimentoAlterarServiceTests {

        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento A")
                    .build());
        }

        @AfterEach
        public void tearDown() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando altero um atributo de um estabelecimento que existe no banco")
        public void test01() {
            EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento B")
                    .build();
            Estabelecimento estabelecimentoAtualizado = estabelecimentoAlterarService.alterar(estabelecimento.getId(), estabelecimentoPostPutRequestDTO);
            assertEquals("Estabelecimento B", estabelecimentoAtualizado.getNome());
        }

        @Test
        @DisplayName("Quando altero um atributo de um estabelecimento que não existe no banco")
        public void test02() {
            EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO = EstabelecimentoPostPutRequestDTO.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento B")
                    .build();
            assertThrows(MercadoFacilException.class, () ->  estabelecimentoAlterarService.alterar(estabelecimento.getId() + 1, estabelecimentoPostPutRequestDTO));
        }
    }

    @Nested
    public class EstabelecimentoCriarServiceTests {
        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento A")
                    .build());
        }

        @AfterEach
        public void tearDown() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando cria o primeiro estabelecimento do banco")
        public void test01() {
            estabelecimentoRepository.deleteAll();
            EstabelecimentoPostPutRequestDTO estabelecimento1 = EstabelecimentoPostPutRequestDTO.builder().nome("Estabelecimento C").codigoDeAcesso("123456").build();

            Estabelecimento estabelecimentoCriado = estabelecimentoCriarService.criar(estabelecimento1);

            assertEquals(1, estabelecimentoRepository.findAll().size());
            assertEquals(estabelecimento1.getCodigoDeAcesso(), estabelecimentoCriado.getCodigoDeAcesso());
            assertEquals(estabelecimento1.getNome(), estabelecimentoCriado.getNome());


        }

        @Test
        @DisplayName("Quando cria um estabelecimento em banco não vazio")
        public void test02() {
            EstabelecimentoPostPutRequestDTO estabelecimento1 = EstabelecimentoPostPutRequestDTO.builder().nome("Estabelecimento C").codigoDeAcesso("123456").build();

            Estabelecimento estabelecimentoCriado = estabelecimentoCriarService.criar(estabelecimento1);

            assertEquals(2, estabelecimentoRepository.findAll().size());
            assertEquals(estabelecimento1.getCodigoDeAcesso(), estabelecimentoCriado.getCodigoDeAcesso());
            assertEquals(estabelecimento1.getNome(), estabelecimentoCriado.getNome());
        }
    }

    @Nested
    public class EstabelecimentoExcluirServiceTests {
        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento A")
                    .build());
        }

        @AfterEach
        public void tearDown() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando excluo um estabelecimento existente com sucesso e ainda restam estabelecimentos salvos")
        public void test01() {
            Estabelecimento estabelecimentoSalvo = estabelecimentoRepository.save(Estabelecimento.builder().nome("Estabelecimento 2").codigoDeAcesso("123456").build());

            estabelecimentoExcluirService.excluir(estabelecimentoSalvo.getId());

            assertEquals(1, estabelecimentoRepository.findAll().size());
            assertEquals("Estabelecimento A", estabelecimentoRepository.findAll().get(0).getNome());
            assertEquals("1234567", estabelecimentoRepository.findAll().get(0).getCodigoDeAcesso());
        }

        @Test
        @DisplayName("Quando excluo um estabelecimento existente com sucesso e ele é o único")
        public void test02() {
            estabelecimentoRepository.deleteAll();
            Estabelecimento estabelecimentoSalvo = estabelecimentoRepository.save(Estabelecimento.builder().nome("Estabelecimento 2").codigoDeAcesso("123456").build());

            estabelecimentoExcluirService.excluir(estabelecimentoSalvo.getId());

            assertEquals(0, estabelecimentoRepository.findAll().size());
        }

        @Test
        @DisplayName("Quando excluo um estabelecimento inexistente")
        public void test03() {
            Estabelecimento estabelecimentoSalvo = estabelecimentoRepository.save(Estabelecimento.builder().nome("Estabelecimento 2").codigoDeAcesso("123456").build());

            assertThrows(MercadoFacilException.class, () -> estabelecimentoExcluirService.excluir(estabelecimentoSalvo.getId() + 1));
        }
    }

    @Nested
    public class EstabelecimentoListarServiceTests {
        Estabelecimento estabelecimento;

        @BeforeEach
        public void setup() {
            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento A")
                    .build());
        }

        @AfterEach
        public void tearDown() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando listo todos os estabelecimentos do banco")
        public void test01() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
            estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("1234567")
                    .nome("Estabelecimento 1")
                    .build());
            estabelecimentoRepository.save(Estabelecimento.builder()
                    .nome("Estabelecimento 2")
                    .codigoDeAcesso("123456")
                    .build());

            List<Estabelecimento> estabelecimentoList = estabelecimentoListarService.listar(null);

            assertEquals(2, estabelecimentoList.size());
        }

        @Test
        @DisplayName("Quando listo um estabelecimento que existe pelo ID")
        public void test02() {
            Estabelecimento estabelecimentoSalvo = estabelecimentoRepository.save(Estabelecimento.builder().nome("Estabelecimento 2").codigoDeAcesso("123456").build());

            List<Estabelecimento> estabelecimentoList = estabelecimentoListarService.listar(estabelecimentoSalvo.getId());

            assertEquals("Estabelecimento 2", estabelecimentoList.get(0).getNome());
            assertEquals("123456", estabelecimentoList.get(0).getCodigoDeAcesso());
        }

        @Test
        @DisplayName("Quando listo um estabelecimento que não existe pelo ID")
        public void test03() {
            Estabelecimento estabelecimentoSalvo = estabelecimentoRepository.save(Estabelecimento.builder().nome("Estabelecimento 2").codigoDeAcesso("123456").build());

            assertThrows(MercadoFacilException.class, () -> estabelecimentoListarService.listar(estabelecimentoSalvo.getId() + 1));


        }
    }
}
