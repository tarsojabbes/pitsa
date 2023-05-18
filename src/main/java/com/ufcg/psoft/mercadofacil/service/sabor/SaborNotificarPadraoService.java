package com.ufcg.psoft.mercadofacil.service.sabor;

import com.ufcg.psoft.mercadofacil.exception.SaborNaoExisteException;
import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.repository.SaborRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaborNotificarPadraoService implements SaborNotificarService{
    @Autowired
    SaborRepository saborRepository;

    @Override
    public List<String> notificar(Long id) {
        Sabor sabor = saborRepository.findById(id).orElseThrow(SaborNaoExisteException::new);

        List<Integer> interessados = sabor.getInteressados();
        List<String> saida = new ArrayList<String>();

        if (!interessados.isEmpty()) {
            for (Integer interessado : interessados) {
                String notificacao = "Notificando cliente de ID " + interessado + " sobre disponibilidade de sabor";
                System.out.println(notificacao);
                saida.add(notificacao);
            }

            sabor.setInteressados(new ArrayList<Integer>());
            return saida;
        }

        return saida;
    }
}
