package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborExcluirPadraoService implements SaborExcluirService{
    
    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public void excluir(Long id, String codigoAcesso) {

        Sabor sabor = saborRepository.findById(id).orElseThrow(SaborNaoExisteException::new);
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(sabor.getEstabelecimento().getId()).orElseThrow(EstabelecimentoNaoExisteException::new);

        if (!codigoAcesso.equals(estabelecimento.getCodigoDeAcesso())) {
            throw new EstabelecimentoNaoAutorizadoException();
        }

        saborRepository.delete(sabor);
        
    }
}
