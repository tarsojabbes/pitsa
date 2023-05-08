package com.ufcg.psoft.mercadofacil.exception;

public class EntregadorNaoExisteException extends MercadoFacilException {
    public EntregadorNaoExisteException() {
        super("O entregador consultado nao existe!");
    }
}
