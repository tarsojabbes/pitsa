package com.ufcg.psoft.mercadofacil.service.associacao;

import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.model.Associacao;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;


public interface AssociacaoService {
    public Associacao associarEntregadorEstabelecimento(Long entregadorId, Long estabelecimentoId, String codigoAcessoEntregador);

    public Associacao aceitarAssociacao(Long id, String codigoDeAcessoEstabelecimento);

    public void recusarAssociacao(Long id, String codigoDeAcessoEstabelecimento);

    public Associacao buscarAssociacao(Long entregadorId, Long estabelecimentoId, String codigoAcessoEstabelecimento);
}
