package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class SaborListarPadraoService implements SaborListarService {

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public List<Sabor> listar(Long id, Long idEstabelecimento) {

        if (id != null && idEstabelecimento == null && id > 0L) {
            Sabor sabor = saborRepository.findById(id).orElseThrow(SaborNaoExisteException::new);
            List<Sabor> list = new ArrayList<>();
            list.add(sabor);

            return list;

        }

        if (id == null && idEstabelecimento != null) {
            return saborRepository.findByEstabelecimento(estabelecimentoRepository.findById(idEstabelecimento).orElseThrow(EstabelecimentoNaoExisteException::new));
        }
        
        return saborRepository.findAll();
        
    }

}