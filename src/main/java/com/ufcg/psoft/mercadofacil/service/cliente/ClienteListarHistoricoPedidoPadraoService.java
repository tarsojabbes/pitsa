package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.mercadofacil.exception.PedidoClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteListarHistoricoPedidoPadraoService implements ClienteListarHistoricoPedidoService {

    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public List<Pedido> listarHistorico(Long clienteId, String codigoDeAcesso, Acompanhamento filtroDeAcompanhamento) {
        List<Pedido> pedidos = pedidoRepository.findAll();

        // filtrando pedidos que são do cliente.
        pedidos = pedidos.stream()
                .filter(p -> p.getCliente().getId().equals(clienteId))
                .collect(Collectors.toList());

        // Verificando o codigo de acesso.
        if (pedidos.size() != 0 && !pedidos.get(0).getCliente().getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        // Filtrando por estado
        if (filtroDeAcompanhamento != null) {
            pedidos = pedidos.stream()
                    .filter(p -> p.getAcompanhamento().equals(filtroDeAcompanhamento))
                    .collect(Collectors.toList());
        }

        ordenaPedidos(pedidos);


        return pedidos;
    }

    private void ordenaPedidos(List<Pedido> pedidos) {
        pedidos.sort((o1, o2) -> {

            if (!o1.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE) &&
                    o2.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE))
                return -1;

            if (o1.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE) &&
                    !o2.getAcompanhamento().equals(Acompanhamento.PEDIDO_ENTREGUE))
                return 1;


            return o2.getHorarioDoPedido().compareTo(o1.getHorarioDoPedido());
        });
    }
}
