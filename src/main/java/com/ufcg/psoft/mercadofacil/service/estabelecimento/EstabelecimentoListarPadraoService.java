package com.ufcg.psoft.mercadofacil.service.estabelecimento;

import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EstabelecimentoListarPadraoService implements EstabelecimentoListarService {
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;


    @Override
    public List<Estabelecimento> listar(Long id) {
        if (id != null && id > 0) {
            Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoExisteException::new);
            List<Estabelecimento> list = new ArrayList<Estabelecimento>();
            list.add(estabelecimento);
            return list;
        } return estabelecimentoRepository.findAll();
    }
}
