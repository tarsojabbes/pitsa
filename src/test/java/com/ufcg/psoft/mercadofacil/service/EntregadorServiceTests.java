package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.EntregadorGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.EntregadorPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.EntregadorNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EntregadorNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorAlterarService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorCriarService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorExcluirService;
import com.ufcg.psoft.mercadofacil.service.entregador.EntregadorListarService;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo.CARRO;
import static com.ufcg.psoft.mercadofacil.model.TipoDoVeiculo.MOTO;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EntregadorServiceTests {

    @Autowired
    EntregadorAlterarService entregadorAlterarService;

    @Autowired
    EntregadorListarService entregadorListarService;

    @Autowired
    EntregadorCriarService entregadorCriarService;

    @Autowired
    EntregadorExcluirService entregadorExcluirService;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    ModelMapper modelMapper;

    Entregador entregador;

    @BeforeEach
    public void setUp() {
        entregador = entregadorRepository.save(Entregador.builder()
                .codigoDeAcesso("123456")
                .nome("Steve Jobs")
                .corDoVeiculo("preto")
                .placaDoVeiculo("ABC1234")
                .tipoDoVeiculo(MOTO)
                .build());
    }

    @AfterEach
    public void tearDown() {
        entregadorRepository.deleteAll();
    }

    @Nested
    public class EntregadorAlterarServiceTests {
        @Test
        @DisplayName("Quando altero um atributo de um entregador que existe no banco")
        public void test01() {

            Entregador entregador = Entregador.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates")
                    .corDoVeiculo("vermelho")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(CARRO)
                    .build();
            entregadorRepository.save(entregador);

            EntregadorPostPutRequestDTO entregadorAtualizado = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            Entregador entregadorAlterado = entregadorAlterarService.alterar(entregador.getId(), entregador.getCodigoDeAcesso(), entregadorAtualizado);

            assertEquals(entregadorAtualizado.getNome(), entregadorAlterado.getNome());
            assertEquals(entregadorAtualizado.getCorDoVeiculo(), entregadorAlterado.getCorDoVeiculo());
            assertEquals(entregadorAtualizado.getPlacaDoVeiculo(), entregadorAlterado.getPlacaDoVeiculo());
            assertEquals(entregadorAtualizado.getTipoDoVeiculo(), entregadorAlterado.getTipoDoVeiculo());
        }

        @Test
        @DisplayName("Quando tento alterar um atributo de um entregador que não existe no banco")
        public void test02() {
            Long id = 999L;
            String codigoDeAcesso = "abc123";
            EntregadorPostPutRequestDTO entregadorDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            assertThrows(EntregadorNaoExisteException.class, () -> entregadorAlterarService.alterar(id, codigoDeAcesso, entregadorDTO));
        }

        @Test
        @DisplayName("Ao tentar alterar um atributo do entregador informando um código de acesso diferente do código do entregador")
        public void testAlterarEntregadorComCodigoAcessoDiferenteDoCadastrado() {
            EntregadorPostPutRequestDTO entregadorDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            String codigoDeAcessoInvalido = "senhaInvalida";

            EntregadorNaoAutorizadoException exception = assertThrows(EntregadorNaoAutorizadoException.class,
                    () -> entregadorAlterarService.alterar(entregador.getId(), codigoDeAcessoInvalido, entregadorDTO));

            assertEquals("O entregador nao possui permissao para alterar dados de outro entregador", exception.getMessage());
        }
    }

    @Nested
    public class EntregadorCriarServiceTests {
        @Test
        @DisplayName("Quando crio o primeiro entregador no banco")
        public void test01() {
            entregadorRepository.deleteAll();

            EntregadorPostPutRequestDTO entregadorDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            Entregador entregadorCriado = entregadorCriarService.criar(entregadorDTO);

            assertNotNull(entregadorCriado.getId());
            assertEquals(1, entregadorRepository.findAll().size());
            assertEquals(entregadorDTO.getNome(), entregadorCriado.getNome());
            assertEquals(entregadorDTO.getCorDoVeiculo(), entregadorCriado.getCorDoVeiculo());
            assertEquals(entregadorDTO.getPlacaDoVeiculo(), entregadorCriado.getPlacaDoVeiculo());
            assertEquals(entregadorDTO.getTipoDoVeiculo(), entregadorCriado.getTipoDoVeiculo());
        }

        @Test
        @DisplayName("Quando crio outro entregador no banco")
        public void test02() {
            EntregadorPostPutRequestDTO entregadorDTO = EntregadorPostPutRequestDTO.builder()
                    .codigoDeAcesso("123456")
                    .nome("Bill Gates da Silva")
                    .corDoVeiculo("rosa")
                    .placaDoVeiculo("DEF1234")
                    .tipoDoVeiculo(MOTO)
                    .build();

            Entregador entregadorCriado = entregadorCriarService.criar(entregadorDTO);

            assertNotNull(entregadorCriado.getId());
            assertEquals(2, entregadorRepository.findAll().size());
            assertEquals(entregadorDTO.getNome(), entregadorCriado.getNome());
            assertEquals(entregadorDTO.getCorDoVeiculo(), entregadorCriado.getCorDoVeiculo());
            assertEquals(entregadorDTO.getPlacaDoVeiculo(), entregadorCriado.getPlacaDoVeiculo());
            assertEquals(entregadorDTO.getTipoDoVeiculo(), entregadorCriado.getTipoDoVeiculo());
        }
    }

    @Nested
    public class EntregadorExcluirServiceTests {
        @Test
        @DisplayName("Quando excluo um entregador existente no banco")
        public void test01() {
            entregadorExcluirService.excluir(entregador.getId(), entregador.getCodigoDeAcesso());
            assertEquals(0, entregadorRepository.findAll().size());
        }

        @Test
        @DisplayName("Quando tento excluir um entregador inexistente no banco")
        public void test02() {
            assertThrows(EntregadorNaoExisteException.class, () -> entregadorExcluirService.excluir(entregador.getId() + 1, entregador.getCodigoDeAcesso()));
            assertEquals(1, entregadorRepository.findAll().size());
        }

        @Test
        @DisplayName("Quando tento excluir um entregador que existe no banco mas com credenciais erradas")
        public void test03() {
            assertThrows(EntregadorNaoAutorizadoException.class, () -> entregadorExcluirService.excluir(entregador.getId(), "senhaIncorreta"));
            assertEquals(1, entregadorRepository.findAll().size());
        }
    }

    @Nested
    public class EntregadorListarServiceTests {
        @Test
        @DisplayName("Quando listo todos os entregadores que existem no banco")
        public void test01() {
            EntregadorGetResponseDTO entregador1 = modelMapper.map(entregador, EntregadorGetResponseDTO.class);
            EntregadorGetResponseDTO entregador2 = modelMapper.map(
                    entregadorRepository.save(Entregador.builder()
                            .codigoDeAcesso("123456")
                            .nome("Bill Gates da Silva")
                            .corDoVeiculo("rosa")
                            .placaDoVeiculo("DEF1234")
                            .tipoDoVeiculo(MOTO)
                            .build()),
                    EntregadorGetResponseDTO.class);

            List<EntregadorGetResponseDTO> entregadores = entregadorListarService.listar(null);

            assertEquals(2, entregadores.size());
            assertTrue(entregadores.contains(entregador1));
            assertTrue(entregadores.contains(entregador2));
        }

        @Test
        @DisplayName("Quando listo um entregador que existe pelo ID")
        public void test02() {
            List<EntregadorGetResponseDTO> entregadores = entregadorListarService.listar(entregador.getId());

            assertEquals("Steve Jobs", entregadores.get(0).getNome());
            assertEquals("preto", entregadores.get(0).getCorDoVeiculo());
            assertEquals("ABC1234", entregadores.get(0).getPlacaDoVeiculo());
            assertEquals(MOTO, entregadores.get(0).getTipoDoVeiculo());
        }

        @Test
        @DisplayName("Quando listo um entregador que não existe pelo ID")
        public void test03() {
            assertThrows(EntregadorNaoExisteException.class, () -> entregadorListarService.listar(entregador.getId() + 99));
        }
    }

}
