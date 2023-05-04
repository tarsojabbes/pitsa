package com.ufcg.psoft.mercadofacil.service.entregador;

import com.ufcg.psoft.mercadofacil.model.Entregador;

@FunctionalInterface
public interface EntregadorValidarCodigoDeAcessoService {

    public boolean validarCodigoDeAcesso(Entregador entregador, String codigoDeAcesso);
}
