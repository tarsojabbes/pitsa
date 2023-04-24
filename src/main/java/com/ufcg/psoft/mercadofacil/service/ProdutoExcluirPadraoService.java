package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.exception.ProdutoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Produto;
import com.ufcg.psoft.mercadofacil.repository.ProdutoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoExcluirPadraoService implements ProdutoExcluirService {
    @Autowired
    ProdutoRepository produtoRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow(ProdutoNaoExisteException::new);
        produtoRepository.delete(produto);
    }
}
