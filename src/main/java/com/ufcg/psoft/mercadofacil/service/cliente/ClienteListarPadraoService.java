package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.dto.ClienteGetResponseDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteListarPadraoService implements ClienteListarService {
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<ClienteGetResponseDTO> listar(Long id) {
        if (id == null) {
            List<Cliente> clientes = clienteRepository.findAll();
            return clientes.stream()
                    .map(cliente -> modelMapper.map(cliente, ClienteGetResponseDTO.class))
                    .collect(Collectors.toList());
        }
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);
        ClienteGetResponseDTO clienteGetResponseDTO = modelMapper.map(cliente, ClienteGetResponseDTO.class);
        List<ClienteGetResponseDTO> clientes = new ArrayList<ClienteGetResponseDTO>();
        clientes.add(clienteGetResponseDTO);
        return clientes;

    }
}
