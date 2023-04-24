package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.exception.ProdutoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Produto;
import com.ufcg.psoft.mercadofacil.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoListarPadraoService implements ProdutoListarService {

    @Autowired
    ProdutoRepository produtoRepository;

    @Override
    public List<Produto> listar(Long id) {
        if(id!=null && id > 0) {
            produtoRepository.findById(id).orElseThrow(ProdutoNaoExisteException::new);
        }
        return produtoRepository.findAll();
    }
}
