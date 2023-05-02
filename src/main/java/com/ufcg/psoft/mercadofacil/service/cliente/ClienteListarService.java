package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;

import java.util.List;

@FunctionalInterface
public interface ClienteListarService {
    List<ClienteGetResponseDTO> listar(Long id);
}
