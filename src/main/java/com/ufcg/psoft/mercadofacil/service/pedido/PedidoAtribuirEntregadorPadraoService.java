package com.ufcg.psoft.mercadofacil.service.pedido;

import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.service.associacao.AssociacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoAtribuirEntregadorPadraoService implements PedidoAtribuirEntregadorService {

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    AssociacaoService associacaoService;

    @Override
    public Pedido atribuirEntregador(Long idPedido, Long idEntregador) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(PedidoNaoExisteException::new);
        Entregador entregador = entregadorRepository.findById(idEntregador).orElseThrow(EntregadorNaoExisteException::new);

        if (associacaoService.buscarAssociacao(idEntregador,
                pedido.getEstabelecimento().getId(),
                pedido.getEstabelecimento().getCodigoDeAcesso()) == null) {
            throw new AssociacaoNaoExisteException();
        }

        if (pedido.getAcompanhamento().equals(Acompanhamento.PEDIDO_PRONTO)) {
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_EM_ROTA);
            pedido.setEntregador(entregador);
            return pedidoRepository.save(pedido);
        }

        throw new MudancaDeStatusInvalidaException();
    }
}
