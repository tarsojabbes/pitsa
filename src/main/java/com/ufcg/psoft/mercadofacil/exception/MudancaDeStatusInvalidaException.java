package com.ufcg.psoft.mercadofacil.exception;

public class MudancaDeStatusInvalidaException extends MercadoFacilException{
    
    public MudancaDeStatusInvalidaException(){

        super("A operacao de mudanca de status nao pode ser realizada.");

    }

}
