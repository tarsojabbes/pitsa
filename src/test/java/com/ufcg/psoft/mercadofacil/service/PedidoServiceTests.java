package com.ufcg.psoft.mercadofacil.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.dto.PedidoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
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

    List<Pizza> pizzas;

    Cliente cliente;

    Estabelecimento estabelecimento;

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

        pedido = pedidoRepository.save(Pedido.builder()
            .cliente(cliente)
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

        Pizza duasCalabresasGrandes = new Pizza(sabores,false, sabor.getPrecoGrande(),2);
        List<Pizza> novoPedido = new ArrayList<>();
        novoPedido.add(duasCalabresasGrandes);

        return novoPedido;
    }

    @Nested
    public class PedidoCriarServiceTests{

        
        @Test
        @DisplayName("Criacao de Pedido valido em BD vazio")
        void testPedidoValidoBDVazio(){

            pedidoRepository.deleteAll();
            assertEquals(0, pedidoRepository.findAll().size());

            
            PedidoPostPutRequestDTO novoPedido = PedidoPostPutRequestDTO.builder()
                .idCLiente(cliente.getId())
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .pizzas(pizzas)
                .enderecoAlternativo("")
            .build();

            pedidoCriarService.criar(cliente.getCodigoDeAcesso(),novoPedido);

            assertEquals(1,pedidoRepository.findAll().size());

        }

        @Test
        @DisplayName("Criacao de Pedido valido em BD ja populado")
        void testPedidoValidoBDPopulado(){

            assertEquals(1, pedidoRepository.findAll().size());

            PedidoPostPutRequestDTO novoPedido = PedidoPostPutRequestDTO.builder()
                .idCLiente(cliente.getId())
                .codigoDeAcesso(cliente.getCodigoDeAcesso())
                .pizzas(pizzas)
            .build();

            pedidoCriarService.criar(novoPedido.getCodigoDeAcesso(),novoPedido);

            assertEquals(2,pedidoRepository.findAll().size());

        }

    }

    @Nested
    public class PedidoAlterarTests{

        @Test
        @DisplayName("Modifica um pedido válido")
        void testModificaPedido(){

            String novaRua = "Rua 2";
            PedidoPostPutRequestDTO pedidoModificado = PedidoPostPutRequestDTO.builder()
                    .idCLiente(cliente.getId())

                .enderecoAlternativo(novaRua)
                .build();

            pedidoAlterarService.alterar(pedido.getId(), cliente.getCodigoDeAcesso(), pedidoModificado);

            Optional<Pedido> pedidoAlterado = pedidoRepository.findById(pedido.getId());

            if(pedidoAlterado.isPresent()){
                assertEquals(novaRua,pedidoAlterado.get().getEndereco());
            }
            
        }

        @Test
        @DisplayName("Tenta modificar um pedido inexistente")
        void testRemovePizzaValida(){
            
            PedidoPostPutRequestDTO pedidoModificado = PedidoPostPutRequestDTO.builder()
                .enderecoAlternativo("Rua 2")
            .build();

            assertThrows(MercadoFacilException.class, () -> pedidoAlterarService.alterar(pedido.getId()+6L, cliente.getCodigoDeAcesso(), pedidoModificado).getClass());
        }

    }
    
    @Nested
    public class PedidoListarTests{

        @Test
        @Transactional
        @DisplayName("Lista um pedido de um cliente válido")
        void testListaPedidoClienteValido() throws Exception {
            List<Pedido> pedidos = pedidoListarService.listar(pedido.getId(), cliente.getCodigoDeAcesso());
            Pedido pedido1 = pedidos.get(0);

            assertEquals(pedido.getMeioDePagamento(), pedido1.getMeioDePagamento());
            assertEquals(pedido.getPizzasPedido(), pedido1.getPizzasPedido());
            assertEquals(pedido.getEndereco(), pedido1.getEndereco());

        }

        @Test
        @DisplayName("Tenta listar um pedido de cliente inválido")
        void testListaPedidoClienteInvalido(){
            
            assertThrows(MercadoFacilException.class, () -> pedidoListarService.listar(cliente.getId()+1L, cliente.getCodigoDeAcesso()));
            assertThrows(MercadoFacilException.class, () -> pedidoListarService.listar(cliente.getId(), cliente.getCodigoDeAcesso()+"789"));

        }
    }
    
    @Nested
    public class PedidoExcluirTests{

        @Test
        @DisplayName("Exclui um pedido corretamente a partir da id do cliente")
        void testExclusaoValidaPedido(){
            
            pedidoExcluirService.excluir(pedido.getId(), cliente.getCodigoDeAcesso());

            assertEquals(0,pedidoRepository.findAll().size());
        }

        @Test
        @DisplayName("Tenta excluir um pedido invalido")
        void testExclusaoInvalidaPedido(){
            
            assertEquals(1, pedidoRepository.findAll().size());

            assertThrows(MercadoFacilException.class, () -> pedidoExcluirService.excluir(pedido.getId()+2L, cliente.getCodigoDeAcesso()));

            assertEquals(1, pedidoRepository.findAll().size());

        }

    }
    
}
