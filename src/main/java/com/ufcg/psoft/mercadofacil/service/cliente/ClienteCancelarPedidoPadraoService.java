package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import com.ufcg.psoft.mercadofacil.service.pedido.PedidoExcluirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteCancelarPedidoPadraoService implements ClienteCancelarPedidoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    PedidoExcluirService pedidoExcluirService;

    @Override
    public void cancelarPedido(Long idPedido, String codigoDeAcesso) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(PedidoNaoExisteException::new);
        Cliente cliente = pedido.getCliente();

        if (!cliente.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new ClienteNaoAutorizadoException();
        }

        if (pedido.getAcompanhamento().equals(Acompanhamento.PEDIDO_PRONTO) ||
            pedido.getAcompanhamento().equals(Acompanhamento.PEDIDO_EM_ROTA)) {
            throw new MudancaDeStatusInvalidaException();
        }

        pedidoExcluirService.excluir(pedido.getId());
    }
}
