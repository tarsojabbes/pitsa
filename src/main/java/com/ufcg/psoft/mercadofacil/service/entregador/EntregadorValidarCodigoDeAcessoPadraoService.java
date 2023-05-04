package com.ufcg.psoft.mercadofacil.service.entregador;

import com.ufcg.psoft.mercadofacil.model.Entregador;
import org.springframework.stereotype.Service;

@Service
public class EntregadorValidarCodigoDeAcessoPadraoService implements EntregadorValidarCodigoDeAcessoService {
    @Override
    public boolean validarCodigoDeAcesso(Entregador entregador, String codigoDeAcesso) {
        return entregador.getCodigoDeAcesso().equals(codigoDeAcesso);
    }
}
