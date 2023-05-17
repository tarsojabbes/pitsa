package com.ufcg.psoft.mercadofacil.exception;

public class ClienteNaoAutorizadoException extends MercadoFacilException{
    public ClienteNaoAutorizadoException() {
        super("O cliente nao possui permissao para alterar dados de outro cliente");
    }
}
