package com.ufcg.psoft.mercadofacil.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ufcg.psoft.mercadofacil.dto.AcompanhamentoPedidoDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.MercadoFacilException;
import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
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
    void testArgumentosValidos() throws InvalidAttributeValueException{

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

        acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 0);

        Acompanhamento acompanhamentoAlterado = pedidoRepository.findById(pedido.getId()).get().getAcompanhamento();

        assertTrue(acompanhamentoAlterado.isPedidoConfirmado());

    }

    @Test
    @DisplayName("Tenta modificar o acompanhamento de um pedido não associado ao cliente")
    void testPedidoClienteErrado(){

        Pedido novoPedido = novoPedido();

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(MercadoFacilException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(novoPedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 0));

    }

    @Test
    @DisplayName("Tenta modificar o acompanhamento de um pedido não associado ao estabelecimento")
    void testPedidoEstabelecimentoErrado(){

        Pedido novoPedido = novoPedido();

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(MercadoFacilException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(novoPedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 1));

    }

    @Test
    @DisplayName("Tenta modificar um pedido inexistente")
    void testModificaAcompanhamentoPedidoInexistente(){
        
        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(PedidoInvalidoException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId()+1L, true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 1));

    }

    @Test
    @DisplayName("Cliente tenta modificar o acompanhamento de um um pedido mas informa senha errada")
    void testCodigoDeAcessoClienteErrado(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(ClienteNaoAutorizadoException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso()+"789", acompanhamentoDTO, 0));

    }

    @Test
    @DisplayName("Estabelecimento tenta modificar o acompanhamento de um pedido mas informa senha errada")
    void testCodigoDeAcessoEstabelecimentoErrado(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(EstabelecimentoNaoAutorizadoException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso()+"789", acompanhamentoDTO, 0));


    }

    @Test
    @DisplayName("Indicador de andamento invalido (negativo)")
    void testAndamentoInvalidoNegativo(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(IllegalArgumentException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, -5));

    }

    @Test
    @DisplayName("Indicador de andamento invalido (maior que 4)")
    void testAndamentoInvalidoMaiorQueQuatro(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

            assertThrows(IllegalArgumentException.class,
                        () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 5));

    }

    @Test
    @DisplayName("Cliente tenta cancelar pedido pronto")
    void testCancelamentoPedidoPronto(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(false)
            .build();

        pedido.modificaAcompanhamento(true,0);
        pedido.modificaAcompanhamento(true,1);
        pedido.modificaAcompanhamento(true,2);

        assertThrows(MudancaDeStatusInvalidaException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 0));

    }

    @Test
    @DisplayName("Cliente errado tenta cancelar confirmacao de pedido ainda nao pronto")
    void testIdClienteErrado(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId()+1L)
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

        pedido.modificaAcompanhamento(true, 0);
        pedido.modificaAcompanhamento(true, 1);

        assertThrows(ClienteNaoAutorizadoException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 0));

    }

    @Test
    @DisplayName("Estabelecimento errado tenta modificar andamento de pedido")
    void testIdEstabelecimentoErrado(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId()+1L)
            .statusPedido(true)
            .build();

        pedido.modificaAcompanhamento(true, 0);
        pedido.modificaAcompanhamento(true, 1);

        assertThrows(EstabelecimentoNaoAutorizadoException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 2));

    }

    @Test
    @DisplayName("Informa valor inválido no DTO (inCLiente null)")
    void testDTOInvalidoIdClienteNull(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(null)
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

        assertThrows(MercadoFacilException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 0));

    }

    @Test
    @DisplayName("Informa valor inválido no DTO (idCliente negativa)")
    void testDTOInvalidoIdClienteNegativa(){
        
        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId()*-1)
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

        assertThrows(MercadoFacilException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 0));

    }

    @Test
    @DisplayName("Informa valor inválido no DTO (idEstabelecimento null)")
    void testDTOInvalidoIdEstabelecimentoNull(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(null)
            .statusPedido(true)
            .build();

        pedido.modificaAcompanhamento(true, 0);

        assertThrows(MercadoFacilException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 1));

    }

    @Test
    @DisplayName("Informa valor inválido no DTO (idEstabalecimento negativa)")
    void testDTOInvalidoIdEstabelecimentoNegativa(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId()*-1)
            .statusPedido(true)
            .build();

        pedido.modificaAcompanhamento(true, 0);

        assertThrows(MercadoFacilException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 1));

    }

    @Test
    @DisplayName("Tenta modificar o status de acompanhamento de um pedido de maneira ilegal.")
    void testModificacaoDeAcompanhamentoInvalida(){

        AcompanhamentoPedidoDTO acompanhamentoDTO = AcompanhamentoPedidoDTO.builder()
            .idCliente(cliente.getId())
            .idEstabelecimento(estabelecimento.getId())
            .statusPedido(true)
            .build();

        assertThrows(MudancaDeStatusInvalidaException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 1));

        assertThrows(MudancaDeStatusInvalidaException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 2));

        assertThrows(MudancaDeStatusInvalidaException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), true, estabelecimento.getCodigoDeAcesso(), acompanhamentoDTO, 3));
        
        assertThrows(MudancaDeStatusInvalidaException.class,
                    () -> acompanhamentoPedidoService.alteraAcompanhamento(pedido.getId(), false, cliente.getCodigoDeAcesso(), acompanhamentoDTO, 4));
    }

}
