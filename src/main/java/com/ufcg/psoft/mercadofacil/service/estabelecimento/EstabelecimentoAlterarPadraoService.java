package com.ufcg.psoft.mercadofacil.service.estabelecimento;

import com.ufcg.psoft.mercadofacil.dto.EstabelecimentoPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.CustomErrorType;
import com.ufcg.psoft.mercadofacil.exception.EstabelecimentoNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import com.ufcg.psoft.mercadofacil.repository.EstabelecimentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoAlterarPadraoService implements EstabelecimentoAlterarService {
    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Estabelecimento alterar(Long id, EstabelecimentoPostPutRequestDTO estabelecimentoPostPutRequestDTO) {
       Estabelecimento estabelecimento = estabelecimentoRepository.findById(id).orElseThrow(EstabelecimentoNaoExisteException::new);
       modelMapper.map(estabelecimentoPostPutRequestDTO, estabelecimento);
       return estabelecimentoRepository.save(estabelecimento);
    }
}
