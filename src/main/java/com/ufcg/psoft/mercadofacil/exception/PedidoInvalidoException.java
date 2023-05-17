package com.ufcg.psoft.mercadofacil.exception;

public class PedidoInvalidoException extends MercadoFacilException{
    
    public PedidoInvalidoException(){

        super("O pedido requisitado nao e valido.");

    }
}
