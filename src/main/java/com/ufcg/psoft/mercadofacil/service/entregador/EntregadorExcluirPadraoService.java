package com.ufcg.psoft.mercadofacil.service.entregador;

import com.ufcg.psoft.mercadofacil.exception.EntregadorNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EntregadorNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.repository.EntregadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregadorExcluirPadraoService implements EntregadorExcluirService {

    @Autowired
    EntregadorRepository entregadorRepository;

    @Override
    public void excluir(Long id, String codigoDeAcesso) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(EntregadorNaoExisteException::new);
        // todo ver como permitir ao estabelecimento que remova um entregador, tb ver se a parte de o estabelecimento aprovar/recusar entregador é da us3 ou us4

        if (!entregador.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new EntregadorNaoAutorizadoException();
        }

        entregadorRepository.delete(entregador);
    }

}
