package com.ufcg.psoft.mercadofacil.exception;

public class PedidoNaoExisteException extends MercadoFacilException {
    public PedidoNaoExisteException() {
        super("Pedido com id informado nao existe.");
    }
}
