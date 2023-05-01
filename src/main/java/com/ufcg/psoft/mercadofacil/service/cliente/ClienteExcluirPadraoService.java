package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.exception.ClienteNaoAutorizadoException;
import com.ufcg.psoft.mercadofacil.exception.ClienteNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteExcluirPadraoService implements ClienteExcluirService{
    @Autowired
    ClienteRepository clienteRepository;
    @Override
    public void excluir(Long id, String codigoDeAcesso) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new ClienteNaoAutorizadoException();
        }

        clienteRepository.delete(cliente);
    }
}
