package com.ufcg.psoft.mercadofacil.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Pedidos")
public class PedidoV1ControllerTests {
    
    @Autowired
    MockMvc driver;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Pedido pedido;
    List<Pizza> pizzas;
    Estabelecimento estabelecimento;
    Cliente cliente;

    @BeforeEach
    void setup(){

        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
            .nome("Jipao")
            .codigoDeAcesso("123456")
            .associacoes(new ArrayList<>())
        .build());

        cliente = clienteRepository.save(Cliente.builder()
            .nome("Joao")
            .endereco("Rua 1")
            .codigoDeAcesso("123456")
            .pedidos(new ArrayList<>())
        .build());

        pedido = pedidoRepository.save(Pedido.builder()
            .id(cliente.getId())
            .cliente(cliente)
            .pizzasPedido(pizzas)
            .endereco("abc")
            .pizzasPedido(duasCalabresasGrandesCreator())
            .meioDePagamento("PIX")
        .build()
        );
    }
    
    @AfterEach
    void tearDown(){
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();

    }

    private List<Pizza> duasCalabresasGrandesCreator(){
        Sabor sabor = new Sabor(1L,"Calabresa", "Salgada", 50.00, 60.00, estabelecimento);
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);

        Pizza duasCalabresasGrandes = new Pizza(sabores,false,sabor.getPrecoGrande(),2);
        List<Pizza> novoPedido = new ArrayList<Pizza>();
        novoPedido.add(duasCalabresasGrandes);

        return novoPedido;
    }

    private List<Pizza> bandoDeGordosEsquisitos(){
        Sabor atum = new Sabor(2L,"Atum","Salgada",60.00,70.00,estabelecimento);
        Sabor pacoca = new Sabor(4L,"Pacoca","Doce",50.00,60.00,estabelecimento);
        List<Sabor> sabores = new ArrayList<Sabor>();
        sabores.add(atum);
        sabores.add(pacoca);

        List<Pizza> pizzaMaldita = new ArrayList<>();
        Pizza p = new Pizza(sabores, true, (atum.getPrecoGrande()+pacoca.getPrecoGrande())/2,1);
        pizzaMaldita.add(p);

        return pizzaMaldita;
    }

    @Nested
    class PedidoPostTests{

        @Test
        @Transactional
        @DisplayName("Criação de Pedido válido")
        public void testCriaPedidoValido() throws Exception{

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                .nome("Jose Lesinho")
                .endereco("Rua Sem Nome S/N")
                .codigoDeAcesso("admin123")
                .pedidos(new ArrayList<Pedido>())
                .build());

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                .idCLiente(cliente2.getId())
                .pizzas(bandoDeGordosEsquisitos())
                .enderecoAlternativo("")
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente2.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals(pedidoDTO.getIdCLiente(),pedidoSalvo.getCliente().getId());
            assertEquals(2,pedidoRepository.findAll().size());

        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (lista de pizzas vazia)")
        public void testCriaPedidoInvalidoMapVazio() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .idCLiente(cliente.getId())
                .pizzas(new ArrayList<Pizza>())
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode estar vazia.")
                                     || error.getErrors().contains("A listagem de pedidos nao pode ser null.");
            
            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (map de pizzas null)")
        public void testCriaPedidoInvalidoMapNull() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .idCLiente(cliente.getId())
                .pizzas(null)
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode estar vazia.")
                                     || error.getErrors().contains("A listagem de pedidos nao pode ser null.");
            
            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (idCliente inválida - null)")
        public void testCriaPedidoInvalidoIdClienteInvalidaNull() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .idCLiente(null)
                .pizzas(pizzas)
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A id do cliente nao deve ser nula.")
                                     || error.getErrors().contains("A id do cliente deve ser maior que zero.");
            
            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (idCliente inválida - menor que 1L)")
        public void testCriaPedidoInvalidoIdClienteInvalidaNaoPositiva() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .idCLiente(-2L)
                .pizzas(pizzas)
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A id do cliente nao deve ser nula.")
                                     || error.getErrors().contains("A id do cliente deve ser maior que zero.");
            
            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (código de acesso inválido - null)")
        public void testCriaPedidoInvalidoCANull() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(null)
                .idCLiente(cliente.getId())
                .pizzas(pizzas)
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Codigo de acesso nao pode estar em branco.")
                                     || error.getErrors().contains("Codigo de acesso nao pode ser null.")
                                     || error.getErrors().contains("Codigo de acesso nao pode ser vazio.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (código de acesso inválido - vazio)")
        public void testCriaPedidoInvalidoCAVazio() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso("")
                .idCLiente(cliente.getId())
                .pizzas(pizzas)
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Codigo de acesso nao pode estar em branco.")
                                     || error.getErrors().contains("Codigo de acesso nao pode ser null.")
                                     || error.getErrors().contains("Codigo de acesso nao pode ser vazio.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Tenta criar um pedido inválido (código de acesso inválido - em branco)")
        public void testCriaPedidoInvalido() throws Exception{

            pedidoRepository.deleteAll();

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso("     ")
                .idCLiente(cliente.getId())
                .pizzas(pizzas)
                .build();
            
            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("Codigo de acesso nao pode estar em branco.")
                                     || error.getErrors().contains("Codigo de acesso nao pode ser null.")
                                     || error.getErrors().contains("Codigo de acesso nao pode ser vazio.");

            assertTrue(errorStringTester);
        }


        @Test
        @Transactional
        @DisplayName("Criação de Pedido sem endereço alternativo")
        public void testCriaPedidoSemEndereco() throws Exception{

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Jose Lesinho")
                    .endereco("Rua Sem Nome S/N")
                    .codigoDeAcesso("admin123")
                    .pedidos(new ArrayList<Pedido>())
                    .build());

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                    .codigoDeAcesso(cliente2.getCodigoDeAcesso())
                    .idCLiente(cliente2.getId())
                    .pizzas(bandoDeGordosEsquisitos())
                    .build();

            String jsonString = objectMapper.writeValueAsString(pedidoDTO);

            String respostaJson = driver.perform(post("/v1/pedidos"+"?codigoDeAcesso="+cliente2.getCodigoDeAcesso())
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson, Pedido.class);

            Pedido pedidoSalvo = pedidoRepository.findById(resposta.getId()).get();

            assertNotNull(pedidoSalvo);
            assertEquals("Rua Sem Nome S/N", resposta.getEndereco());
            assertEquals(pedidoDTO.getIdCLiente(),pedidoSalvo.getCliente().getId());
            assertEquals(2,pedidoRepository.findAll().size());

        }

    }

    @Nested
    class PedidoGetTests{

        @Test
        @DisplayName("Busca o pedido atual de um cliente (só deve existir um pedido por cliente)")
        public void testBuscaPedidoAtual() throws Exception{

            String responseJsonString = driver.perform(get("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resultado = objectMapper.readValue(responseJsonString, Pedido.class);

            assertEquals(pedido.getId(),resultado.getId());
            assertEquals(pedido.getCliente(),resultado.getCliente());
            assertEquals(pedido.getEndereco(), resultado.getEndereco());
        }

        @Test
        @DisplayName("Busca um pedido inválido")
        public void testBuscaPedidoInvalido() throws Exception{

            MvcResult resultado = driver.perform(get("/v1/pedidos/" + pedido.getId()+1L)
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

            assertTrue(resultado.getResolvedException() instanceof Exception);

        }

    }

    @Nested
    class PedidoPutTests{

        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumentos válidos (endereço)")
        public void testAtualizacaoValidaEndereco() throws Exception{

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
            .codigoDeAcesso(cliente.getCodigoDeAcesso())
            .idCLiente(cliente.getId())
            .enderecoAlternativo("Avenida Augusto dos Anjos 44")
            .pizzas(pedido.getPizzasPedido())
            .build();

            String respostaJson = driver.perform(put("/v1/pedidos/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson,Pedido.class);

            assertEquals("Avenida Augusto dos Anjos 44", resposta.getEndereco());
        }

        /*Atualiza o endereço original (composto em cliente) por uma string
        válida, e realiza nova atualização com uma string inválida (vazia,em
        branco, ou null) para verificar se o endereço volta a ser o original*/
        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumentos inválidos (endereço).")
        public void testValidacaoEndereco() throws Exception{

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
            .codigoDeAcesso(cliente.getCodigoDeAcesso())
            .idCLiente(cliente.getId())
            .enderecoAlternativo("Avenida Augusto dos Anjos 44")
            .pizzas(pedido.getPizzasPedido())
            .build();

            String respostaJson = driver.perform(put("/v1/pedidos/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson,Pedido.class);

            assertEquals("Avenida Augusto dos Anjos 44", resposta.getEndereco());

            pedidoDTO = PedidoPostPutRequestDTO.builder()
            .codigoDeAcesso(cliente.getCodigoDeAcesso())
            .idCLiente(cliente.getId())
            .enderecoAlternativo(null)
            .pizzas(pedido.getPizzasPedido())
            .build();

            respostaJson = driver.perform(put("/v1/pedidos/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            resposta = objectMapper.readValue(respostaJson,Pedido.class);

            assertEquals(cliente.getEndereco(),resposta.getEndereco());
        }

        @Test
        @Transactional
        @DisplayName("Atualização de um pedido com argumentos válidos (pizzas do pedido)")
        public void testAtualizacaoValidaListaPizzas() throws Exception{

            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
            .codigoDeAcesso(cliente.getCodigoDeAcesso())
            .idCLiente(cliente.getId())
            .pizzas(bandoDeGordosEsquisitos())
            .build();

            String respostaJson = driver.perform(put("/v1/pedidos/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Pedido resposta = objectMapper.readValue(respostaJson,Pedido.class);

            assertEquals(bandoDeGordosEsquisitos(), resposta.getPizzasPedido());
        }

        @Test
        @DisplayName("Atualização de um pedido com argumento inválido (map de pizzas null)")
        public void testAtualizacaoInvalidaPizzasNull() throws Exception{
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .idCLiente(cliente.getId())
                .pizzas(null)
                .build();

            String respostaJson = driver.perform(put("/v1/pedidos/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode ser null.")
                                     || error.getErrors().contains("A listagem de pedidos nao pode estar vazia.");

            assertTrue(errorStringTester);
        }

        @Test
        @DisplayName("Atualização de um pedido com argumento inválido (map de pizzas vazia)")
        public void testAtualizacaoInvalidaPizzasVazia() throws Exception{
            PedidoPostPutRequestDTO pedidoDTO = PedidoPostPutRequestDTO.builder()
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .idCLiente(cliente.getId())
                .pizzas(new ArrayList<Pizza>())
                .build();

            String respostaJson = driver.perform(put("/v1/pedidos/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringTester = error.getErrors().contains("A listagem de pedidos nao pode ser null.")
                                     || error.getErrors().contains("A listagem de pedidos nao pode estar vazia.");

            assertTrue(errorStringTester);
        }

    }

    @Nested
    class PedidoDeleteTests{

        @Test
        @DisplayName("Exclusão por ID de um pedido existente")
        public void testExclusaoPorIDValida() throws Exception {

            driver.perform(delete("/v1/pedidos/" + pedido.getId() + "?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(0, pedidoRepository.findAll().size());
        }

        @Test
        @DisplayName("Exclusao por ID de um pedido inválido (potencial pedido de outro cliente)")
        public void testExclusaoPorIDInvalida() throws Exception {

            driver.perform(delete("/v1/pedidos/" + (pedido.getId() + 1L))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertEquals(1, pedidoRepository.findAll().size());
        }
    }
}
