package com.ufcg.psoft.mercadofacil.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.mercadofacil.dto.AcompanhamentoPedidoDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de acompanhamento de Pedidos")
public class AcompanhamentoPedidoV1ControllerTests {

    @Autowired
    MockMvc driver;
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    AcompanhamentoPedidoV1Controller acompanhamentoPedidoV1Controller;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    Pedido pedido;

    Estabelecimento estabelecimento;

    Cliente cliente;

    List<Pizza> pizzas;

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
                .build());

        Sabor sabor = saborRepository.save(Sabor.builder()
        .nomeSabor("Calabresa")
        .tipoSabor("salgado")
        .precoGrande(60.00)
        .precoMedio(50.00)
        .estabelecimento(estabelecimento)
        .build());
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);
        Pizza duasCalabresasGrandes = new Pizza(sabores, false, true, 2);
        List<Pizza> novoPedido = new ArrayList<>();
        novoPedido.add(duasCalabresasGrandes);
        pizzas = novoPedido;

        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .pizzas(pizzas)
                .build());

    }

    @AfterEach
    void tearDown(){

        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        saborRepository.deleteAll();

    }

    private Estabelecimento novoEstabelecimento(){

        Estabelecimento novoEstabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
                .nome("Joab 2")
                .codigoDeAcesso("123456")
                .associacoes(new ArrayList<>())
                .build());

        return novoEstabelecimento;

    }

    private Cliente novoCliente(){

        Cliente novoCliente = clienteRepository.save(Cliente.builder()
            .nome("Maria")
            .endereco("Rua 2")
            .codigoDeAcesso("123456")
            .build());

        return novoCliente;

    }

    private Pedido novoPedido(){

        Sabor sabor = saborRepository.save(Sabor.builder()
                .nomeSabor("Pomodoro")
                .tipoSabor("salgado")
                .precoGrande(60.00)
                .precoMedio(50.00)
                .estabelecimento(novoEstabelecimento())
                .build());
        List<Sabor> sabores = new ArrayList<>();
        sabores.add(sabor);
        Pizza pomodoro = new Pizza(sabores, false, true, 1);
        List<Pizza> listaPizzas = new ArrayList<>();
        listaPizzas.add(pomodoro);

        Pedido novoPedido = pedidoRepository.save(Pedido.builder()
                .cliente(novoCliente())
                .pizzas(pizzas)
                .build());

        return novoPedido;
        
    }

    @Nested
    class AcompanhamentoPedidoGetTests{

        @Test
        @Transactional
        @DisplayName("Verifica o acompanhamento de um pedido com o cliente")
        void testVerificaAcompanhamentoPedidoCliente() throws UnsupportedEncodingException, Exception{

            String responseJsonString = driver.perform(get("/v1/acompanhamento/"+pedido.getId()+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Acompanhamento resultado = objectMapper.readValue(responseJsonString, Pedido.class).getAcompanhamento();

            assertFalse(resultado.isPedidoConfirmado());
            assertFalse(resultado.isPedidoEmPreparacao());
            assertFalse(resultado.isPedidoPronto());
            assertFalse(resultado.isPedidoACaminho());
            assertFalse(resultado.isPedidoEntregue());
        }

        @Test
        @Transactional
        @DisplayName("Verifica o acompanhamento de um pedido com o estabelecimento")
        void testVerificaAcompanhamentoPedidoEstabelecimento() throws UnsupportedEncodingException, Exception{

            String responseJsonString = driver.perform(get("/v1/acompanhamento/"+pedido.getId()+"?codigoDeAcessoEstabelecimento="+estabelecimento.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Acompanhamento resultado = objectMapper.readValue(responseJsonString, Pedido.class).getAcompanhamento();

            assertFalse(resultado.isPedidoConfirmado());
            assertFalse(resultado.isPedidoEmPreparacao());
            assertFalse(resultado.isPedidoPronto());
            assertFalse(resultado.isPedidoACaminho());
            assertFalse(resultado.isPedidoEntregue());

            pedido.modificaAcompanhamento(true, 0);
            pedido.modificaAcompanhamento(true, 1);

            responseJsonString = driver.perform(get("/v1/acompanhamento/"+pedido.getId()+"?codigoDeAcessoEstabelecimento="+cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            resultado = objectMapper.readValue(responseJsonString, Pedido.class).getAcompanhamento();

            assertTrue(resultado.isPedidoConfirmado());
            assertTrue(resultado.isPedidoEmPreparacao());
            assertFalse(resultado.isPedidoPronto());
            assertFalse(resultado.isPedidoACaminho());
            assertFalse(resultado.isPedidoEntregue());
        }

        @Test
        @Transactional
        @DisplayName("Tenta verificar o acompanhamento de um pedido invalido")
        void testVerificaAcompanhamentoPedidoInvalido() throws UnsupportedEncodingException, Exception{

            MvcResult resposta = driver.perform(get("/v1/acompanhamento/"+(pedido.getId()+1L)+"?codigoDeAcesso="+cliente.getCodigoDeAcesso())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertTrue(resposta.getResolvedException() instanceof Exception);

        }

    }

    @Nested
    class AcompanhamentoPedidoPutTests{

        @Test
        @Transactional
        @DisplayName("Realiza uma atualizacao valida no acompanhamento de um pedido")
        void testAtualizacaoValidaCliente()throws Exception {

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();

            String respostaJson = driver.perform(put("/v1/acompanhamento/0/"+pedido.getId()+"?codigoDeAcessoCliente="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acompanhamentoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
                
            Acompanhamento resposta = objectMapper.readValue(respostaJson, Pedido.class).getAcompanhamento();

            assertTrue(resposta.isPedidoConfirmado());

        }

        @Test
        @Transactional
        @DisplayName("Cliente tenta atualizar o acompanhamento de um pedido com uma id errada")
        void testAtualizacaoInvalidaCliente()throws Exception {

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();

            String respostaJson = driver.perform(put("/v1/acompanhamento/0/"+(pedido.getId()+1L)+"?codigoDeAcessoCliente="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acompanhamentoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringtester = error.getErrors().contains("O pedido requisitado nao e valido.");

            assertTrue(errorStringtester);

        }

        @Test
        @Transactional
        @DisplayName("Atualizacao de acompanhamento de pedido realizada pelo estabelecimento")
        void testAtualizacaoValidaEstabelecimento() throws Exception {

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();

            pedido.modificaAcompanhamento(true, 0);

            String respostaJson = driver.perform(put("/v1/acompanhamento/1/"+pedido.getId()+"?codigoDeAcessoEstabelecimento="+estabelecimento.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acompanhamentoDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Acompanhamento resposta = objectMapper.readValue(respostaJson, Pedido.class).getAcompanhamento();

            assertTrue(resposta.isPedidoConfirmado());
            assertTrue(resposta.isPedidoEmPreparacao());

        }

        @Test
        @Transactional
        @DisplayName("Estabelecimento tenta atualizar o acompanhamento de um pedido com uma id errada")
        void testAtualizacaoInvalidaEstabelecimento()throws Exception {

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();

            pedido.modificaAcompanhamento(true, 0);

            String respostaJson = driver.perform(put("/v1/acompanhamento/1/"+(pedido.getId()+1L)+"?codigoDeAcessoEstabelecimento="+estabelecimento.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acompanhamentoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

                    CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

                    boolean errorStringtester = error.getErrors().contains("O pedido requisitado nao e valido.");
        
                    assertTrue(errorStringtester);

        }

        @Test
        @Transactional
        @DisplayName("")
        void testAtualizacaoLogicaInvalida() throws Exception {

            pedido.modificaAcompanhamento(true, 0);
            pedido.modificaAcompanhamento(true, 1);
            pedido.modificaAcompanhamento(true, 2);

            AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
                .idCliente(cliente.getId())
                .idEstabelecimento(estabelecimento.getId())
                .statusPedido(true)
                .build();

            String respostaJson = driver.perform(put("/v1/acompanhamento/0/"+(pedido.getId()+1L)+"?codigoDeAcessoCliente="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(acompanhamentoDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            
            CustomErrorType error = objectMapper.readValue(respostaJson, CustomErrorType.class);

            boolean errorStringtester = error.getErrors().contains("A operacao de mudanca de status nao pode ser realizada.");

            assertTrue(errorStringtester);
        }

    }

    @Nested
    class AcompanhamentoPedidoDeleteTests{

        @Test
        @Transactional
        @DisplayName("Exclusao de pedido valido mas ainda nao pronto")
        void testDeletaPedidoValidoNaoPronto() throws UnsupportedEncodingException, Exception{

            driver.perform(delete("/v1/acompanhamento/"+pedido.getId()+"?codigoDeAcessoCliente="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            
            assertEquals(0, pedidoRepository.findAll().size());
        }

        @Test
        @Transactional
        @DisplayName("Exclusao de pedido valido mas pronto para entrega")
        void testDeletaPedidoValidoPronto() throws UnsupportedEncodingException, Exception{

            pedido.modificaAcompanhamento(true, 0);
            pedido.modificaAcompanhamento(true, 1);
            pedido.modificaAcompanhamento(true, 2);

            driver.perform(delete("/v1/acompanhamento/"+pedido.getId()+"?codigoDeAcessoCliente="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            
            assertEquals(1, pedidoRepository.findAll().size());

        }

        @Test
        @Transactional
        @DisplayName("Exclusao de pedido com id errado")
        void testDeletaPedidoErrado() throws UnsupportedEncodingException, Exception{

            driver.perform(delete("/v1/acompanhamento/"+(pedido.getId()+1L)+"?codigoDeAcessoCliente="+cliente.getCodigoDeAcesso())
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();
            
            assertEquals(1, pedidoRepository.findAll().size());

        }

    }
    
}
