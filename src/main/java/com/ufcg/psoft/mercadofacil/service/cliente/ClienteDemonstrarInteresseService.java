package com.ufcg.psoft.mercadofacil.service.cliente;


@FunctionalInterface
public interface ClienteDemonstrarInteresseService {

    public void demonstrarInteressePorSabor(String codigoDeAcesso, Long clienteId, Long saborId);
}
