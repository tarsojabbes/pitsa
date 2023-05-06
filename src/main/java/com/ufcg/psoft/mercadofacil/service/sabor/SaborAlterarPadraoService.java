package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborAlterarPadraoService implements SaborAlterarService {
    
    @Autowired
    SaborRepository saborRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Sabor alterar(Long id, SaborPostPutRequestDTO saborPostPutRequestDTO) {

       Sabor sabor = saborRepository.findById(id).orElseThrow(SaborNaoExisteException::new);
       modelMapper.map(saborPostPutRequestDTO, sabor);

       return saborRepository.save(sabor);
       
    }
}