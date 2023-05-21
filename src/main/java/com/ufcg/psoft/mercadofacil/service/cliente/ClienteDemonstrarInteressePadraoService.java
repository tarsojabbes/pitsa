package com.ufcg.psoft.mercadofacil.service.cliente;

import com.ufcg.psoft.mercadofacil.exception.*;
import com.ufcg.psoft.mercadofacil.model.Cliente;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.ClienteRepository;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborAlterarPadraoService;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborListarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteDemonstrarInteressePadraoService implements ClienteDemonstrarInteresseService {

    @Autowired
    SaborListarService saborListarService;

    @Autowired
    SaborAlterarPadraoService saborAlterarPadraoService;

    @Autowired
    SaborRepository saborRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public void demonstrarInteressePorSabor(String codigoDeAcesso, Long clienteId, Long saborId) {

        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Sabor sabor = saborRepository.findById(saborId).orElseThrow(SaborNaoExisteException::new);

        if (!cliente.getCodigoDeAcesso().equals(codigoDeAcesso)) {
            throw new ClienteNaoAutorizadoException();
        }

        if (sabor.getDisponivel()){
            throw new SaborDisponivelException();
        }

        sabor.getInteressados().add(clienteId);
        saborRepository.save(sabor);


    }
}
