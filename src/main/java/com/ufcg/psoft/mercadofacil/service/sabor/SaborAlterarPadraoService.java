package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborAlterarPadraoService implements SaborAlterarService {
    
    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Sabor alterar(Long id, String codigoDeAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO) {

       Sabor sabor = saborRepository.findById(id).orElseThrow(SaborNaoExisteException::new);
       Estabelecimento estabelecimento = estabelecimentoRepository.findById(saborPostPutRequestDTO.getIdEstabelecimento()).orElseThrow(EstabelecimentoNaoExisteException::new);

       if (!codigoDeAcesso.equals(estabelecimento.getCodigoDeAcesso())) {
        throw new EstabelecimentoNaoAutorizadoException();
       }

       sabor.setNomeSabor(saborPostPutRequestDTO.getNomeSabor());
       sabor.setTipoSabor(saborPostPutRequestDTO.getTipoSabor());
       sabor.setEstabelecimento(saborPostPutRequestDTO.getEstabelecimento());
       sabor.setPrecoGrande(saborPostPutRequestDTO.getPrecoGrande());
       sabor.setPrecoMedio(saborPostPutRequestDTO.getPrecoMedio());


       return saborRepository.save(sabor);
       
    }
}