package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.SaborDisponivelException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.cliente.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClienteServiceTests {

    @Autowired
    ClienteAlterarService clienteAlterarService;

    @Autowired
    ClienteListarService clienteListarService;

    @Autowired
    ClienteCriarService clienteCriarService;

    @Autowired
    ClienteExcluirService clienteExcluirService;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ClienteDemonstrarInteresseService clienteDemonstrarInteresseService;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    Cliente cliente;

    @BeforeEach
    public void setUp() {
        cliente = clienteRepository.save(Cliente.builder()
                .codigoDeAcesso("123456")
                .nome("Tarso Jabbes")
                .endereco("Rua Aprigio Veloso")
                .build());
    }

    @AfterEach
    public void tearDown() {
        clienteRepository.deleteAll();
    }


    @Nested
    public class ClienteAlterarServiceTests {
        @Test
        @DisplayName("Quando altero um atributo de um cliente que existe no banco")
        public void test01() {

            Cliente cliente = Cliente.builder()
                    .nome("João")
                    .endereco("Rua A, 123")
                    .codigoDeAcesso("123456")
                    .build();
            clienteRepository.save(cliente);

            ClientePostPutRequestDTO clienteAtualizado = ClientePostPutRequestDTO.builder()
                    .nome("João da Silva")
                    .endereco("Rua B, 456")
                    .codigoDeAcesso("123456")
                    .build();

            Cliente clienteAlterado = clienteAlterarService.alterar(cliente.getId(), cliente.getCodigoDeAcesso(), clienteAtualizado);

            assertEquals(clienteAtualizado.getNome(), clienteAlterado.getNome());
            assertEquals(clienteAtualizado.getEndereco(), clienteAlterado.getEndereco());
        }

        @Test
        @DisplayName("Quando tento alterar um atributo de um cliente que não existe no banco")
        public void test02() {
            Long id = 999L;
            String codigoDeAcesso = "abc123";
            ClientePostPutRequestDTO clienteDTO = ClientePostPutRequestDTO.builder()
                    .nome("João da Silva")
                    .endereco("Rua dos Bobos, 0")
                    .codigoDeAcesso("abc123")
                    .build();

            assertThrows(ClienteNaoExisteException.class, () -> {
                clienteAlterarService.alterar(id, codigoDeAcesso, clienteDTO);
            });
        }

        @Test
        @DisplayName("Ao tentar alterar um atributo do cliente informando um código de acesso diferente do código do cliente")
        public void testAlterarClienteComCodigoAcessoDiferenteDoCadastrado() {
            ClientePostPutRequestDTO clienteAlterado = ClientePostPutRequestDTO.builder()
                    .nome("Tarso Jabbes")
                    .endereco("Av. B, 456")
                    .codigoDeAcesso("123456")
                    .build();

            String codigoDeAcessoInvalido = "senhaInvalida";

            ClienteNaoAutorizadoException exception = assertThrows(ClienteNaoAutorizadoException.class,
                    () -> clienteAlterarService.alterar(cliente.getId(), codigoDeAcessoInvalido, clienteAlterado));

            assertEquals("O cliente nao possui permissao para alterar dados de outro cliente", exception.getMessage());
        }
    }

    @Nested
    public class ClienteCriarServiceTests {
        @Test
        @DisplayName("Quando crio o primeiro cliente no banco")
        public void test01() {
            clienteRepository.deleteAll();

            ClientePostPutRequestDTO clienteDTO = ClientePostPutRequestDTO.builder()
                    .nome("Fulano de tal")
                    .codigoDeAcesso("123456")
                    .endereco("Rua A, 123")
                    .build();

            Cliente clienteCriado = clienteCriarService.criar(clienteDTO);

            assertNotNull(clienteCriado.getId());
            assertEquals(clienteDTO.getNome(), clienteCriado.getNome());
            assertEquals(clienteDTO.getCodigoDeAcesso(), clienteCriado.getCodigoDeAcesso());
            assertEquals(clienteDTO.getEndereco(), clienteCriado.getEndereco());
        }

        @Test
        @DisplayName("Quando crio outro cliente no banco")
        public void test02() {
            ClientePostPutRequestDTO clienteDTO = ClientePostPutRequestDTO.builder()
                    .nome("Fulano de tal")
                    .codigoDeAcesso("123456")
                    .endereco("Rua A, 123")
                    .build();

            Cliente clienteCriado = clienteCriarService.criar(clienteDTO);

            assertNotNull(clienteCriado.getId());
            assertEquals(2, clienteRepository.findAll().size());
            assertEquals(clienteDTO.getNome(), clienteCriado.getNome());
            assertEquals(clienteDTO.getCodigoDeAcesso(), clienteCriado.getCodigoDeAcesso());
            assertEquals(clienteDTO.getEndereco(), clienteCriado.getEndereco());
        }
}

    @Nested
    public class ClienteExcluirServiceTests {
        @Test
        @DisplayName("Quando excluo um cliente existente no banco")
        public void test01() {
            clienteExcluirService.excluir(cliente.getId(), cliente.getCodigoDeAcesso());
            assertEquals(0, clienteRepository.findAll().size());
        }

        @Test
        @DisplayName("Quando tento excluir um cliente inexistente no banco")
        public void test02() {
            assertThrows(ClienteNaoExisteException.class, () -> clienteExcluirService.excluir(cliente.getId() + 1, cliente.getCodigoDeAcesso()));

        }

        @Test
        @DisplayName("Quando tento excluir um cliente que existe no banco mas com credenciais erradas")
        public void test03() {
            assertThrows(ClienteNaoAutorizadoException.class, () -> clienteExcluirService.excluir(cliente.getId(), "senhaIncorreta"));
        }
    }

    @Nested
    public class ClienteListarServiceTests {
        @Test
        @DisplayName("Quando listo todos os clientes que existem no banco")
        public void test01() {
            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Fulaninho")
                    .endereco("Rua do Bobo, 0")
                    .codigoDeAcesso("meuCodigoDeAcesso")
                    .build());

            List<ClienteGetResponseDTO> clientes = clienteListarService.listar(null);

            assertEquals(2, clientes.size());
        }

        @Test
        @DisplayName("Quando listo um cliente que existe pelo ID")
        public void test02() {
            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Fulaninho")
                    .endereco("Rua do Bobo, 0")
                    .codigoDeAcesso("meuCodigoDeAcesso")
                    .build());

            List<ClienteGetResponseDTO> clientes = clienteListarService.listar(cliente1.getId());

            assertEquals("Fulaninho", clientes.get(0).getNome());
            assertEquals("Rua do Bobo, 0", clientes.get(0).getEndereco());
        }

        @Test
        @DisplayName("Quando listo um cliente que não existe pelo ID")
        public void test03() {
            assertThrows(ClienteNaoExisteException.class, () -> clienteListarService.listar(cliente.getId() + 99));
        }
    }

    @Nested
    class DemonstrarInteresseServiceTests{
        Estabelecimento estabelecimento;
        Cliente cliente;
        Sabor sabor;

        @BeforeEach
        void setup(){
            cliente = clienteRepository.save(Cliente.builder()
                    .codigoDeAcesso("12345")
                    .nome("Sabrina Barbosa")
                    .endereco("Avenida Ipiranga com Avenida São João")
                    .build());

            estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                    .codigoDeAcesso("123456")
                    .nome("Estabelecimento Zero")
                    .build());

            sabor = saborRepository.save(
                    Sabor.builder()
                            .nomeSabor("Sabor Zero")
                            .tipoSabor("Salgado")
                            .precoMedio(20.00)
                            .precoGrande(40.00)
                            .estabelecimento(estabelecimento)
                            .build());
        }

        @AfterEach
        public void tearDown() {
            saborRepository.deleteAll();
            estabelecimentoRepository.deleteAll();
            clienteRepository.deleteAll();
        }

        @Test
        public void testDemonstrarInteressePorSabor_CodigoAcessoInvalido_ExceptionLancada() {
            String codigoDeAcessoInvalido = "senhaInvalida";

            assertThrows(ClienteNaoAutorizadoException.class,
                    () -> clienteDemonstrarInteresseService.demonstrarInteressePorSabor(codigoDeAcessoInvalido, cliente.getId(), sabor.getId()));
        }

        @Test
        public void testDemonstrarInteressePorSabor_Disponível() {

            assertThrows(SaborDisponivelException.class,
                    () -> clienteDemonstrarInteresseService.demonstrarInteressePorSabor("12345", cliente.getId(), sabor.getId()));
        }

        @Test
        public void testDemonstrarInteressePorSabor_ClienteNaoExiste() {
            clienteRepository.deleteAll();

            assertThrows(ClienteNaoExisteException.class,
                    () -> clienteDemonstrarInteresseService.demonstrarInteressePorSabor("12345", 2L, sabor.getId()));
        }

        @Test
        public void testDemonstrarInteressePorSabor_SaborNaoExiste() {

            assertThrows(SaborNaoExisteException.class,
                    () -> clienteDemonstrarInteresseService.demonstrarInteressePorSabor("12345", cliente.getId(), 2L));
        }

        @Test
        public void testDemonstrarInteressePorSabor_Indisponivel() {
            sabor.setDisponivel(false);
            saborRepository.save(sabor);

            clienteDemonstrarInteresseService.demonstrarInteressePorSabor("12345", cliente.getId(), sabor.getId());

            Sabor saborAtualizado = saborRepository.findById(sabor.getId()).orElseThrow(SaborNaoExisteException::new);
            List<Long> interessados = saborAtualizado.getInteressados();

            assertEquals(interessados.get(0), cliente.getId());
        }
    }

}
