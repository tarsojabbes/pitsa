package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteAlterarService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteCriarService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteExcluirService;
import com.ufcg.psoft.mercadofacil.service.cliente.ClienteListarService;
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

}
