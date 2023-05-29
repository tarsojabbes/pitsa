package com.ufcg.psoft.mercadofacil.service.pedido;


import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoIndicarProntoPadraoService implements PedidoIndicarProntoService {

    @Autowired
    PedidoRepository pedidoRepository;
    @Override
    public Pedido indicarPedidoPronto(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);

        if (pedido.getAcompanhamento().equals(Acompanhamento.PEDIDO_EM_PREPARO)) {
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
            return pedidoRepository.save(pedido);
        }
        throw new MudancaDeStatusInvalidaException();
    }

}
