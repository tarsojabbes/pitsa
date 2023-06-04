package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.PedidoClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoListarHistoricoPadraoService implements PedidoListarHistoricoService{

    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public List<Pedido> listarHistorico(Long clienteId, String codigoDeAcesso) {
        List<Pedido> pedidos = pedidoRepository.findAll();

        // filtrando pedidos que são do cliente.
        pedidos = pedidos.stream()
                .filter(p -> p.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());

        // Verificando o codigo de acesso.
        // Filtra os pedidos com o código de acesso correto
        // Caso não encontre nenhum, quer dizer que o código é inválido
        Pedido pedido = pedidos.stream()
                .filter(p -> p.getCliente().getCodigoDeAcesso().equals(codigoDeAcesso))
                .findFirst()
                .orElseThrow(() -> new PedidoClienteNaoAutorizadoException());

        // Ordenando os pedidos.
        pedidos.sort((o1, o2) -> {

            if (!o1.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE) &&
                    o2.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE))
                return -1;

            if (o1.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE) &&
                    !o2.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE))
                return 1;


            return o2.getHorarioDoPedido().compareTo(o1.getHorarioDoPedido());
        });


        return pedidos;
    }
}
