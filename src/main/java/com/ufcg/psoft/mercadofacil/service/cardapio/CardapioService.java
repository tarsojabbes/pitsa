package com.ufcg.psoft.mercadofacil.service.cardapio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.mercadofacil.controller.SaborV1Controller;
import com.ufcg.psoft.mercadofacil.model.Sabor;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardapioService {
    
    @Autowired
    SaborV1Controller saborV1Controller;

    public List<Sabor> cardapioCompleto(){

        return getSabores();

    }

    public List<Sabor> cardapioSaboresSalgados(){

        List<Sabor> cardapioCompleto = cardapioCompleto();
        List<Sabor> saboresSalgados = new ArrayList<>();

        for (Sabor s:cardapioCompleto){
            if (s.getTipoSabor().toLowerCase().contains("salgad")){
                saboresSalgados.add(s);
            }
        }

        return saboresSalgados;
    }

    public List<Sabor> cardapioSaboresDoces(){
        
        List<Sabor> cardapioCompleto = cardapioCompleto();
        List<Sabor> saboresDoces = new ArrayList<>();

        for (Sabor s:cardapioCompleto){
            if (s.getTipoSabor().toLowerCase().contains("doce")){
                saboresDoces.add(s);
            }
        }

        return saboresDoces;

    }

    private List<Sabor> getSabores(){

        return saborV1Controller.buscarTodosSabores().getBody();
        
    }

}
