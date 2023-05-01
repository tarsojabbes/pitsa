package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.model.Cliente;

import java.util.List;

@FunctionalInterface
public interface ClienteListarService {
    public List<ClienteGetResponseDTO> listar(Long id);
}
