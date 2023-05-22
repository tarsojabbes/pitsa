package com.ufcg.psoft.mercadofacil.exception;

public class PizzaInvalidaException extends MercadoFacilException{
    
    public PizzaInvalidaException(){
        super("A pizza requisitada nao pode ser criada.");
    }

}
