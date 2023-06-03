package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteConfirmarEntregaPadraoService implements ClienteConfirmarEntregaService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public Pedido confirmarPedidoEntregue(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);

        if (pedido.getAcompanhamento().equals(Acompanhamento.PEDIDO_EM_ROTA)) {
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_ENTREGUE);
            pedido.getEstabelecimento().notificarPedidoEntregue(pedido.getId());
            return pedidoRepository.save(pedido);
        }

        throw new MudancaDeStatusInvalidaException();
    }
}
