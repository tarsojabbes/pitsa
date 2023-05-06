package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborCriarPadraoService  implements SaborCriarService {
    
    @Autowired
    SaborRepository saborRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Sabor criar(SaborPostPutRequestDTO saborPostPutRequestDTO) {

        Sabor sabor = modelMapper.map(saborPostPutRequestDTO, Sabor.class);

        return saborRepository.save(sabor);
        
    }
}