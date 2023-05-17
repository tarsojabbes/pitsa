package com.ufcg.psoft.mercadofacil.exception;

public class ProdutoNaoExisteException extends MercadoFacilException {
    public ProdutoNaoExisteException() {
        super("O produto consultado não existe!");
    }
}
