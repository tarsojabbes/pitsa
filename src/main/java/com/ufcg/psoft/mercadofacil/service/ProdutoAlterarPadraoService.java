package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.ProdutoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ProdutoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Produto;
import com.ufcg.psoft.mercadofacil.repository.ProdutoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoAlterarPadraoService implements ProdutoAlterarService {
    @Autowired
    ProdutoRepository produtoRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public Produto alterar(Long id, ProdutoPostPutRequestDTO produtoPostPutRequestDTO) {
        Produto produto = produtoRepository.findById(id).orElseThrow(ProdutoNaoExisteException::new);
        modelMapper.map(produtoPostPutRequestDTO, produto);
        return produtoRepository.save(produto);
    }
}
