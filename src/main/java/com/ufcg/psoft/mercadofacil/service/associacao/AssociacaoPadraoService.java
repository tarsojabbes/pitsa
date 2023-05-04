package com.ufcg.psoft.mercadofacil.service.associacao;

import com.ufcg.psoft.mercadofacil.model.Associacao;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.AssociacaoRepository;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociacaoPadraoService implements AssociacaoService{

    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private EntregadorRepository entregadorRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public void associarEntregadorEstabelecimento(Long entregadorId, Long estabelecimentoId, String codigoAcessoEntregador) {
        Estabelecimento estabelecimento = estabelecimentoRepository.getById(estabelecimentoId);
        Entregador entregador = entregadorRepository.getById(entregadorId);

        if (entregador.getCodigoDeAcesso().equals(codigoAcessoEntregador)){
            Associacao associacao = Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .statusAssociacao(false)
                    .build();

            associacaoRepository.save(associacao);
        }
    }

    @Override
    public void aceitarAssociacao(Long id) {
        Optional<Associacao> optionalAssociacao = associacaoRepository.findById(id);
        if (optionalAssociacao.isPresent()) {
            Associacao associacao = optionalAssociacao.get();
            associacao.setStatusAssociacao(true);
            associacaoRepository.save(associacao);
        }
    }

    @Override
    public void recusarAssociacao(Long id) {
        associacaoRepository.deleteById(id);
    }

    @Override
    public Associacao buscarAssociacao(Long entregadorId, Long estabelecimentoId, String codigoAcessoEstabelecimento) {
        Estabelecimento estabelecimento = estabelecimentoRepository.getById(estabelecimentoId);

        if (estabelecimento.getCodigoDeAcesso().equals(codigoAcessoEstabelecimento)){
            return associacaoRepository.findByEntregadorIdAndEstabelecimentoId(entregadorId, estabelecimentoId);
        }

        return null;
    }
}
