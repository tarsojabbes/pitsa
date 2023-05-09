package com.ufcg.psoft.mercadofacil.exception;

public class EstabelecimentoNaoAutorizadoException extends MercadoFacilException {
    public EstabelecimentoNaoAutorizadoException() {
        super("O estabelecimento nao possui permissao para alterar dados de outro estabelecimento");
    }
}
