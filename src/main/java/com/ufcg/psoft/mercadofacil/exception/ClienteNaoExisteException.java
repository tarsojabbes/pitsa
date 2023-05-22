package com.ufcg.psoft.mercadofacil.exception;

public class ClienteNaoExisteException extends MercadoFacilException {
    public ClienteNaoExisteException() {
        super("O cliente consultado nao existe!");
    }
}
