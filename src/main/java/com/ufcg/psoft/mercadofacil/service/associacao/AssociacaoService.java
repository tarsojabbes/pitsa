package com.ufcg.psoft.mercadofacil.service.associacao;

import com.ufcg.psoft.mercadofacil.model.Associacao;
import com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador;


public interface AssociacaoService {
    public Associacao associarEntregadorEstabelecimento(Long entregadorId, Long estabelecimentoId, String codigoAcessoEntregador);

    public Associacao aceitarAssociacao(Long id, String codigoDeAcessoEstabelecimento);

    public void recusarAssociacao(Long id, String codigoDeAcessoEstabelecimento);

    public Associacao buscarAssociacao(Long entregadorId, Long estabelecimentoId, String codigoAcessoEstabelecimento);

    public void alterarDisponibilidadeEntregador(Long entregadorId, DisponibilidadeEntregador disponibilidade, Long associacaoId, String codigoAcessoEntregador);
}
