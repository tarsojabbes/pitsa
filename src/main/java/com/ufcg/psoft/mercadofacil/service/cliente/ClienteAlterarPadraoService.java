package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.dto.ClientePostPutRequestDTO;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteAlterarPadraoService implements ClienteAlterarService{
    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Cliente alterar(Long id, String codigoDeAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente clienteEncontrado = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);

        if (!clienteEncontrado.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new ClienteNaoAutorizadoException();
        }

        modelMapper.map(clientePostPutRequestDTO, clienteEncontrado);
        return clienteRepository.save(clienteEncontrado);

    }
}
