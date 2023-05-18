package com.ufcg.psoft.mercadofacil.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.PizzaGrandeUmSabor;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoAlterarService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoCriarService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoExcluirService;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoListarService;

@SpringBootTest
public class PedidoServiceTests {
    
    @Autowired
    PedidoCriarService pedidoCriarService;

    @Autowired
    PedidoListarService pedidoListarService;

    @Autowired
    PedidoAlterarService pedidoAlterarService;

    @Autowired
    PedidoExcluirService pedidoExcluirService;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    Pedido pedido;

    Pizza pizza;

    Cliente cliente;

    Estabelecimento estabelecimento;

    @BeforeEach
    void setup(){

        estabelecimento = estabelecimentoRepository.save(Estabelecimento.builder()
            .nome("Jipao")
            .codigoDeAcesso("123456")
            .associacoes(new ArrayList<>())
        .build());

        Pizza pizza = new PizzaGrandeUmSabor(new Sabor(1L,"Calabresa", "Salgada", 50.00, 60.00, estabelecimento));
        Map<Pizza,Integer> pizzas = new HashMap<Pizza,Integer>();
        pizzas.put(pizza,2);

        cliente = clienteRepository.save(Cliente.builder()
            .nome("Joao")
            .endereco("Rua 1")
            .codigoDeAcesso("123456")
        .build());

        pedido = pedidoRepository.save(Pedido.builder()
            .cliente(cliente)
            .pizzasPedido(pizzas)
        .build()
        );
    }
    
    @AfterEach
    void tearDown(){
        clienteRepository.deleteAll();
        estabelecimentoRepository.deleteAll();
        pedidoRepository.deleteAll();
    }

    @Nested
    public class PedidoCriarServiceTests{

        @Test
        @DisplayName("Criacao de Pedido valido")
        void testPedidoValido(){
            //TODO
        }

        @Test
        @DisplayName("Criacao de Pedido invalido (Cliente null)")
        void testPedidoInvalidoClienteNull(){
            //TODO
        }

        @Test
        @DisplayName("Criacao de Pedido invalido (Pizza null)")
        void testPedidoInvalidoPizzaNull(){
            //TODO
        }

    }

        

    @Nested
    public class PedidoAlterarTests{

        @Test
        @DisplayName("Adiciona uma Pizza ao pedido")
        void testAdicionaPizza(){
            //TODO
        }

        @Test
        @DisplayName("Remove uma pizza existente do pedido")
        void testRemovePizzaValida(){
            //TODO
        }

        @Test
        @DisplayName("Altera a quantidade de uma pizza no pedido (aumento)")
        void testAumentaQuantidadePizza(){
            //TODO
        }

        @Test
        @DisplayName("Altera a quantidade de uma pizza (reducao)")
        void testReduzQuantidadePizza(){
            //TODO
        }

        @Test
        @DisplayName("Zera a quantidade de uma pizza existente")
        void testZeraPizzaExistente(){
            //TODO
        }

        @Test
        @DisplayName("Tenta remover uma pizza inexistente")
        void testPizzaInexistente(){
            //TODO
        }

        @Test
        @DisplayName("Tenta definir uma quantidade negativa de pizzas")
        void testPizzaNegativa(){
            //TODO
        }

        @Test
        @DisplayName("Tenta definir uma pizza com quantidade null")
        void testPizzaNull(){
            //TODO
        }

    }
    
    @Nested
    public class PedidoListarTests{

        @Test
        @DisplayName("Lista o pedido atual de um cliente válido")
        void testListaPedidoClienteValido(){
            //TODO
        }

        @Test
        @DisplayName("Tenta listar um pedido de cliente inválido (pedido)")
        void testListaPedidoClienteInvalido(){
            //TODO
        }
    }
    
    @Nested
    public class PedidoExluirTests{

        @Test
        @DisplayName("Exclui um pedido corretamente a partir da id do cliente")
        void testExclusaoValidaPedido(){
            //TODO
        }

        @Test
        @DisplayName("Tenta excluir o pedido de um cliente diferente")
        void testExclusaoInvalidaPedido(){
            //TODO
        }

    }
    
}
