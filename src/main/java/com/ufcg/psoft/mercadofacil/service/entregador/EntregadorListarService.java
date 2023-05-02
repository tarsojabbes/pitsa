package com.ufcg.psoft.mercadofacil.service.entregador;

import com.ufcg.psoft.mercadofacil.dto.EntregadorGetResponseDTO;

import java.util.List;

@FunctionalInterface
public interface EntregadorListarService {
    List<EntregadorGetResponseDTO> listar(Long id);
}
