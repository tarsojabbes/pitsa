package com.ufcg.psoft.mercadofacil.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.model.Pizza;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.pedido.AcompanhamentoPedidoService;

public class AcompanhamentoPedidoServiceTests {
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    AcompanhamentoPedidoService acompanhamentoPedidoService;

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

    @Test
    @DisplayName("Realiza uma operacao valida de acompanhamento de pedido")
    void testArgumentosValidos(){

    }

    @Test
    @DisplayName("Tenta modificar o acompanhamento de um pedido não associado ao cliente")
    void testPedidoClienteErrado(){

    }

    @Test
    @DisplayName("Tenta modificar o acompanhamento de um pedido não associado ao estabelecimento")
    void testPedidoEstabelecimentoErrado(){

    }

    @Test
    @DisplayName("Cliente tenta modificar o acompanhamento de um um pedido mas informa senha errada")
    void testCodigoDeAcessoClienteErrado(){

    }

    @Test
    @DisplayName("Estabelecimento tenta modificar o acompanhamento de um pedido mas informa senha errada")
    void testCodigoDeAcessoEstabelecimentoErrado(){

    }

    @Test
    @DisplayName("Indicador de andamento invalido (negativo)")
    void testAndamentoInvalidoNegativo(){

    }

    @Test
    @DisplayName("Indicador de andamento invalido (maior que 4)")
    void testAndamentoInvalidoMaiorQueQuatro(){

    }

    @Test
    @DisplayName("Cliente tenta cancelar pedido pronto")
    void testCancelamentoPedidoPronto(){

    }

    @Test
    @DisplayName("Cliente errado tenta modificar andamento de pedido ainda nao pronto")
    void testIdClienteErrado(){

    }

    @Test
    @DisplayName("Estabelecimento errado tenta modificar andamento de pedido")
    void testIdEstabelecimentoErrado(){

    }

}
