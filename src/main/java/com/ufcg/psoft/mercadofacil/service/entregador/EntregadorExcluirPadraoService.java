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

        if (!entregador.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new EntregadorNaoAutorizadoException();
        }

        entregadorRepository.delete(entregador);
    }

}
