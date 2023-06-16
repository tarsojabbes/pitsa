package com.ufcg.psoft.mercadofacil.service.pedido;


import com.ufcg.psoft.mercadofacil.exception.MudancaDeStatusInvalidaException;
import com.ufcg.psoft.mercadofacil.exception.PedidoInvalidoException;
import com.ufcg.psoft.mercadofacil.model.Acompanhamento;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Pedido;
import com.ufcg.psoft.mercadofacil.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoIndicarProntoPadraoService implements PedidoIndicarProntoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    PedidoAtribuirEntregadorPadraoService pedidoAtribuirEntregadorPadraoService;

    @Override
    public Pedido indicarPedidoPronto(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoInvalidoException::new);

        if (pedido.getAcompanhamento().equals(Acompanhamento.PEDIDO_EM_PREPARO)) {
            pedido.setAcompanhamento(Acompanhamento.PEDIDO_PRONTO);
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);
            this.associarEntregador(pedidoAtualizado);
            return pedidoAtualizado;
        }
        throw new MudancaDeStatusInvalidaException();
    }

    private void associarEntregador(Pedido pedido){
        Estabelecimento estabelecimento = pedido.getEstabelecimento();
        if(estabelecimento.getEntregadoresDisponiveis().isEmpty()){
            estabelecimento.getPedidosEmEspera().add(pedido.getId());
        } else{
            Long idEntregador = estabelecimento.getEntregadoresDisponiveis().get(0);
            estabelecimento.getEntregadoresDisponiveis().remove(0);
            pedidoAtribuirEntregadorPadraoService.atribuirEntregador(pedido.getId(), idEntregador);
        }
    }

}
