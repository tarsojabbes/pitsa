package com.ufcg.psoft.mercadofacil.exception;

public class MercadoFacilException extends RuntimeException {
    public MercadoFacilException() {
        super("Erro inesperado no Mercado Fácil!");
    }

    public MercadoFacilException(String message) {
        super(message);
    }
}
