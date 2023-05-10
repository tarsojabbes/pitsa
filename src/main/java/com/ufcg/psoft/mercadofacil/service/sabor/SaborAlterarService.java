
package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.dto.SaborPostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.model.Sabor;

@FunctionalInterface
public interface SaborAlterarService {

    public Sabor alterar(Long id, String codigoDeAcesso, SaborPostPutRequestDTO saborPostPutRequestDTO);
    
}