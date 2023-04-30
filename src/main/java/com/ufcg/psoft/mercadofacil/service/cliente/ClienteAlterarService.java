package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Cliente;

@FunctionalInterface
public interface ClienteAlterarService {
    public Cliente alterar(Long id, String codigoDeAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO);
}
