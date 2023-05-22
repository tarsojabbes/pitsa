package com.ufcg.psoft.mercadofacil.exception;

public class PedidoPrecoInvalidoException extends MercadoFacilException{

    public PedidoPrecoInvalidoException(){

        super("O preco do pedido requisitado nao e valido. Pizzas foram instanciadas com precos erroneos");

    }
}
