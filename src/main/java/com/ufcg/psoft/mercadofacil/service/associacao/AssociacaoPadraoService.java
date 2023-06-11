package com.ufcg.psoft.mercadofacil.service.associacao;

import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.Associacao;
import com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.AssociacaoRepository;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ufcg.psoft.mercadofacil.model.DisponibilidadeEntregador.DESCANSO;

@Service
public class AssociacaoPadraoService implements AssociacaoService {

    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private EntregadorRepository entregadorRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public Associacao associarEntregadorEstabelecimento(Long entregadorId, Long estabelecimentoId, String codigoAcessoEntregador) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(EstabelecimentoNaoExisteException::new);
        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(EntregadorNaoExisteException::new);
        if (entregador.getCodigoDeAcesso().equals(codigoAcessoEntregador)) {
            Associacao associacao = Associacao.builder()
                    .entregador(entregador)
                    .estabelecimento(estabelecimento)
                    .statusAssociacao(false)
                    .build();

            return associacaoRepository.save(associacao);
        }

        throw new EntregadorNaoAutorizadoException();
    }

    @Override
    public Associacao aceitarAssociacao(Long id, String codigoDeAcessoEstabelecimento) {
        Associacao associacao = associacaoRepository.findById(id).orElseThrow(AssociacaoNaoExisteException::new);
        Estabelecimento estabelecimento = associacao.getEstabelecimento();
        if (estabelecimento.getCodigoDeAcesso().equals(codigoDeAcessoEstabelecimento)) {
            associacao.setStatusAssociacao(true);
            associacao.setDisponibilidadeEntregador(DESCANSO);
            return associacaoRepository.save(associacao);
        }

        throw new EstabelecimentoNaoAutorizadoException();
    }

    @Override
    public void recusarAssociacao(Long id, String codigoDeAcessoEstabelecimento) {
        Associacao associacao = associacaoRepository.findById(id).orElseThrow(AssociacaoNaoExisteException::new);
        Estabelecimento estabelecimento = associacao.getEstabelecimento();
        if (estabelecimento.getCodigoDeAcesso().equals(codigoDeAcessoEstabelecimento)) {
            associacaoRepository.deleteById(id);
        } else {
            throw new EstabelecimentoNaoAutorizadoException();
        }

    }

    // Função para ser usada no futuro.
    // Caso seja perdido o código de acesso de associacao
    // Por isso não está sendo testada no Estabelecimento Testes
    @Override
    public Associacao buscarAssociacao(Long entregadorId, Long estabelecimentoId, String codigoAcessoEstabelecimento) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(estabelecimentoId).orElseThrow(EstabelecimentoNaoExisteException::new);

        if (estabelecimento.getCodigoDeAcesso().equals(codigoAcessoEstabelecimento)) {
            return associacaoRepository.findByEntregadorIdAndEstabelecimentoId(entregadorId, estabelecimentoId);
        }

        return null;
    }

    @Override
    public void alterarDisponibilidadeEntregador(Long entregadorId, DisponibilidadeEntregador disponibilidade, Long associacaoId, String codigoAcessoEntregador) {

        Associacao associacao = associacaoRepository.findById(associacaoId).orElseThrow(AssociacaoNaoExisteException::new);
        Entregador entregadorAssociacao = associacao.getEntregador();
        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(EntregadorNaoExisteException::new);
        if (!entregadorAssociacao.equals(entregador)) {
            throw new EntregadorNaoAutorizadoException();
        } else if (!entregadorAssociacao.getCodigoDeAcesso().equals(codigoAcessoEntregador)) {
            throw new CodigoDeAcessoInvalidoException();
        }
        associacao.setDisponibilidadeEntregador(disponibilidade);
    }

}
