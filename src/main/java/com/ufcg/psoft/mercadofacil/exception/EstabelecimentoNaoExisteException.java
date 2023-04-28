package com.ufcg.psoft.mercadofacil.exception;

public class EstabelecimentoNaoExisteException extends MercadoFacilException{
    public EstabelecimentoNaoExisteException() {
        super("O estabelecimento consultado não existe!");
    }
}
