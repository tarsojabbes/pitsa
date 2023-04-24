package com.ufcg.psoft.mercadofacil.service;

import com.ufcg.psoft.mercadofacil.dto.ProdutoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Produto;
import com.ufcg.psoft.mercadofacil.repository.ProdutoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoCriarPadraoService implements ProdutoCriarService {
    @Autowired
    ProdutoRepository produtoRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public Produto salvar(ProdutoPostPutRequestDTO produtoPostPutRequestDTO) {
        Produto produto = modelMapper.map(produtoPostPutRequestDTO, Produto.class);
        return produtoRepository.save(produto);
    }
}
