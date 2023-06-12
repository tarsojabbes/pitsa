package com.ufcg.psoft.mercadofacil.exception;

public class EntregadorIndisponivelException extends MercadoFacilException {
    public EntregadorIndisponivelException() {
        super("O entregador nao esta disponivel!");
    }
}
