package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaborCriarPadraoService  implements SaborCriarService {
    
    @Autowired
    SaborRepository saborRepository;

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Sabor criar(String codigoDeAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(saborPostPutRequestDTO.getIdEstabelecimento()).orElseThrow(EstabelecimentoNaoExisteException::new);

        if (!estabelecimento.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new EstabelecimentoNaoAutorizadoException();
        }
        
        Sabor sabor = Sabor.builder()
        .estabelecimento(estabelecimento)
        .nomeSabor(saborPostPutRequestDTO.getNomeSabor())
        .tipoSabor(saborPostPutRequestDTO.getTipoSabor())
        .precoGrande(saborPostPutRequestDTO.getPrecoGrande())
        .precoMedio(saborPostPutRequestDTO.getPrecoMedio())
        .build();

        return saborRepository.save(sabor);
        
    }
}