package com.ufcg.psoft.mercadofacil.exception;

public class AssociacaoNaoExisteException extends MercadoFacilException {
    public AssociacaoNaoExisteException() {
        super("A associacao consultada nao existe!");
    }
}
