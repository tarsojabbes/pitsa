package com.ufcg.psoft.mercadofacil.service.estabelecimento;

import com.ufcg.psoft.mercadofacil.model.Entregador;
import com.ufcg.psoft.mercadofacil.model.Estabelecimento;

@FunctionalInterface
public interface EstabelecimentoValidarCodigoAcessoService {

    public boolean validarCodigoDeAcesso(Estabelecimento estabelecimento, String codigoDeAcesso);

}
