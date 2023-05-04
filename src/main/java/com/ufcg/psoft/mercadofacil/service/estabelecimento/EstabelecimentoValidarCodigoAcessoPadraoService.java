package com.ufcg.psoft.mercadofacil.service.estabelecimento;

import com.ufcg.psoft.mercadofacil.model.Estabelecimento;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoValidarCodigoAcessoPadraoService implements EstabelecimentoValidarCodigoAcessoService{
    @Override
    public boolean validarCodigoDeAcesso(Estabelecimento estabelecimento, String codigoDeAcesso) {
        return estabelecimento.getCodigoDeAcesso().equals(codigoDeAcesso);
    }
}
