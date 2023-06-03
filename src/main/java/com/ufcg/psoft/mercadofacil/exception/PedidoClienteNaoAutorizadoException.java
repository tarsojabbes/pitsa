package com.ufcg.psoft.mercadofacil.exception;

public class PedidoClienteNaoAutorizadoException extends MercadoFacilException{
    public PedidoClienteNaoAutorizadoException() {
        super("O cliente nao possui permissao para acessar o pedido de outro cliente");
    }
}
