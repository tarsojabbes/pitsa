package com.ufcg.psoft.mercadofacil.exception;

public class EntregadorNaoAutorizadoException extends MercadoFacilException {
    public EntregadorNaoAutorizadoException() {
        super("O entregador nao possui permissao para alterar dados de outro entregador");
    }
}
