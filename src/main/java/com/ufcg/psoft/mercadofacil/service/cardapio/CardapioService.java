package com.ufcg.psoft.mercadofacil.service.cardapio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.mercadofacil.model.Sabor;
import com.ufcg.psoft.mercadofacil.service.sabor.SaborListarService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardapioService {
    
    @Autowired
    SaborListarService saborListarService;

    public List<Sabor> cardapioCompleto(Long idEstabelecimento){
        List<Sabor> sabores = saborListarService.listar(null, idEstabelecimento);
        List<Sabor> saboresOrdenados = new ArrayList<Sabor>();

        // Adiciona todos os sabores disponíveis primeiro
        for (Sabor s : sabores) {
            if (s.getDisponivel()) {
                saboresOrdenados.add(s);
            }
        }

        for (Sabor s : sabores) {
            if (!saboresOrdenados.contains(s)) {
                saboresOrdenados.add(s);
            }
        }

        return saboresOrdenados;
    }

    public List<Sabor> cardapioSaboresSalgados(Long idEstabelecimento){

        List<Sabor> cardapioCompleto = cardapioCompleto(idEstabelecimento);
        List<Sabor> saboresSalgados = new ArrayList<>();

        for (Sabor s:cardapioCompleto){
            if (s.getTipoSabor().toLowerCase().contains("salgado")){
                saboresSalgados.add(s);
            }
        }

        return saboresSalgados;
    }

    public List<Sabor> cardapioSaboresDoces(Long idEstabelecimento){
        
        List<Sabor> cardapioCompleto = cardapioCompleto(idEstabelecimento);
        List<Sabor> saboresDoces = new ArrayList<>();

        for (Sabor s:cardapioCompleto){
            if (s.getTipoSabor().toLowerCase().contains("doce")){
                saboresDoces.add(s);
            }
        }

        return saboresDoces;

    }

}
